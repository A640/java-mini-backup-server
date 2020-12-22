package database;

import java.sql.*;

public class DBConnector {
    private static String url = "jdbc:mysql://localhost:3306/java?useUnicode=true&characterEncoding=UTF-8";
    private static String dbName = "java";
    private static String username = "java";
    private static String password = "mini-project";

    private static Connection connection = null;

    private static void initializeConnection() throws SQLException{

        Connection c = null;

//        String driver = "com.mysql.jdbc.Driver";


//            Class.forName(driver).newInstance();
        c = DriverManager.getConnection(url,username,password);
        System.out.println("Połączono z bazą: " + url + dbName);


        connection = c;

    }

    public static Connection getConnection() throws SQLException {
        if(connection == null){
            initializeConnection();
        }

        return connection;
    }
}
