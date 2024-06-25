package org.example.command;

import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;

import static org.example.managment.UserManager.*;

public class Registration extends BaseCommandAbs implements BaseCommand {

    public Registration(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Registration.class.getName());
    }

    

    @Override
    public ResultResponse action() {
        if (userManager.isAuthorized()) {
            return new ResultResponse(false, ResponseEnum.ALREADY_AUTHORIZED);
        }
        try {
            String[] nameSurname = gettingNameAndSurname().getData().split(" ");
            String name = nameSurname[0];
            String surname = nameSurname[1];
            String email = gettingEmail().getData();
            String phoneNumber = gettingPhoneNumber().getData();
            String password = gettingPassword().getData();
            return userManager.registering(name, surname, email, phoneNumber, password);

        } catch (GettingBackToMain e) {
            return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
        }
    }
}
