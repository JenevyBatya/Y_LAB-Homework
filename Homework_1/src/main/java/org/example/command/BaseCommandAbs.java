package org.example.command;

import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.UserManager;
import org.example.model.Chamber;

import java.util.HashMap;
import java.util.Scanner;

public class BaseCommandAbs {
    static Scanner sc;
    protected UserManager userManager;
    public String name;
    private ChamberManager chamberManager;

    public BaseCommandAbs(ChamberManager chamberManager, UserManager userManager, String name) {
        this.chamberManager = chamberManager;
//        this.chamberList = chamberManager.getChamberList();
        this.userManager = userManager;
        this.name = name;
    }

    public static void setSc(Scanner sc) {
        BaseCommandAbs.sc = sc;
    }

    public static String commandOrBackOption() throws GettingBackToMain {
        String line = sc.nextLine();
        if (line.equals("Back")) {
            throw new GettingBackToMain();
        } else {
            return line;
        }
    }

    public void checkingAuthorization() throws IllegalAccessException {
        if (!userManager.isAuthorized()) {
            throw new IllegalAccessException();
        }
    }

    public String getName() {
        return name;
    }

    public ChamberManager getChamberManager() {
        return chamberManager;
    }

}
