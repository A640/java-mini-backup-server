package database;

import common.BFile;
import common.Backup;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class QueryExecutor {
    private static Connection connection = null;

    private static void checkConnection(){
        if(connection == null){
            try {
                connection = DBConnector.getConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static ResultSet get(String sql){

        checkConnection();

        ResultSet result = null;

        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }


    public static boolean set(String sql){

        boolean result = false;

        checkConnection();


        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
            result = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }


    public static int login(String login, String password){

        checkConnection();


        int userID = -1;

        String sql="SELECT id FROM user WHERE login = ? AND password = ?";
        PreparedStatement selectUser = null;
        ResultSet result = null;
        try {
            selectUser = connection.prepareStatement(sql);
            selectUser.setString(1, login);
            selectUser.setString(2, password);
            result = selectUser.executeQuery();


            result.next();
            userID =  result.getInt("id");


        } catch (SQLException throwables) {
            userID = -1;
        }

        return userID;
    }


    public static boolean register(String name, String surname, String login, String password){

        checkConnection();


        boolean result = false;
        String sql = "INSERT INTO user (name,surname,login,password) VALUES (?,?,?,?)";
        try {
            PreparedStatement insertUser = connection.prepareStatement(sql);
            insertUser.setString(1, name);
            insertUser.setString(2, surname);
            insertUser.setString(3, login);
            insertUser.setString(4, password);

            insertUser.executeUpdate();

            result = true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;

    }

    public static int newFile(String name, long size, int backupID){

        checkConnection();


        int id = -1;
        String sql = "INSERT INTO file (name,size,backupID) VALUES (?,?,?)";
        String sql2 = "INSERT INTO file (name,size) VALUES (?,?)";
        try {
            PreparedStatement insertFile = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insertFile.setString(1, name);
            insertFile.setLong(2, size);
            insertFile.setInt(3,backupID);

            insertFile.executeUpdate();
            ResultSet res = null;
            res =insertFile.getGeneratedKeys();

            res.next();
            id = res.getInt("GENERATED_KEY");


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;


//        String sql = "INSERT INTO file (name,size) VALUES (" + "\"" +name + "\",\"" + size + "\""+")";
//        System.out.println(sql);
//        int id = -5;
//        try {
//            Statement statement = connection.createStatement();
//            id = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

//        return id;
    }

    public static int newBackup(String name, String description, int userID){

        checkConnection();


        int id = -1;
        String sql = "INSERT INTO backup (name,description,userID) VALUES (?,?,?)";
        try {
            PreparedStatement insertFile = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insertFile.setString(1, name);
            insertFile.setString(2, description);
            insertFile.setInt(3,userID);

            insertFile.executeUpdate();
            ResultSet res = null;
            res =insertFile.getGeneratedKeys();

            res.next();
            id = res.getInt("GENERATED_KEY");


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;

    }

    public static void removeBackup(int backupID){

        checkConnection();

        String sql = "DELETE FROM `backup` WHERE `backup`.`ID` = ?";
        try {
            PreparedStatement remove = connection.prepareStatement(sql);
            remove.setInt(1, backupID);

            remove.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void removeFile(int fileID){

        checkConnection();

        String sql = "DELETE FROM `file` WHERE `file`.`ID` = ?";
        try {
            PreparedStatement remove = connection.prepareStatement(sql);
            remove.setInt(1, fileID);

            remove.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static List<Backup> getBackupsList(int userID){

        checkConnection();

        List<Backup> backups= new LinkedList<>();


        String sql = "SELECT * FROM `backup` WHERE userID = ? ORDER BY `backup`.`created_date` DESC ";
        try {
            PreparedStatement getBackups = connection.prepareStatement(sql);
            getBackups.setInt(1, userID);



            ResultSet res = getBackups.executeQuery();

            while(res.next()){

                List<BFile> files = getFilesList(res.getInt("ID"));

                backups.add(new Backup(res.getInt("ID"),res.getString("name"),
                        res.getString("description"),res.getDate("created_date"),files));

            }



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return backups;
    }


    public static List<BFile> getFilesList(int backupID){

        checkConnection();

        List<BFile> bfiles= new LinkedList<>();

        String sql = "SELECT * FROM `file` WHERE backupID = ?";
        try {
            PreparedStatement getBackups = connection.prepareStatement(sql);
            getBackups.setInt(1, backupID);



            ResultSet res = getBackups.executeQuery();

            while(res.next()){
                //add info about each file to list
                bfiles.add(new BFile(res.getInt("ID"),res.getString("name"),
                        res.getLong("size")));
            }



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return bfiles;

    }

}
