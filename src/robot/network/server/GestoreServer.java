/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.server;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Giovanni
 */
public class GestoreServer {

    private final int PORT;
    private Server server;
    private ServerConsoleController servercc;

    public GestoreServer(int PORT) throws IOException, Exception {
        this.PORT = PORT;

        server = null;
        Stage stage = new Stage();
        try {
            server = new Server(PORT, this);
            start(stage);
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        } catch (Exception ex) {
            throw new Exception("Error while initializating server interface");
        } finally {
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }

    private void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerConsole.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Server Console");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            server.disconnect();
            
        });
        servercc = fxmlLoader.getController();
    }

    public void sendMessage(String sentence) {
        servercc.writeLine(sentence);
    }

}
