//package com.sdd.config;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//public class DataBaseConnection {
//    private static final String DB_DRIVER_CLASS="oracle.jdbc.driver.OracleDriver";
//    private static final String DB_USERNAME="budget";
//    private static final String DB_PASSWORD="budget123";
//    private static final String DB_URL ="jdbc:oracle:thin:@172.18.0.105:1521:yatra";
//    public static Connection getConnection() {
//        Connection conn = null;
//        Properties connectionProps = new Properties();
//        connectionProps.put("user", DB_USERNAME);
//        connectionProps.put("password", DB_PASSWORD);
////                Class.forName(connectionProps.getProperty(DB_DRIVER_CLASS));
//        try {
//            conn = DriverManager.getConnection(DB_URL, connectionProps);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Connected to database");
//        return conn;
//    }
//
//
//}
