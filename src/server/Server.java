package server;

import database.DBConnector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;


    public static void main(String[] args) {
        Server s = new Server(2137);
    }

    public Server(int port){
        System.out.println("File server initialization...");
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Connecting with database...");
            DBConnector.getConnection();
            System.out.println("Waiting for connections on: " + this.serverSocket.getLocalSocketAddress());
            waitForClients();
        } catch (IOException e) {
            System.out.println("The server cannot be created on the provided port. Reason: " + e.toString());
        } catch (SQLException e) {
            System.out.println("Cannot connect to database. Reason: " + e.toString());
        }
    }

    private void waitForClients(){
        Socket socket = null;
        while(true){
            try {
                socket = serverSocket.accept();
                new ConnectedClient(socket).start();
            } catch (IOException e) {
                System.out.println("Cannot connect with client. Reason: " + e.toString());
            }
        }
    }
}
