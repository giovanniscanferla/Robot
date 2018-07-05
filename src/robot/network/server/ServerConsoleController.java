/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.server;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Giovanni
 */
public class ServerConsoleController implements Initializable {

    @FXML
    VBox vbDisplay;
    
    @FXML
    BorderPane mainPane;
    
    private ScrollPane scroll;

    public void writeLine(String sentence) {

        vbDisplay.setPadding(new Insets(10, 10, 10, 10));
        vbDisplay.setSpacing(10);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        sentence = df.format(calobj.getTime()) + "---" + sentence;

        Label label = new Label(sentence);
        label.setWrapText(true);
        //label.setMaxWidth(400);
        label.setMinWidth(400);
        //label.setPrefWidth(400);

        Platform.runLater(() -> {
            vbDisplay.getChildren().add(label);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scroll = new ScrollPane();
        scroll.setContent(vbDisplay);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPane.setRight(scroll);

        vbDisplay.heightProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                scroll.setVvalue((Double) newValue + 20.0);
            }
        });
    }

}
