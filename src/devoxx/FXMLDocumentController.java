/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devoxx;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private static DateFormat TIME24_FORMAT = new SimpleDateFormat("HH:mm");

    @FXML
    Label sessionLbl, roomLbl, roomNumber, currentTimeTitleLbl, time, sessionTitle, sessionTime, sessionAbstract, 
            sessionsTitleLbl, talk1Time, talk1Title, talk2Time, talk2Title, talk3Time, talk3Title;  //New

    @FXML
    VBox speakersVBox, speaker1, speaker2, speaker3;

    @FXML
    ImageView speakerImg1, speakerImg2, speakerImg3;

    @FXML
    Label speakerName1, speakerName2, speakerName3;

    
    Font lightFont, qTypeBig, qTypeSml, titleThin, gothambookBig, gothambookMed, gothambookSml,
            titleHuge, titleBig, timeFont, roomNumberFont; //New

    @FXML
    private void quitApp(MouseEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       //New
        lightFont = Font.loadFont(Devoxx.class.getResource("fonts/gothamexlight-webfont.ttf").toExternalForm(), 20);
        titleThin = Font.loadFont(Devoxx.class.getResource("fonts/gothamexlight-webfont.ttf").toExternalForm(), 40);
        qTypeBig =  Font.loadFont(Devoxx.class.getResource("fonts/QTypeOT-SeextMedium.otf").toExternalForm(), 30);
        qTypeSml =  Font.loadFont(Devoxx.class.getResource("fonts/QTypeOT-SeextMedium.otf").toExternalForm(), 23);
        gothambookBig =  Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 35);
        gothambookMed =  Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 28);
        gothambookSml =  Font.loadFont(Devoxx.class.getResource("fonts/gothambook-webfont.ttf").toExternalForm(), 25);
        titleHuge =  Font.loadFont(Devoxx.class.getResource("fonts/GillSans.ttc").toExternalForm(), 83);
        setFonts();
        //End New
        // TODO
        KeyFrame keyFrame = new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                time.setText(TIME24_FORMAT.format(new Date()));
            }
        });
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().get(0).getOnFinished().handle(null);
        timeline.play();
    }

    //New
    public void setFonts(){
    
           
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
    }//End new
    public void setScreenData(Presentation mainPreso, Presentation secondPreso, Presentation thirdPreso) {

        

        if (speakersVBox.getChildren() != null) {
            while (speakersVBox.getChildren().size() > 0) {
                speakersVBox.getChildren().remove(0);
            }

        }

        if (mainPreso != null && mainPreso.title != null) {
            System.out.println("         " + mainPreso);
        sessionTitle.setText((mainPreso.title).toUpperCase());
            sessionAbstract.setText(mainPreso.summary);
            sessionTime.setText(TIME24_FORMAT.format(mainPreso.fromTime) + " - " + TIME24_FORMAT.format(mainPreso.toTime));

            for (Speaker speaker : mainPreso.speakers) {
                VBox speakerBox = new VBox();
                speakerBox.setAlignment(Pos.CENTER);
                speakerBox.prefHeightProperty().set(180.0);
                speakerBox.prefWidthProperty().set(170.0);
                speakerBox.setSpacing(15.0);

                Image image = new Image(speaker.imageUrl, 200, 200, true, true);
                ImageView photo = new ImageView(image);
//                double scale = 170d / image.getWidth();
                if (image.getWidth() < image.getHeight()) {
                    photo.setFitWidth(170);
                } else {
                    photo.setFitHeight(170);
                }
                photo.setPreserveRatio(true);
//                photo.scaleXProperty().set(scale);
//                photo.scaleYProperty().set(scale);
                photo.setClip(new Circle(85, 85, 85));

                speakerBox.getChildren().add(photo);

                Label name = new Label(speaker.fullName.toUpperCase());
                name.setFont(lightFont);
                speakerBox.getChildren().add(name);
                speakersVBox.getChildren().add(speakerBox);
            }
            talk1Title.setText(mainPreso.title);
            talk1Time.setText(TIME24_FORMAT.format(mainPreso.fromTime) + " - " + TIME24_FORMAT.format(mainPreso.toTime));
        }else{
            sessionTitle.setText("");
            sessionAbstract.setText("");
            sessionTime.setText("");

            talk1Title.setText("");
            talk1Time.setText("");
        
        }
        
        if(secondPreso !=null){System.out.println("         " + secondPreso);}
        if(thirdPreso != null){System.out.println("         " + thirdPreso);}
        
        talk2Title.setText((secondPreso == null) ? "" : secondPreso.title);
        talk2Time.setText((secondPreso == null) ? "" : TIME24_FORMAT.format(secondPreso.fromTime) + " - " + TIME24_FORMAT.format(secondPreso.toTime));
        talk3Title.setText((thirdPreso == null) ? "" : thirdPreso.title);
        talk3Time.setText((thirdPreso == null) ? "" : TIME24_FORMAT.format(thirdPreso.fromTime) + " - " + TIME24_FORMAT.format(thirdPreso.toTime));

    }

    public void setRoom(String room) {
        roomNumber.setText(room);
    }
}
