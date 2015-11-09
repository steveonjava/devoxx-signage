/*
 * Devoxx digital signage project
 */
package devoxx;

import devoxx.JSONParserJP.CallbackAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class for loading all session data from devoxx server
 *
 * @author Jasper Potts
 */
public class DataFetcher {

  private static final String[] day = {
    "monday", "tuesday", "wednesday", "thursday", "friday"
  };

//  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
  private final Map<String, Speaker> speakerMap = new HashMap<>();
  private final Map<String, Presentation> presentationMap = new HashMap<>();
  private final List<Presentation> presentations = new ArrayList<>();
  private final Logger logger;
  private final String room;
  private final String devoxxHost;
  private final LocalDate startDate;
  private final String imageCache;

  /**
   * Constructor
   *
   * @param logger Where to log messages to
   * @param controlProperties control properties
   * @param room Which room to get data for
   */
  public DataFetcher(Logger logger, ControlProperties controlProperties,
      String room) {
    this.logger = logger;
    this.room = room;
    devoxxHost = controlProperties.getDevoxxHost();
    imageCache = controlProperties.getImageCache();
    startDate = controlProperties.getStartDate();
  }

  /**
   * Get the list of presentations for the chosen room
   *
   * @return
   */
  public List<Presentation> getPresentationList() {
    return presentations;
  }

  /**
   * Try to update the data from the Devoxx CFP web service
   *
   * @return Whether the update suceeded or failed
   */
  public boolean updateData() {
    logger.fine("Retrieving data for room " + room);

    /* First get all the speakers data */
    String dataUrl;

    try {
      logger.finer("Retrieving speaker data...");
      dataUrl = devoxxHost + "speakers";
      JSONParserJP.download(logger, dataUrl, "speakers.json");
      JSONParserJP.parse(logger, "speakers.json", new SpeakerCallcack());
    } catch (Exception e) {
      logger.severe("Failed to retrieve speaker data!");
      logger.severe(e.getMessage());
      return false;
    }

    logger.info("Found [" + speakerMap.size() + "] SPEAKERS");

    /* Now retrieve all the session data for the week */
    for (int i = 0; i < 5; i++) {
      try {
        logger.finer("Retrieving data for " + day[i]);
        dataUrl = devoxxHost + "rooms/" + room + "/" + day[i];
        logger.finest(day[i] + " URL = " + dataUrl);
        String jsonString = "schedule-" + day[i] + ".json";
        JSONParserJP.download(logger, dataUrl, jsonString);
        JSONParserJP.parse(logger, jsonString, new SessionCallcack());
      } catch (Exception e) {
        logger.severe("Failed to retrieve schedule for " + day[i]);
        logger.severe(e.getMessage());
      }
    }

    if (presentationMap.isEmpty()) {
      logger.severe("Error: No presentation data downloaded!");
      return false;
    }

    logger.info("Found [" + presentationMap.size() + "] PRESENTATIONS\n");

    /* Sort the presentation by time.  I'm not sure this is really
     * necessary given the size of the data set (SR)
     */
    presentations.addAll(presentationMap.values());
    Collections.sort(presentations,
        (s1, s2) -> s1.fromTime.compareTo(s2.fromTime));
    return true;
  }

  /**
   * Callback class for handling Devoxx speaker JSON data
   */
  private class SpeakerCallcack extends CallbackAdapter {

    private String uuid;
    private String firstName;
    private String lastName;
    private String company;
    private String bio;
    private String imageUrl;
    private String twitter;
    private boolean rockStar;

    /**
     * Key value pair detected in the JSON data
     *
     * @param key The key
     * @param value The value
     * @param depth The depth of the key/value
     */
    @Override
    public void keyValue(String key, String value, int depth) {
      if (depth == 2) {
        if (null != key) {
          switch (key) {
            case "uuid":
              uuid = value;
              break;
            case "lastName":
              lastName = value;
              break;
            case "firstName":
              firstName = value;
              break;
            case "bio":
              bio = value;
              break;
            case "company":
              company = value;
              break;
            case "avatarURL":
              imageUrl = value;
              break;
          }
        }
      }
    }

