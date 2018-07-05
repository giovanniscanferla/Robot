/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.client;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Giovanni
 */
public class ClientController implements Initializable {

    @FXML
    BorderPane mainPane;

    @FXML
    VBox vbDisplay;

    @FXML
    JFXTextField tfMessage;

    private GestoreClient gestoreClient;
    private ScrollPane scroll;

    @FXML
    private void sendMessage() {
        if (!tfMessage.getText().equals("")) {
            HBox hbContainer = new HBox();
            hbContainer.setPadding(new Insets(10, 10, 10, 10));

            Label lbMessage = new Label(tfMessage.getText());
            lbMessage.setMinSize(190, 50);
            lbMessage.setMaxSize(190, Double.MAX_VALUE);
            lbMessage.setAlignment(Pos.CENTER_RIGHT);
            lbMessage.setWrapText(true);
            lbMessage.setPadding(new Insets(10, 10, 10, 10));
            lbMessage.setStyle("-fx-background-color: #c9c9c9;");

            Label lbVoid = new Label();
            lbVoid.setMinSize(190, 50);

            Platform.runLater(() -> {
                hbContainer.getChildren().add(lbVoid);
                hbContainer.getChildren().add(lbMessage);
                vbDisplay.getChildren().add(hbContainer);
            });

            gestoreClient.sendMessage(tfMessage.getText());
            tfMessage.setText("");
        }
    }

    public void setGestoreClient(GestoreClient gestoreClient) {
        this.gestoreClient = gestoreClient;
    }

    public void addError(String error) {
        Label lbError = new Label(error);
        lbError.setMinSize(400, 50);
        lbError.setAlignment(Pos.CENTER);
        lbError.setWrapText(true);
        lbError.setStyle("-fx-background-color: #bc3609; -fx-text-fill: WHITE;");

        Platform.runLater(() -> {
            vbDisplay.getChildren().add(lbError);
        });

    }

    public void addRecivedMessage(String recived) {
        HBox hbContainer = new HBox();
        hbContainer.setPadding(new Insets(10, 10, 10, 10));

        Label lbMessage = new Label(recived);
        lbMessage.setMinSize(190, 50);
        lbMessage.setMaxSize(190, Double.MAX_VALUE);
        lbMessage.setAlignment(Pos.CENTER_LEFT);
        lbMessage.setWrapText(true);
        lbMessage.setPadding(new Insets(10, 10, 10, 10));
        lbMessage.setStyle("-fx-background-color: #353535; -fx-text-fill: WHITE;");

        Label lbVoid = new Label();
        lbVoid.setMinSize(190, 50);

        Platform.runLater(() -> {
            hbContainer.getChildren().add(lbMessage);
            hbContainer.getChildren().add(lbVoid);
            vbDisplay.getChildren().add(hbContainer);
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
