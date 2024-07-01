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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static org.example.managment.ConnectionManager.connection;

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
                HelperNameEnum[] text = new HelperNameEnum[]{HelperNameEnum.Add, HelperNameEnum.Delete};
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
        try {
            insertChamber(num, name, description, chamberTypeEnum.toString(), amount);
        } catch (SQLException e) {
            System.out.println(ResponseEnum.SQL_ERROR);
            return new ResultResponse(false, ResponseEnum.UNSUCCESS_ADD);
        }
        return new ResultResponse(true, ResponseEnum.SUCCESS_ADD);
    }

    public ResultResponse deleteChamber() throws GettingBackToMain {
        int num = Integer.parseInt(getChamberManager().gettingNumberToDelete().getData());
        String sql = "DELETE FROM example.chamber WHERE chamber_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, num);
            int result = ps.executeUpdate();
            if (result == 1) {
                return new ResultResponse(true, ResponseEnum.SUCCESS_DELETE_CHAMBER);
            }
        } catch (SQLException e) {
            System.out.println(ResponseEnum.SQL_ERROR);
            return new ResultResponse(false, ResponseEnum.UNSUCCESS_DELETE_CHAMBER);
        }
        return new ResultResponse(false, ResponseEnum.UNSUCCESS_DELETE_CHAMBER);
    }

    public void insertChamber(int num, String name, String description, String type, int amount) throws SQLException {
        String sql = "INSERT INTO example.chamber (number, name, description, capacity, type) VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, num);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, description);
        preparedStatement.setInt(4, amount);
        preparedStatement.setString(5, type);
        preparedStatement.executeUpdate();
        connection.commit();
    }

}
