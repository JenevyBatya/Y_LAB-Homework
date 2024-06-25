package org.example.command;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.HelperNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Chamber;
import org.example.model.Role;

import java.util.HashMap;

public class ExpertMode extends BaseCommandAbs implements BaseCommand {
    public ExpertMode(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, ExpertMode.class.getName());
    }

    @Override
    public ResultResponse action() {
        while (true) {
            try {
                checkingAuthorization();
                if (userManager.getUser().getRole() != Role.ADMIN) {
                    throw new IllegalArgumentException();
                }
                HelperNameEnum[] text = new HelperNameEnum[]{HelperNameEnum.Create, HelperNameEnum.Delete};
                for (HelperNameEnum helper : text) {
                    System.out.println(helper + ": " + helper.getText());
                }
                String line = commandOrBackOption();
                switch (line) {
                    case "Add":
                        return addChamber();
                    case "Delete":
                        return deleteChamber();
                    default:
                        System.out.println(ResponseEnum.UNKNOWN_COMMAND);
                }
            } catch (IllegalAccessException e) {
                return new ResultResponse(false, ResponseEnum.NO_AUTHORIZATION_YET);
            } catch (GettingBackToMain e) {
                return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
            } catch (IllegalArgumentException e) {
                return new ResultResponse(false, ResponseEnum.ACCESS_DENIED);
            }
        }
    }

    public ResultResponse addChamber() throws GettingBackToMain {
        int num = Integer.parseInt(getChamberManager().gettingNumber().getData());
        String name = getChamberManager().gettingName().getData();
        String description = getChamberManager().gettingDescription().getData();
        ChamberTypeEnum chamberTypeEnum = getChamberManager().gettingType().getChamberTypeEnum();
        int amount = Integer.parseInt(getChamberManager().gettingAudienceCapacity().getData());
        chamberList.put(num, new Chamber(num, name, description, new HashMap<>(), chamberTypeEnum, amount));
        return new ResultResponse(true, ResponseEnum.SUCCESS_ADD);
    }

    public ResultResponse deleteChamber() throws GettingBackToMain {
        int num = Integer.parseInt(getChamberManager().gettingNumberToDelete().getData());
        chamberList.remove(num);
        return new ResultResponse(true, ResponseEnum.SUCCESS_DELETE_CHAMBER);

    }

}
