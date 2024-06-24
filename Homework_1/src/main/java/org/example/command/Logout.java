package org.example.command;

import org.example.enumManagment.CommandNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Chamber;

import java.util.HashMap;

public class Logout extends BaseCommandAbs implements BaseCommand {

    public Logout(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Logout.class.getName());
    }

    

    @Override
    public ResultResponse action() {
        try {
            checkingAuthorization();
            userManager.setAuthorized(false);
            userManager.setUser(null);
        } catch (IllegalAccessException e) {
            return new ResultResponse(false, ResponseEnum.NO_AUTHORIZATION_YET);
        }
        return new ResultResponse(true, ResponseEnum.SUCCESS_LOGOUT);
    }
}
