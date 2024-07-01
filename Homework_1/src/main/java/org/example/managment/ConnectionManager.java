package org.example.managment;

import org.example.enumManagment.ResponseEnum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/coworking";
    private static final String USER = "sushka";
    private static final String PASSWORD = "12345";
    public static Connection connection = null;
    public static void registeringConnection(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println(ResponseEnum.SQL_ERROR);
        }
    }

    public static void setConnection(Connection connection) {
        ConnectionManager.connection = connection;
    }
}
