package devoxx;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for loading all session data from devoxx server
 * 
 * @author Jasper Potts
 */
public class DataFetcher {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private Map<String,Speaker> speakerMap = new HashMap<>();
    private Map<String,Presentation> presentationMap = new HashMap<>();
    private final String appRoom;

    public DataFetcher(String room) {
        this.appRoom = room;
    }
    
    public List<Presentation> getData() {
        System.out.println("datafetcher getting data for " + appRoom);
        // GET ALL SPEAKERS FIRST
        String room = appRoom.split("\\s+")[0].toLowerCase() + appRoom.split("\\s+")[1];
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/speakers", "speakers.json");
            JSONParserJP.parse("speakers.json",new SpeakerCallcack());
        } catch (Exception e) { 
            e.printStackTrace(); return null;}
        System.out.println("");
        // THEN GET ALL Sessions (for Monday)
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/rooms/" + room + "/monday", "schedule-monday.json");
            JSONParserJP.parse("schedule-monday.json", new SessionCallcack());
        } catch (Exception e) { e.printStackTrace(); return null; }
        // THEN GET ALL Sessions (and Tuesday)
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/rooms/" + room + "/tuesday", "schedule-tuesday.json");
            JSONParserJP.parse("schedule-tuesday.json", new SessionCallcack());
        } catch (Exception e) { e.printStackTrace(); return null; }
        // THEN GET ALL Sessions (and Wednesday)
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/rooms/" + room + "/wednesday", "schedule-wednesday.json");
            JSONParserJP.parse("schedule-wednesday.json", new SessionCallcack());
        } catch (Exception e) { e.printStackTrace(); return null; }
        // THEN GET ALL Sessions (and Thursday)
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/rooms/" + room + "/thursday", "schedule-thursday.json");
            JSONParserJP.parse("schedule-thursday.json", new SessionCallcack());
        } catch (Exception e) { e.printStackTrace(); return null; }
        // THEN GET ALL Sessions (and Friday)
        try{
            JSONParserJP.download("http://cfp.devoxx.be/api/conferences/DevoxxBe2014/rooms/" + room + "/friday", "schedule-friday.json");
            JSONParserJP.parse("schedule-friday.json", new SessionCallcack());
        } catch (Exception e) { e.printStackTrace(); return null; }
        System.out.println("");
        
        System.out.println("");
        System.out.println("FOUND ["+speakerMap.size()+"] SPEAKERS");
        System.out.println("fetcher FOUND ["+presentationMap.size()+"] PRESENTATIONS");
        System.out.println("");
        
        // SORT PRESENTATIONS BY TIME
        List<Presentation> presentations = new ArrayList<>(presentationMap.values());
        Collections.sort(presentations, new Comparator<Presentation>() {
            @Override public int compare(Presentation o1, Presentation o2) {
                return o1.fromTime.compareTo(o2.fromTime);
            }
        });
        return presentations;
    }
    
    private class SpeakerCallcack extends JSONParserJP.CallbackAdapter {
        private String uuid;
        private String firstName;
        private String lastName;
        private String company;
        private String bio;
        private String imageUrl;
        private String twitter;
        private boolean rockStar = false;
        
        @Override public void keyValue(String key, String value, int depth) {
            if(depth == 2) {
                if ("uuid".equals(key)) {
                    uuid = value;
                } else if ("lastName".equals(key)) {
                    lastName = value;
                } else if ("firstName".equals(key)) {
                    firstName = value;
                } else if ("bio".equals(key)) {
                    bio = value;
                } else if ("company".equals(key)) {
                    company = value;
                } else if ("avatarURL".equals(key)) {
                    imageUrl = value;
                }
            }
        }
        @Override public void endObject(String objectName, int depth) {
            if(depth == 1) {
                Speaker speaker = speakerMap.get(uuid);
                if (speaker == null) {
                    speaker = new Speaker(uuid,firstName+" "+lastName,imageUrl);
                    speakerMap.put(uuid,speaker);
                }
            }
        }
    }
        
    private class SessionCallcack extends JSONParserJP.CallbackAdapter {
        public String id;
        private String summary;
        private List<Speaker> speakers = new ArrayList<>();
        private String type;
        private String track;
        private String title;
        private String room;
        private Date start,end;
        private int length;
        private int count = 0;

        @Override public void keyValue(String key, String value, int depth) {
            if (depth == 4 && "id".equals(key)) {
                id = value;
            } else if (depth == 4 && "summary".equals(key)) {
                summary = value;
            } else if (depth == 4 && "track".equals(key)) {
                track = value;
            } else if (depth == 4 && "talkType".equals(key)) {
                type = value;
            } else if (depth == 7 && "href".equals(key)) {
                Speaker speaker = speakerMap.get(value.substring(value.lastIndexOf('/') + 1));
                if (speaker == null) {
                    System.out.println("Failed to load: " + value.substring(value.lastIndexOf('/') + 1));
                } else {
                    speakers.add(speaker);
                }
            } else if (depth == 3 && "roomName".equals(key)) {
                room = value;
            } else if (depth == 4 && "title".equals(key)) {
                title = value;
            } else if (depth == 3 && "fromTime".equals(key)) {
                try {
                    start = DATE_FORMAT.parse(value);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } else if (depth == 3 && "toTime".equals(key)) {
                try {
                    end = DATE_FORMAT.parse(value);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } else if (depth == 3 && "day".equals(key)) {
                start.setYear(2014 - 1900);
                start.setMonth(11 - 1);
                end.setYear(2014 - 1900);
                end.setMonth(11 - 1);
                switch (value) {
                    case "monday":
                        start.setDate(10);
                        end.setDate(10);
                        break;
                    case "tuesday":
                        start.setDate(11);
                        end.setDate(11);
                        break;
                    case "wednesday":
                        start.setDate(12);
                        end.setDate(12);
                        break;
                    case "thursday":
                        start.setDate(13);
                        end.setDate(13);
                        break;
                    case "friday":
                        start.setDate(14);
                        end.setDate(14);
                        break;
                    default:
                        throw new IllegalStateException("unknown day: " + value);
                }
            }
        }
        
        @Override public void endObject(String objectName, int depth) {
            if(depth == 2 && title != null) {
                length = (int)((end.getTime()/60000) - (start.getTime()/60000));
                Presentation presentation = new Presentation(id, title, room, start, end, length);
                presentationMap.put(
                    id,
                    presentation
                );
                presentation.setExtended(summary, speakers.toArray(new Speaker[speakers.size()]), track, type);
            }
        }

        @Override public void startObject(String objectName, int depth) {
            if(depth == 2) {
                speakers.clear();
            }
        }
    }
    
}
