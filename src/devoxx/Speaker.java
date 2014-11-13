package devoxx;

import javafx.scene.image.Image;

/**
 * Model object for conference Speaker.
 * 
 * @author Jasper Potts
 */
public class Speaker {
    public final String uuid;
    public final String fullName;
    public final String imageUrl;
    public Image image;

    public Speaker(String uuid, String fullName, String imageUrl) {
        this.uuid = uuid;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    @Override public String toString() {
        return fullName;
    }
}
