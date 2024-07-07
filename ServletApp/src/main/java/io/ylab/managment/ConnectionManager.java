package io.ylab.managment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/coworking";
    private static final String USER = "sushka";
    private static final String PASSWORD = "12345";
    private static Connection connection = null;

    public static void registeringConnection() {
        try {
            Connection preConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            setConnection(preConnection);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setConnection(Connection connection) {
        ConnectionManager.connection = connection;
    }

    public static Connection getConnection() {
        return connection;
    }
}
