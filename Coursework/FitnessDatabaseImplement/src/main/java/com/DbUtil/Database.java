package com.DbUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {

    private static final String host = "192.168.1.71";
    private static final String database = "FitnessCoursework";
    private static final int port = 5434;
    private static final String user = "postgres";
    private static final String pass = "1234";
    private static String url = "jdbc:postgresql://%s:%d/%s";

    public static Connection dbConnection = null;

    public Database() {
        this.url = String.format(this.url, this.host, this.port, this.database);
        connect();
    }

    private void connect() {
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                dbConnection = DriverManager.getConnection(url, user, pass);
                System.out.println("connected:" + true);
                dbConnection.setAutoCommit(false);
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        try {
            if (dbConnection != null)
                dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return dbConnection;
    }
}