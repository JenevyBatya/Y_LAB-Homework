package org.example;

import org.example.managment.ChamberManager;
import org.example.managment.CommandManager;

public class Main {
    public static void main(String[] args) {
        ChamberManager chamberManager = new ChamberManager();
        CommandManager commandManager = new CommandManager();
        chamberManager.registerChambers();
        commandManager.registerChambers(chamberManager);
        commandManager.registerCommands();
        commandManager.run();

    }
}