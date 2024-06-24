package org.example.command;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.HelperNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;

import java.util.ArrayList;

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

                if (chamberList.containsKey(num)) {
                    ArrayList<Booking> bookings = userManager.getUser().getBookingList();
                    for (Booking booking : bookings) {
                        if (booking.getUser() == userManager.getUser()) {

                            answer.append("Аудитория ").append(booking.getChamberNumber()).append(": ").append(booking.getStartDate()).append(" - ").append(booking.getEndDate());
                        }
                    }
                    if (answer.isEmpty()) {
                        if (chamberList.get(num).getChamberTypeEnum() == ChamberTypeEnum.HALL) {
//                            answer += "Вы не бронировали данную аудитоию";
                            System.out.println("Вы не бронировали данную аудитоию");
                        } else {
//                            answer += "Вы не бронировали место в данной аудитории";
                            System.out.println("Вы не бронировали место в данной аудитории");
                        }

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

            }
        }
    }

}
