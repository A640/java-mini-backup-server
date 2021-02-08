package server;

import database.DBConnector;
import database.QueryExecutor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ConnectedClientTest {
    private static ConnectedClient c;
    private static ServerSocket serverS;

    private static ObjectOutputStream send;
    private static ObjectInputStream receive;


    @org.junit.jupiter.api.BeforeAll
    static void setUp() {

        //create server
//        Server s = new Server(2137);

//        assertDoesNotThrow(() -> DBConnector.getConnection());

        try {
            serverS = new ServerSocket(2137);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket socket = null;
        try {
            //create temporary client
            socket = new Socket("127.0.0.1",2137);

            //create output stream for sending data to server
            send = new ObjectOutputStream(socket.getOutputStream());

            //connect to "server"
            c = new ConnectedClient(serverS.accept());

            //create input stream for reading response from server
            receive = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        c.start();
//        Server s = new Server(2137);
//
//
//        this.socket = new Socket(address, port);
    }



    @org.junit.jupiter.api.Test
     void databaseConnection() {
        assertDoesNotThrow(() -> DBConnector.getConnection());
    }

    @org.junit.jupiter.api.Test
    void loginLocal() {
        String login = "test";
        String password = "unit";

        assertTrue(c.login(login,password));

        //cleanup after test
        try {
            receive.readUTF();
            receive.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @org.junit.jupiter.api.Test
    void loginWithConnection() {

//        c.start();

        String login = "test";
        String password = "unit";

        try {

            send.writeUTF("login");
            send.writeUTF(login);
            send.writeUTF(password);
            send.flush();



            assertEquals("login",receive.readUTF());
            assertTrue(receive.readBoolean());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @org.junit.jupiter.api.Test
    void createBackupLocal() {
        String name= "UNIT TEST BACKUP";
        String description = "UNIT TEST BACKUP - SHOULD NOT BE VISIBLE IN USER BACKUPS";

        int backupID = c.createBackup(name,description,1);

        assertNotNull(backupID);
        assertNotEquals(-1,backupID);

        System.out.println("Utworzono nowy backup o ID: " + backupID);

        //cleanup after test
        try {
            receive.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //remove from database
        QueryExecutor.removeBackup(backupID);

    }

    @org.junit.jupiter.api.Test
    void createBackupWithConnection() {

//        c.start();

        String login = "test";
        String password = "unit";

        String name= "UNIT TEST BACKUP";
        String description = "UNIT TEST BACKUP - SHOULD NOT BE VISIBLE IN USER BACKUPS";

        try {
            //login
//            send.writeUTF("login");
//            send.writeUTF(login);
//            send.writeUTF(password);
//            send.flush();
//
//            assertEquals("login",receive.readUTF());
//            assertTrue(receive.readBoolean());

            loginWithConnection();

            //creating new backup
            send.writeUTF("createBackup");
            send.writeUTF(name);
            send.writeUTF(description);
            send.flush();

            int backupID = receive.readInt();

            assertNotNull(backupID);
            assertNotEquals(-1,backupID);

            System.out.println("Utworzono nowy backup o ID: " + backupID);


            //remove from database
            QueryExecutor.removeBackup(backupID);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @org.junit.jupiter.api.Test
    void addFileLocal() {
        String name= "test1.txt";
        long size = 8000;
        int backupID = 1;

        int fileID = QueryExecutor.newFile(name,size,backupID);

        assertNotNull(fileID);
        assertNotEquals(-1,fileID);

        System.out.println("Dodano do bazy danych plik o ID: " + fileID);

        //cleanup after test

        //remove from database
        QueryExecutor.removeFile(fileID);

    }


//    @org.junit.jupiter.api.Test
//    void upload() {
//
//        loginWithConnection();
//
//
//
//
//    }
}