    /**
     * Indicates that the parser ran into end of object '}'
     *
     * @param objectName if this object is value of key/value pair the this is
     * the key name, otherwise its null
     * @param depth The current depth, number of parent objects and arrays that
     * contain this object
     */
    @Override
    public void endObject(String objectName, int depth) {
      logger.finest("End speaker object found: " + firstName + " " + lastName);

      if (depth == 1) {
        Speaker speaker = speakerMap.get(uuid);

        if (speaker == null) {
          logger.finest("Speaker is null, adding new speaker");
          speaker = new Speaker(logger, uuid, firstName + " " + lastName,
              imageUrl, imageCache);
          speaker.cachePhoto();
          speakerMap.put(uuid, speaker);
        }
      }
    }
  }

  /**
   * Callback class for handling Devoxx session JSON data
   */
  private class SessionCallcack extends CallbackAdapter {

    private final List<Speaker> speakers = new ArrayList<>();
    public String id;
    private String summary;
    private String type;
    private String track;
    private String title;
    private String room;
    private LocalDateTime start;
    private LocalDateTime end;
    private int length;

    /**
     * Key value pair detected in the JSON data
     *
     * @param key The key
     * @param value The value
     * @param depth The depth of the key/value
     */
    @Override
    public void keyValue(String key, String value, int depth) {
      if (depth == 4 && "id".equals(key)) {
        id = value;
      } else if (depth == 4 && "summary".equals(key)) {
        summary = value;
      } else if (depth == 4 && "track".equals(key)) {
        track = value;
      } else if (depth == 4 && "talkType".equals(key)) {
        type = value;
      } else if (depth == 7 && "href".equals(key)) {
        Speaker speaker
            = speakerMap.get(value.substring(value.lastIndexOf('/') + 1));

        if (speaker == null) {
          logger.finer("Failed to load: "
              + value.substring(value.lastIndexOf('/') + 1));
        } else {
          speakers.add(speaker);
        }
      } else if (depth == 3 && "roomName".equals(key)) {
        room = value;
      } else if (depth == 4 && "title".equals(key)) {
        title = value;
        logger.finest("Title = " + title);
      } else if (depth == 3 && "fromTime".equals(key)) {
        logger.finest("Session start time = " + value);
        LocalTime startTime
            = LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
        start = LocalDateTime.of(startDate, startTime);
      } else if (depth == 3 && "toTime".equals(key)) {
        logger.finest("Session end time = " + value);
        LocalTime startTime
            = LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
        end = LocalDateTime.of(startDate, startTime);
      } else if (depth == 3 && "day".equals(key)) {
        logger.finest("Day = " + value);

        /**
         * Process which day it is.
         */
        switch (value) {
          case "monday":
            break;
          case "tuesday":
            start = start.plusDays(1);
            end = end.plusDays(1);
            break;
          case "wednesday":
            start = start.plusDays(2);
            end = end.plusDays(2);
            break;
          case "thursday":
            start = start.plusDays(3);
            end = end.plusDays(3);
            break;
          case "friday":
            start = start.plusDays(4);
            end = end.plusDays(4);
            break;
          default:
            throw new IllegalStateException("unknown day: " + value);
        }
      }
    }

    /**
     * Indicates that the parser ran into end of object '}'
     *
     * @param objectName if this object is value of key/value pair the this is
     * the key name, otherwise its null
     * @param depth The current depth, number of parent objects and arrays that
     * contain this object
     */
    @Override
    public void endObject(String objectName, int depth) {
      if (depth == 2 && title != null) {
        /* XXX LETS COME BACK AND FIGURE THIS OUT LATER */
        length = 0;

        Presentation presentation
            = new Presentation(logger, id, title, room, start, end, length);
        presentationMap.put(
            id,
            presentation);
        presentation.setExtended(
            summary,
            speakers.toArray(new Speaker[speakers.size()]),
            track,
            type);
      }
    }

    /**
     * Indicates that the parser ran into start of object '{'
     *
     * @param objectName if this object is value of key/value pair the this is
     * the key name, otherwise its null
     * @param depth The current depth, number of parent objects and arrays that
     * contain this object
     */
    @Override
    public void startObject(String objectName, int depth) {
      if (depth == 2) {
        speakers.clear();
      }
    }
  }
}
