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
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;

/**
 * Model object for conference Speaker.
 *
 * @author Jasper Potts
 */
public class Speaker {
    private final Logger logger;

    public final String uuid;
    public final String fullName;
    public String downloadURL;
    public Image photoImage;
    private final String cache;

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
        this.logger = logger;
        this.uuid = uuid;
        this.fullName = fullName;
        this.downloadURL = downloadURL;
        this.cache = cache;

    }
    
    public ImageView getPhoto() {
        ImageView photo;
        
        String photoFileName = cache + File.separatorChar + uuid + ".jpg";

        logger.finer("New speaker: " + fullName);
        /* Load the image from the cache if it's available, otherwise go out 
         * to the URL, load it and cache it.
         */
        if (Files.exists(Paths.get(photoFileName), LinkOption.NOFOLLOW_LINKS)) {
            logger.finest("Photo found in cache: " + photoFileName);

            try {
                photoImage = new Image(new FileInputStream(photoFileName));
            } catch (FileNotFoundException ex) {
                logger.severe("FileNotFound error, which cannot happen!");
            }
        } else {
            logger.fine("Downloading photo for " + fullName);
            File cacheFile = new File(photoFileName);

            if (downloadURL.contains("\\")) {
                logger.warning("Image URL badly formed: " + downloadURL);
                logger.warning("Trying to fix this");
                downloadURL = downloadURL.replace("\\", "/");
            }

            photoImage = new Image(downloadURL, 150, 150, true, true);

            try {
                URL imageURL = new URL(downloadURL);

                if (!Files.exists(Paths.get(cache), LinkOption.NOFOLLOW_LINKS)) {
                    Files.createDirectory(Paths.get(cache));
                }
                final URLConnection connection = imageURL.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                try (DataInputStream photoInputStream = new DataInputStream(connection.getInputStream());
                    FileOutputStream cacheFileOutputStream = new FileOutputStream(cacheFile)) {
                    BufferedImage photoBufferedImage = ImageIO.read(photoInputStream);
                    ImageIO.write(photoBufferedImage, "jpg", cacheFileOutputStream);
                }
            } catch (Exception ioe) {
                logger.log(Level.WARNING, "Unable to read photo for " + fullName
                    + " from " + downloadURL, ioe);
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
        final double squareDim = Math.min(photoImage.getWidth(), photoImage.getHeight());
        photo.setViewport(new Rectangle2D((photoImage.getWidth() - squareDim) / 2, 
            (photoImage.getHeight() - squareDim) / 2, 
            squareDim, squareDim));
        photo.setClip(new Circle(75, 75, 75));
        logger.finest("Speaker photo loaded");
        return photo;
    }

    /**
     * Simplified toString method to just return the full name
     *
     * @return The speaker's full name
     */
    @Override
    public String toString() {
        return fullName;
    }
}
