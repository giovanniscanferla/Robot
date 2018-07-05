/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import robot.network.client.Client;
import robot.network.client.GestoreClient;
import robot.network.server.GestoreServer;

/**
 *
 * @author Giovanni
 */
public class StartController implements Initializable {

    @FXML
    Pane pnTitle;

    @FXML
    VBox vbInfo;

    @FXML
    JFXTextField tfPort;

    @FXML
    JFXTextField tfIP;
    
    @FXML
    Pane pFoot;
    
    @FXML
    Label lbError;

    final String LOGO = "robot/asserts/botLogo.png";
    final String CAT = "robot/asserts/cat.gif";
    final String ROCKY = "robot/asserts/rocky.gif";
    final String SHARK = "robot/asserts/shark.gif";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitlePaneImage();
        setVBoxImage();
    }

    private void setTitlePaneImage() {
        String style = "-fx-background-image: url('" + LOGO + "'); "
                + "-fx-background-position: center center; "
                + "-fx-background-repeat: stretch; "
                + "-fx-background-repeat: no-repeat;"
                + "-fx-background-color: #353535;"
                + "-fx-background-size: " + (pnTitle.getPrefHeight() - 10) + ", " + (pnTitle.getPrefHeight() - 10) + ";";

        pnTitle.setStyle(style);
    }

    private void setVBoxImage() {
        String style = "-fx-background-image: url('" + SHARK + "'); "
                + "-fx-background-position: left bottom; "
                + "-fx-background-repeat: stretch; "
                + "-fx-background-repeat: no-repeat;"
                + "-fx-background-size: 300, 300;";

        vbInfo.setStyle(style);
    }

    @FXML
    public void startServer(){
        if(!checkCorrectInformation(tfPort.getText(), "PORT")){
            
            lbError.setText("You must insert a valid PORT");

        } else {
            try {
                GestoreServer gestoreServer = new GestoreServer(Integer.parseInt(tfPort.getText()));
            } catch (IOException ex) {
                lbError.setText(ex.getMessage());
            } catch (Exception ex) {
                lbError.setText(ex.getMessage());
            }

        }

    }
    
    
    @FXML
    public void startClient() {
        if (!checkCorrectInformation(tfPort.getText(), "PORT") || !checkCorrectInformation(tfIP.getText(), "IP")) {
            
            lbError.setText("You must insert a valid PORT and ADDRESS");

        } else {
            boolean chiudi = false;

            try {
                GestoreClient gestoreClient = new GestoreClient(tfIP.getText(), Integer.parseInt(tfPort.getText()));
            } catch (IOException ex) {
                lbError.setText("Error connecting to the server");
            } catch (Exception ex) {
                lbError.setText(ex.getMessage());
            }
            
        }

    }

    private boolean checkCorrectInformation(String field, String type) {
        switch (type) {
            case "IP":
                String[] p = field.split("\\.");
                if (p.length == 4) {
                    for (String num : p) {
                        try {
                            Integer.parseInt(num);
                        } catch (Exception e) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
                break;
            case "PORT":
                try {
                    Integer.parseInt(field);
                } catch (Exception e) {
                    return false;
                }
                break;
        }
        return true;
    }
}
