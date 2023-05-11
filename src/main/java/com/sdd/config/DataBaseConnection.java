package com.sdd.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnection {
    private static final String DB_DRIVER_CLASS="com.mysql.jdbc.Driver";
    private static final String DB_USERNAME="root";
    private static final String DB_PASSWORD="TkhQUkAxMjM0IzE5OTM=";
    private static final String DB_URL ="jdbc:mysql://20.204.27.38:3306/napr";
    public static Connection getConnection() {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", DB_USERNAME);
        connectionProps.put("password", DB_PASSWORD);
//                Class.forName(connectionProps.getProperty(DB_DRIVER_CLASS));
        try {
            conn = DriverManager.getConnection(DB_URL, connectionProps);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Connected to database");
        return conn;
    }

    public static Connection getConnection1() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://20.204.27.38:3306/napr",DB_USERNAME, DB_PASSWORD);
            System.out.println("databaseConnection===============" + con.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("databaseConnection===============" + e.toString());
        }

        return con;
    }


}