/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Giovanni
 */
public class Client implements Runnable{

    private final String IP;
    private final int PORT;
    private Socket socket;
    private BufferedReader is;
    private PrintWriter os;
    private boolean disconnect;
    private GestoreClient gestoreClient;

    public Client(String IP, int PORT, GestoreClient gestoreClient) throws IOException {
        this.gestoreClient = gestoreClient;
        this.IP = IP;
        this.PORT = PORT;
        disconnect = false;
        startConnection();
    }

    private void startConnection() throws IOException {
        Socket socketClient = new Socket(IP, PORT);
        is = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        os = new PrintWriter(socketClient.getOutputStream(), true);
    }
    
    public void sendMessage(String message){
        os.println(message);
    }

    @Override
    public void run(){
        String recived;
        int recivedCount = 0;
        try {
            while (!disconnect) {
                TimeUnit.MILLISECONDS.sleep(100);
                recived = is.readLine();
                if(recived != null){
                    gestoreClient.writeMessage(recived);
                    recivedCount = 0;
                } else if(recivedCount == 10){
                    disconnect = true;
                    gestoreClient.writeError("Error on the connection...disconnected");
                } else {
                    recivedCount++;
                }
                
            }
        } catch (IOException ex) {
            gestoreClient.writeError("Error on the connection...disconnected");
            disconnect = true;
        } catch (InterruptedException ex) {
            disconnect = true;
        }
    }

}
