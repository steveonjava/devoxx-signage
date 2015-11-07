/*
 * Devoxx digital signage project
 */
package devoxx;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Model object for conference Speaker.
 *
 * @author Jasper Potts
 */
public class Presentation {

    public final String id;
    public final String title;
    public final String room;
    public final LocalDateTime fromTime;
    public final LocalDateTime toTime;
    public final int length;

    public String summary;
    public Speaker[] speakers;
    public String track;
    public String type;

    /**
     * Constructor
     *
     * @param logger Where to log messages
     * @param id The id of the presentation
     * @param title The title of the presentation
     * @param room Which room the presentation is in
     * @param fromTime What time the presentation starts
     * @param toTime What time the presentation ends
     * @param length How long the presentation is
     */
    public Presentation(Logger logger, String id, String title, String room,
        LocalDateTime fromTime, LocalDateTime toTime, int length) {
        this.id = id;
        this.title = title;
        this.room = room;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.length = length;
    }

    /**
     * Add extended attributes to the speaker record
     *
     * @param summary Presentation summary
     * @param speakers Speakers for the presentation
     * @param track Which track the presentation is in
     * @param type What type of presentation it is
     */
    public void setExtended(String summary, Speaker[] speakers,
        String track, String type) {
        this.summary = summary;
        this.speakers = speakers;
        this.track = track;
        this.type = type;
    }

    /**
     * Create a nice readable string representation of the object
     *
     * @return A string containing the model data
     */
    @Override
    public String toString() {
        return "Presentation{" + "id=" + id
            + ", room=" + room
            + ", fromTime=" + fromTime
            + ", speakers=" + Arrays.toString(speakers)
            + '}';
    }
}
