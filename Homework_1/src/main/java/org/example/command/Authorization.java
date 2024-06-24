package org.example.command;

import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import static org.example.managment.UserManager.*;

public class Authorization extends BaseCommandAbs implements BaseCommand {

    public Authorization(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Authorization.class.getName());
    }

    

    @Override
    public ResultResponse action() {
        if (userManager.isAuthorized()) {
            return new ResultResponse(false, ResponseEnum.ALREADY_AUTHORIZED);
        }
        while (true) {
            try {
                String email = gettingEmail().getData();
                String password = gettingPassword().getData();
                ResultResponse resultResponse = userManager.authorizing(email, password);
                if (resultResponse.getResponse().equals(ResponseEnum.SUCCESS_AUTH)) {
                    return resultResponse;
                } else {
                    System.out.println(resultResponse.getResponse().toString());
                }

            } catch (GettingBackToMain e) {
                return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
            }
        }
    }
}
