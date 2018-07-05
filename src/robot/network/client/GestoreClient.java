/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.client;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Giovanni
 */
public class GestoreClient {

    
    private Client client;
    private ClientController clientController;
    private final String IP;
    private final int PORT;
    private Thread clientThread;

    public GestoreClient(String IP, int PORT) throws IOException, Exception {
        this.IP = IP;
        this.PORT = PORT;
        
        try {
            client = new Client(IP, PORT, this);
            start(new Stage());
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        } catch (Exception ex) {
            throw new Exception("Error while initializating client interface");
        } finally {
            clientThread = new Thread(client);
            clientThread.start();
        }
        
    }
    
    private void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Client: " + IP + ":" + PORT);
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(600);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            clientThread.interrupt();
            
        });
        clientController = fxmlLoader.getController();
        clientController.setGestoreClient(this);
    }

    public void writeError(String error) {
        clientController.addError(error);
    }

    public void writeMessage(String recived) {
        clientController.addRecivedMessage(recived);
    }
    
    public void sendMessage(String message){
        client.sendMessage(message);
    }
}
