package org.example;

import org.example.managment.ChamberManager;
import org.example.managment.CommandManager;

import java.sql.*;

public class Main {


    public static void main(String[] args) {
            CommandManager commandManager = new CommandManager();
            commandManager.run();

    }
}