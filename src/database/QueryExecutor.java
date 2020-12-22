package database;

import java.sql.*;

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

}
