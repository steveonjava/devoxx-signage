/*
 * Devoxx digital signage project
 */
package devoxx;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javax.imageio.ImageIO;

/**
 * Model object for conference Speaker.
 *
 * @author Jasper Potts
 */
public class Speaker {

    public final String uuid;
    public final String fullName;
    public String imageUrlString;
    public Image photoImage;
    public ImageView photo;

    /**
     * Constructor
     *
     * @param logger Where to log messages
     * @param uuid Unique user ID
     * @param fullName The speaker's full name
     * @param downloadURL The URL for where the image comes from
     * @param cache Directory to use for image cache
     */
    public Speaker(Logger logger, String uuid, String fullName,
        String downloadURL, String cache) {
        logger.finer("New speaker: " + fullName);
        this.uuid = uuid;
        this.fullName = fullName;
        imageUrlString = downloadURL;

        String photoFileName = cache + uuid + ".jpg";

        /* Load the image from the cache if it's available, otherwise go out 
         * to the URL, load it and cache it.
         */
        if (Files.exists(Paths.get(photoFileName), LinkOption.NOFOLLOW_LINKS)) {
            logger.finest("Photo found in cache: " + photoFileName);

            try {
                photoImage = new Image(
                    new FileInputStream(photoFileName), 150, 150, true, true);
            } catch (FileNotFoundException ex) {
                logger.severe("FileNotFound error, which cannot happen!");
            }
        } else {
            logger.fine("Downloading photo for " + fullName);
            File cacheFile = new File(photoFileName);

            if (downloadURL.contains("\\")) {
                logger.warning("Image URL badly formed: " + downloadURL);
                logger.warning("Trying to fix this");
                imageUrlString = imageUrlString.replace("\\", "/");
            }

            photoImage = new Image(imageUrlString, 150, 150, true, true);

            try {
                URL imageURL = new URL(imageUrlString);

                try (DataInputStream photoInputStream
                    = new DataInputStream(imageURL.openStream());
                    FileOutputStream cacheFileOutputStream
                    = new FileOutputStream(cacheFile)) {
                    BufferedImage photoBufferedImage = ImageIO.read(photoInputStream);
                    ImageIO.write(photoBufferedImage, "jpg", cacheFileOutputStream);
                }
            } catch (Exception ioe) {
                logger.warning("Unable to read photo for " + fullName
                    + " from " + imageUrlString);
                logger.warning(ioe.getMessage());
            }
        }

        /* Create an ImageView for the Image */
        photo = new ImageView(photoImage);

        if (photoImage.getWidth() < photoImage.getHeight()) {
            photo.setFitWidth(150);
        } else {
            photo.setFitHeight(150);
        }

        photo.setPreserveRatio(true);
        photo.setClip(new Circle(75, 75, 75));
        logger.finest("Speaker added");
    }

    /**
     * Siumplified toString method to just return the full name
     *
     * @return The speaker's full name
     */
    @Override
    public String toString() {
        return fullName;
    }
}
