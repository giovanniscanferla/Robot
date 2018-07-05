/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import robot.gestore.Protocol;

/**
 *
 * @author Giovanni
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private ExecutorService executorPool;
    private final int MAXTHREAD = 10;
    private final int PORT;
    private boolean terminate;
    private GestoreServer gestoreServer;
    private ArrayList<ClientSocket> clientSockList;

    public Server(int port, GestoreServer gestoreServer) throws IOException {
        clientSockList = new ArrayList<>();
        this.PORT = port;
        this.gestoreServer = gestoreServer;
        terminate = false;
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            executorPool = Executors.newWorkStealingPool(MAXTHREAD);
        } catch (IOException ex) {
            throw new IOException("Error while creating the server");
        }
    }

    @Override
    public void run() {
        while (!terminate) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientSocket client = new ClientSocket(clientSocket);
                clientSockList.add(client);
                executorPool.execute(client);
                TimeUnit.MILLISECONDS.sleep(100);

            } catch (IOException ex) {
                gestoreServer.sendMessage("Error while connecting to a new client");

            } catch (InterruptedException ex) {
                terminate = true;
            } finally {

            }
        }
        executorPool.shutdownNow();
    }

    public void disconnect() {
        terminate = true;
        executorPool.shutdownNow();
        executorPool.shutdown();
        try {
            serverSocket.close();
        } catch (IOException ex) {

        }

        Iterator<ClientSocket> it = clientSockList.iterator();

        while (it.hasNext()) {
            it.next().disconnect();
        }
    }

    private class ClientSocket implements Runnable {

        private Socket socket;
        private Protocol protocolServer;
        private BufferedReader is;
        private PrintStream os;
        private boolean disconnect;
        private String ip;

        public ClientSocket(Socket socket) throws IOException {
            disconnect = false;
            protocolServer = new Protocol();
            ip = socket.getInetAddress().toString();
            socket.setSoTimeout(5000);
            try {
                this.socket = socket;
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintStream(socket.getOutputStream());
            } catch (IOException ex) {
                gestoreServer.sendMessage(ip + ": error on the connection with the client... disconnecting");
                disconnect = true;
                throw new IOException();

            }
        }

        public void disconnect() {
            try {
                socket.close();
            } catch (IOException ex) {

            }
        }

        @Override
        public void run() {

            gestoreServer.sendMessage(ip + ": starting listening");
            int timeWaiting = 0;
            while (!disconnect) {
                try {
                    if (timeWaiting == 10) {
                        os.println("Are you still alive?");
                        gestoreServer.sendMessage(ip + ": disconnecting in 50 seconds");
                    } else if (timeWaiting == 20) {
                        os.println("Well, I have better things to do in my life. Bye idiot.");
                    }

                    String line = is.readLine();
                    timeWaiting = 0;
                    TimeUnit.SECONDS.sleep(1);
                    gestoreServer.sendMessage(ip + ": " + line);
                    os.println(protocolServer.elaborateAnswer(line));

                    
                } catch (InterruptedException ex) {
                    disconnect = true;
                    clientSockList.remove(this);
                } catch (IOException ex) {
                    if (timeWaiting == 20) {
                        gestoreServer.sendMessage(ip + ": error on the connection with the client...disconnecting");
                        disconnect = true;
                        clientSockList.remove(this);
                        disconnect();
                    } else {
                        timeWaiting++;
                    }

                }
            }
        }

    }
}
