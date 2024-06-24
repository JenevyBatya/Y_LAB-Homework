package org.example.command;

import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;

import java.util.ArrayList;

public class Delete extends BaseCommandAbs implements BaseCommand {

    public Delete(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Delete.class.getName());
    }

    

    @Override
    public ResultResponse action() {
        while (true) {
            try {
                checkingAuthorization();
                ArrayList<Booking> bookings = userManager.getUser().getBookingList();
                if (bookings.isEmpty()) {
                    return new ResultResponse(true, ResponseEnum.NO_BOOKED_ROOMS);
                }
                for (int i = 0; i < bookings.size(); i++) {
                    Booking booking = bookings.get(i);
                    System.out.printf("%d Аудитория %d %s - %s", i + 1, booking.getChamberNumber(), booking.getStartDate(), booking.getEndDate() + "\n");
                }
                System.out.println("Какую резервацию вы хотите отменить?");
                int num = Integer.parseInt(commandOrBackOption()) - 1;
                if (num >= 0 && num <= bookings.size()) {
                    bookings.remove(num);
                    return new ResultResponse(true, ResponseEnum.SUCCESS_DELETE);
                }
                System.out.println("Резервации под этим номером не существует");
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
