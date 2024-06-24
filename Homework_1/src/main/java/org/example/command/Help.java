package org.example.command;

import org.example.enumManagment.CommandNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;

public class Help extends BaseCommandAbs implements BaseCommand {

    public Help(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Help.class.getName());
    }

    

    @Override
    public ResultResponse action() {
        StringBuilder answer = new StringBuilder();
        for (CommandNameEnum command : CommandNameEnum.values()) {
            answer.append(command).append(": ").append(command.getText()).append("\n");
        }
        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());
    }
}
