package org.example.command;

import org.example.enumManagment.HelperNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.command.Delete.addBookings;
import static org.example.managment.ConnectionManager.connection;
import static org.example.model.Chamber.isChamberExist;

public class Read extends BaseCommandAbs implements BaseCommand {
    public Read(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Read.class.getName());
    }


    @Override
    public ResultResponse action() {
        String line;
        int num;
        StringBuilder answer = new StringBuilder();
        while (true) {
            try {
                checkingAuthorization();
                System.out.println(HelperNameEnum.NUMBER_READ.getText());

                line = commandOrBackOption(); //Проверка, является ли выходом

                num = Integer.parseInt(line);


                if (isChamberExist(num)) {

                    String sql = "SELECT * FROM example.booking WHERE user_id=? AND chamber_id=?";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, userManager.getUser().getId());
                    ps.setInt(2, num);
                    ResultSet resultSet = ps.executeQuery();
                    ArrayList<Booking> bookings = addBookings(resultSet);
                    for (Booking booking : bookings) {
                        answer.append("Аудитория ").append(num).append(": ").append(booking.getStartDate()).append(" - ").append(booking.getEndDate());

                    }
                    if (answer.isEmpty()) {
                        System.out.println("У вас отсутствуют резервации в отношении данной аудитории");


                    } else {
                        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());
                    }

                } else {
                    System.out.println("Данная аудитория отстутсвует");
                }

            } catch (IllegalAccessException e) {
                return new ResultResponse(false, ResponseEnum.NO_AUTHORIZATION_YET);
            } catch (GettingBackToMain e) {
                return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат номера аудитории или команды");

            } catch (SQLException e) {
                System.out.println(ResponseEnum.SQL_ERROR);
            }
        }
    }


}
