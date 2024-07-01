package org.example.command;

import org.example.managment.ResultResponse;

import java.sql.SQLException;

public interface BaseCommand {

    ResultResponse action();

}
