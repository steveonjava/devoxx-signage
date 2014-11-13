package devoxx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static javafx.animation.Animation.INDEFINITE;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Angie
 */
public class Devoxx extends Application {

    private Date currentTimeOverride = null;// new Date(113, 10, 11);
    private FXMLDocumentController myScreenControler;
    private double SCALE = 1;

    private String roomNumber;

    private Presentation currentPresentation = null;
    private Presentation firstPreso, secondPreso, thirdPreso;
    private List<Presentation> presentations;
    private static DateFormat DEBUG_DATE_FORMAT = DateFormat.getDateTimeInstance();
    private static DateFormat TIME24_FORMAT = new SimpleDateFormat("HH:mm");
    
    @Override
    public void start(Stage stage) throws Exception {
         String myroom = getParameters().getRaw().isEmpty() ? null : getParameters().getRaw().get(0);
        if (myroom == null || myroom.isEmpty()) {
            myroom = "Room 8";
        }
        final String room = myroom;
        final boolean testMode = getParameters().getRaw().size() >= 2 && getParameters().getRaw().get(1).equals("test");
        System.out.println("=========================================================");
        System.out.println("== DEVOXX DISPLAY APP for room [" + room + "]");
        if (testMode) {
            SCALE = 0.75;
            System.out.println(" !!!! TEST MODE !!!!");
        }
        System.out.println("=========================================================");

        // FETCH DATA
        presentations = new DataFetcher(room).getData();
        System.out.println("******** SIZE: " + presentations.size());

        System.out.println("ROOM NUMBER = [" + room.split("\\s+")[1] + "]");
        roomNumber = (room.split("\\s+")[1]);

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent) myLoader.load();
        myScreenControler = ((FXMLDocumentController) myLoader.getController());
        root.setScaleX(SCALE);
        root.setScaleY(SCALE);

        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
       // stage.setFullScreen(true);
        stage.show();
        myScreenControler.setRoom(roomNumber);
      //  update();
        Timeline downloadTimeline = new Timeline(new KeyFrame(Duration.minutes(30), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                 List<Presentation> newPresentations = new DataFetcher(room).getData();
                if (newPresentations != null) {
                    presentations = newPresentations;
                }
                update();
            }
        }));
        downloadTimeline.setCycleCount(INDEFINITE);
        downloadTimeline.play();
        
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                update();
            }
        }));
        updateTimeline.setCycleCount(INDEFINITE);
        updateTimeline.getKeyFrames().get(0).getOnFinished().handle(null);
        updateTimeline.play();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void update() {
        Date now = (currentTimeOverride != null) ? currentTimeOverride : new Date();
        System.out.println("NOW = " + now + "  - currentTimeOverride = " + currentTimeOverride);
        List<Presentation> newPresentations = new ArrayList<>();
        for (Presentation presentation : presentations) {
            if (now.before(presentation.toTime)) {
                newPresentations.add(presentation);
            }
            if (newPresentations.size() >= 3) {
                break;
            }
        }
        firstPreso = newPresentations.size() >= 1 ? newPresentations.get(0) : null;
        secondPreso = newPresentations.size() >= 2 ? newPresentations.get(1) : null;
        thirdPreso = newPresentations.size() >= 3 ? newPresentations.get(2) : null;
        System.out.println("UPDATE @ (" + DEBUG_DATE_FORMAT.format(now) + ")");
        if (currentPresentation != firstPreso) {
            currentPresentation = firstPreso;
            myScreenControler.setScreenData(firstPreso, secondPreso, thirdPreso);
        }

    }

    private void goNext() {
        int index = presentations.indexOf(currentPresentation);
        if (index == presentations.size() - 2) {
            index = -1;
        }
        Presentation p = presentations.get(index + 1);
        currentTimeOverride = p.fromTime;
        update();
    }
}
