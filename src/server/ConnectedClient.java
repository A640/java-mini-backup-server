package server;

import common.Backup;
import database.QueryExecutor;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.LinkedList;
import java.util.List;

public class ConnectedClient extends Thread {
    private Socket socket;
    private ObjectOutputStream send;
    private ObjectInputStream receive;
    private int clientID;
    private boolean connected;

    public ConnectedClient(Socket clientSocket){
        this.socket = clientSocket;
        this.clientID = -1;
        System.out.println("New connection from: " + socket.getInetAddress());

        try {

            //create output stream for sending data to client
            send = new ObjectOutputStream(socket.getOutputStream());

            //create input stream for reading response from client
            receive = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.out.println("An error occurred while trying to connect with the client " +socket.getInetAddress()+
                    " . Details: " + e.toString());

        }

    }

    @Override
    public void run(){
        String action = "INIT";
        this.connected = true;

        while (this.connected){

            //read action from client
            try{
                action = receive.readUTF();
            } catch (SocketException e) {
                try {
                    //when client disconnects
                    System.out.println("Connection closed with: " + this.socket.getInetAddress());
                    this.socket.close();
                    this.connected = false;
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //when client disconnects
            if(action == null){
                try {
                    //when client disconnects
                    System.out.println("Connection closed with: " + this.socket.getInetAddress());
                    this.socket.close();
                    connected = false;
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }


            //check action
            switch(action){
                case "login": {

                    String login = null;
                    String password = null;

                    try{
                        login = receive.readUTF();
                        password = receive.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    login(login, password);
                    break;
                }

                case "register":{
                    String name = null;
                    String surname = null;
                    String login = null;
                    String password = null;

                    try{
                        name = receive.readUTF();
                        surname = receive.readUTF();
                        login = receive.readUTF();
                        password = receive.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    register(name,surname,login,password);
                    break;
                }

                case "createBackup":{
                    String name = null;
                    String description = null;

                    try{
                        name = receive.readUTF();
                        description = receive.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    createBackup(name, description,this.clientID);
                    break;
                }

                case "upload": {
                    String name = null;
                    Long size = null;

                    int backupID = -1;
                    try{
                        name = receive.readUTF();
                        size = receive.readLong();
                        backupID = receive.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    upload(name,size, backupID);
                    break;
                }

                case "download": {
                    int backupID = -1;
                    int fileID = -1;

                    try{
                        backupID = receive.readInt();
                        fileID = receive.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    download(this.clientID,backupID,fileID);
                    break;
                }

                case "getBackupList": {
                    getBackupList();
                    break;
                }

                default:{
                    System.out.println("Unknown action: " + action);
                    break;
                }
            }
        }



        return;
    }


    private void login(String login, String password){
//        String login = null;
//        String password = null;
//        try{
//            login = receive.readUTF();
//            password = receive.readUTF();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String sql="SELECT id FROM user WHERE login = \"" + login + "\" AND password = \"" + password + "\"";
//        System.out.println(sql);
//        ResultSet res = null;
//        res = QueryExecutor.get(sql);
//
//        try {
//            res.next();
//            this.clientID = res.getInt("id");
//            send.writeUTF("login");
//            send.writeBoolean(true);
//            send.flush();
//
//        } catch (SQLException | IOException throwables ) {
//            try {
//                send.writeUTF("login");
//                send.writeBoolean(false);
//                send.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        int id = QueryExecutor.login(login,password);

        if(id != -1){
            try {
                this.clientID = id;
                send.writeUTF("login");
                send.writeBoolean(true);
                send.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                send.writeUTF("login");
                send.writeBoolean(false);
                send.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void upload(String name, Long size, int backupID){
        //upload files from client to server

        //get info about file
//        String name = null;
//        Long size = null;
//        int backupID = -1;
//        try{
//            name = receive.readUTF();
//            size = receive.readLong();
//            backupID = receive.readInt();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //save file info to database

        int fileID = QueryExecutor.newFile(name, size, backupID);


        //get file

        try{
            Path path = Paths.get(System.getProperty("user.dir"),"/files/",String.valueOf(clientID),
                    String.valueOf(backupID),String.valueOf(fileID)+".jmpb");
            Files.createDirectories(path.getParent());
//            System.out.println(path);
            Files.copy(receive,path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //after upload close connection with file transfer socket
                System.out.println("Connection closed with: " + this.socket.getInetAddress());
                socket.close();
                this.connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void register(String name,String surname,String login,String password){
//        String name = null;
//        String surname = null;
//        String login = null;
//        String password = null;
//        try{
//            name = receive.readUTF();
//            surname = receive.readUTF();
//            login = receive.readUTF();
//            password = receive.readUTF();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        boolean success = false;
        success = QueryExecutor.register(name,surname,login,password);

        try {
            send.writeUTF("register");
            send.writeBoolean(success);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createBackup(String name, String description, int clientID){
//        String name = null;
//        String description = null;
//        try{
//            name = receive.readUTF();
//            description = receive.readUTF();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int backupID = -1;
        backupID = QueryExecutor.newBackup(name,description,clientID);
        try {
            send.writeInt(backupID);
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getBackupList(){
        List<Backup> backups = QueryExecutor.getBackupsList(this.clientID);

        try {
            send.writeUTF("backups");
            send.writeObject(backups);
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("DONE");
    }

    private void download(int clientID, int backupID, int fileID){

        try{
            Path path = Paths.get(System.getProperty("user.dir"),"/files/",String.valueOf(clientID),
                    String.valueOf(backupID),String.valueOf(fileID)+".jmpb");

            send.writeUTF("File found");
            send.flush();

            System.out.println(path.toString());
            Files.copy(path,send);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //after upload close connection with file transfer socket
                System.out.println("Connection closed with: " + this.socket.getInetAddress());
                socket.close();
                this.connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
