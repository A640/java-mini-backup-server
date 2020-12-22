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
        System.out.println("Inicjalizacja serwera plików...");
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("łączenie z bazą danych...");
            DBConnector.getConnection();
            System.out.println("Oczekiwanie na połączenie na: " + this.serverSocket.getLocalSocketAddress());
            waitForClients();
        } catch (IOException e) {
            System.out.println("Nie można utworzyć serwera na wskazanym porcie. Powód: " + e.toString());
        } catch (SQLException e) {
            System.out.println("Nie można połączyć się z bazą danych. Powód: " + e.toString());
        }
    }

    private void waitForClients(){
        Socket socket = null;
        while(true){
            try {
                socket = serverSocket.accept();
                new ConnectedClient(socket).start();
            } catch (IOException e) {
                System.out.println("Nie można połączyć z klientem. Powód: " + e.toString());
            }
        }
    }
}
