package org.example.managment;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.command.*;
import org.example.enumManagment.ResponseEnum;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

import static org.example.managment.ConnectionManager.connection;

public class CommandManager {
    private final Hashtable<String, BaseCommand> commandTable = new Hashtable<>();
    private final UserManager userManager = new UserManager();
    private ChamberManager chamberManager;

    public CommandManager() {
        chamberManager = new ChamberManager();
        registerCommands();


        try {
            ConnectionManager.registeringConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db.changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("миграция успешна");
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }

    }

    public void registerCommands() {
        commandTable.put("Create", new Create(chamberManager, userManager));
        commandTable.put("Authorization", new Authorization(chamberManager, userManager));
        commandTable.put("Delete", new Delete(chamberManager, userManager));
        commandTable.put("ExpertMode", new ExpertMode(chamberManager, userManager));
        commandTable.put("Help", new Help(chamberManager, userManager));
        commandTable.put("Logout", new Logout(chamberManager, userManager));
        commandTable.put("Read", new Read(chamberManager, userManager));
        commandTable.put("Registration", new Registration(chamberManager, userManager));
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        BaseCommandAbs.setSc(sc);
        System.out.println(ResponseEnum.ONLY_3_MONTHS);
        while (true) {

            System.out.println("Вы в главном меню. Введите команду. Для получения списка команд введите Help");
            try {
                ResultResponse resultResponse = commandTable.get(sc.nextLine()).action();
                if (resultResponse.getResponse().equals(ResponseEnum.TEXT)) {
                    resultResponse.printData();
                } else {
                    resultResponse.printResponse();
                }
            } catch (NullPointerException e) {
                System.out.println("Неизвестная команда");
            }

        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Hashtable<String, BaseCommand> getCommandTable() {
        return commandTable;
    }

    public ChamberManager getChamberManager() {
        return chamberManager;
    }

    public void setChamberManager(ChamberManager chamberManager) {
        this.chamberManager = chamberManager;
    }
}
