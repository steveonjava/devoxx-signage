package devoxx;

import java.util.Arrays;
import java.util.Date;

/**
 * Model object for conference Speaker.
 * 
 * @author Jasper Potts
 */
public class Presentation {
    public final String id;
    public final String title;
    public final String room;
    public final Date fromTime;
    public final Date toTime;
    public final int length;
    
    public String summary;
    public Speaker[] speakers;
    public String track;
    public String type;

    public Presentation(String id, String title, String room, Date fromTime, Date toTime, int length) {
        this.id = id;
        this.title = title;
        this.room = room;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.length = length;
    }

    public void setExtended(String summary, Speaker[] speakers, String track, String type) {
        this.summary = summary;
        this.speakers = speakers;
        this.track = track;
        this.type = type;
    }

    @Override public String toString() {
        return "Presentation{" + "id=" + id + ", room=" + room + ", fromTime=" + fromTime + ", speakers="+Arrays.toString(speakers) +'}';
    }
}
