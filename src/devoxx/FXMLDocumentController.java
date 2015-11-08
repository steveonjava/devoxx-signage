/*
 * Devoxx digital signage project
 */
package devoxx;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 *
 * @author Angie
 */
public class FXMLDocumentController implements Initializable {

    private static final DateTimeFormatter TIME_FORMAT
        = DateTimeFormatter.ofPattern("HH:mm");
    
    private final BooleanProperty offline = new SimpleBooleanProperty(false);

    @FXML
    Label sessionLbl, roomLbl, roomNumber, currentTimeTitleLbl, time,
        sessionTitle, sessionTime, sessionAbstract, sessionsTitleLbl,
        talk1Time, talk1Title, talk2Time, talk2Title, talk3Time, talk3Title;

    @FXML
    VBox speakersVBox, speaker1, speaker2, speaker3;

    @FXML
    ImageView speakerImg1, speakerImg2, speakerImg3;

    @FXML
    Label speakerName1, speakerName2, speakerName3;

    Font lightFont, qTypeBig, qTypeSml, titleThin, gothambookBig,
        gothambookMed, gothambookSml, titleHuge, titleBig, timeFont,
        roomNumberFont;
    
    @FXML Circle networkCircle;

    /**
     * Exit the application
     *
     * @param event An associated mouse event
     */
    @FXML
    private void quitApp(MouseEvent event) {
        System.exit(0);
    }

    /**
     * Initialise various aspects of the display
     *
     * @param url The URL (not used in this app)
     * @param rb The resource bundle (not used in this app)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        networkCircle.visibleProperty().bind(offline);
        /* Load fonts */
        lightFont = Font.loadFont(Devoxx.class.getResource("fonts/gothamexlight-webfont.ttf").toExternalForm(), 20);
        titleThin = Font.loadFont(Devoxx.class.getResource("fonts/gothamexlight-webfont.ttf").toExternalForm(), 40);
        qTypeBig = Font.loadFont(Devoxx.class.getResource("fonts/QTypeOT-SeextMedium.otf").toExternalForm(), 30);
        qTypeSml = Font.loadFont(Devoxx.class.getResource("fonts/QTypeOT-SeextMedium.otf").toExternalForm(), 23);
        gothambookBig = Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 35);
        gothambookMed = Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 28);
        gothambookSml = Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 25);
        titleHuge = Font.loadFont(Devoxx.class.getResource("fonts/GillSans.ttc").toExternalForm(), 83);
        setFonts();

        KeyFrame keyFrame = new KeyFrame(Duration.minutes(1),
            t -> time.setText(LocalTime.now().format(TIME_FORMAT)));
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().get(0).getOnFinished().handle(null);
        timeline.play();
    }

    /**
     * Set the fonts to be used for the different parts of the display
     */
    public void setFonts() {
        sessionTime.setFont(qTypeBig);
        talk1Time.setFont(qTypeBig);
        talk2Time.setFont(qTypeSml);
        talk3Time.setFont(qTypeSml);
        currentTimeTitleLbl.setFont(titleThin);
        sessionsTitleLbl.setFont(titleThin);
        talk1Title.setFont(gothambookBig);
        sessionAbstract.setFont(gothambookMed);
        talk2Title.setFont(gothambookSml);
        talk3Title.setFont(gothambookSml);
        sessionLbl.setFont(Font.font("Arial", FontWeight.BOLD, 83));
        roomLbl.setFont(Font.font("Arial", FontWeight.BOLD, 83));
        roomNumber.setFont(Font.font("Arial", FontWeight.BOLD, 195));
        sessionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 45));
        time.setFont(Font.font("Arial", FontWeight.BOLD, 90));
    }

    /**
     * Set the data for the screen
     *
     * @param mainPreso The main presentation that is on now or next
     * @param secondPreso The next presentation (if there is one)
     * @param thirdPreso The presentation after next (if there is one)
     */
    public void setScreenData(Presentation mainPreso,
        Presentation secondPreso, Presentation thirdPreso) {
        /* Remove current data from the speaker VBox */
        if (speakersVBox.getChildren() != null) {
            while (speakersVBox.getChildren().size() > 0) {
                speakersVBox.getChildren().remove(0);
            }
        }

        if (mainPreso != null && mainPreso.title != null) {
            sessionTitle.setText((mainPreso.title).toUpperCase());
            sessionAbstract.setText(mainPreso.summary);
            sessionTime.setText(mainPreso.fromTime.format(TIME_FORMAT) + " - "
                + mainPreso.toTime.format(TIME_FORMAT));

            /**
             * Sort out the speaker photos and names. This has proved to be
             * incredibly difficult, I know not why. There seems to be some
             * weirdness with some of the photos so they occupy a bigger space
             * than they should which screws things up (if we just use a simple
             * VBox). This still does not work properly (Mark Reinhold is a good
             * test case)
             */
            for (Speaker speaker : mainPreso.speakers) {

                Group speakerGroup = new Group();
                HBox photoBox = new HBox();
                photoBox.setAlignment(Pos.CENTER);
                photoBox.getChildren().add(speaker.photo);
                speakerGroup.getChildren().add(photoBox);
                speaker.photo.setTranslateX(15);

                HBox nameBox = new HBox();
                nameBox.setAlignment(Pos.CENTER);
                nameBox.setTranslateY(165);
                Label name = new Label(speaker.fullName.toUpperCase());
                name.setFont(lightFont);
                nameBox.getChildren().add(name);
                speakerGroup.getChildren().add(nameBox);

                speakersVBox.getChildren().add(speakerGroup);
            }

            talk1Title.setText(mainPreso.title);
            talk1Time.setText(mainPreso.fromTime.format(TIME_FORMAT) + " - "
                + mainPreso.toTime.format(TIME_FORMAT));
        } else {
            sessionTitle.setText("");
            sessionAbstract.setText("");
            sessionTime.setText("");
            talk1Title.setText("");
            talk1Time.setText("");
        }

        if (secondPreso != null) {
            talk2Title.setText(secondPreso.title);
            talk2Time.setText(secondPreso.fromTime.format(TIME_FORMAT) + " - "
                + secondPreso.toTime.format(TIME_FORMAT));
        } else {
            talk2Title.setText("");
            talk2Time.setText("");
        }

        if (thirdPreso != null) {
            talk3Title.setText(thirdPreso.title);
            talk3Time.setText(thirdPreso.fromTime.format(TIME_FORMAT) + " - "
                + thirdPreso.toTime.format(TIME_FORMAT));
        } else {
            talk3Title.setText("");
            talk3Time.setText("");
        }
    }
    
    public void setOnline() {
        offline.set(false);
    }
    
    public void setOffline() {
        offline.set(true);
    }

    /**
     * Set which room data is being displayed for.
     *
     * @param room The name of the room
     */
    public void setRoom(String room) {
        /**
         * For Devoxx BE there are two BOF rooms, which we treat differently by
         * moing the labels for the session and room and reducing the font size
         * of the room number
         */
        if (room.startsWith("BOF")) {
            roomNumber.setFont(Font.font("Arial", FontWeight.BOLD, 120));
            roomNumber.setTranslateX(-160);
            roomNumber.setTranslateY(30);
            sessionLbl.setTranslateX(-150);
            roomLbl.setTranslateX(-150);
        } else {
            roomNumber.setFont(Font.font("Arial", FontWeight.BOLD, 195));
        }

        roomNumber.setText(room);
    }

}
