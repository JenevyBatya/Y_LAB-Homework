package org.example.command;

import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.example.managment.ConnectionManager.connection;
import static org.example.model.Chamber.isCoworking;

public class Delete extends BaseCommandAbs implements BaseCommand {
    private static PreparedStatement ps;
    private static String sql;

    public Delete(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Delete.class.getName());
    }


    @Override
    public ResultResponse action() {
        while (true) {
            try {
                checkingAuthorization();
                ArrayList<Booking> bookings;
                sql = "SELECT * FROM example.booking WHERE user_id=?";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userManager.getUser().getId());
                ResultSet resultSet = ps.executeQuery();
                bookings = addBookings(resultSet);
                if (bookings.isEmpty()) {
                    return new ResultResponse(true, ResponseEnum.NO_BOOKED_ROOMS);
                }
                for (int i = 0; i < bookings.size(); i++) {
                    Booking booking = bookings.get(i);
                    System.out.printf("%d Аудитория %d %s - %s", i + 1, findChamber(booking.getChamberId()), booking.getStartDate(), booking.getEndDate() + "\n");
                }
                System.out.println("Какую резервацию вы хотите отменить?");
                int num = Integer.parseInt(commandOrBackOption()) - 1;
                if (num >= 0 && num <= bookings.size()) {
                    int chamberId = bookings.get(num).getChamberId();
                    deleteChamber(chamberId);
                    if (isCoworking(chamberId)) {
                        recoverTimeSlotTable(bookings.get(num));
                    }
                    return new ResultResponse(true, ResponseEnum.SUCCESS_DELETE);
                }
                System.out.println("Резервации под этим номером не существует");
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

    public static ArrayList<Booking> addBookings(ResultSet resultSet) throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        while (resultSet.next()) {
            Booking booking = new Booking();
            int chamberId = resultSet.getInt("chamber_id");
            Date startDate = resultSet.getDate("start_date");
            Date endDate = resultSet.getDate("end_date");

            Time startTime = resultSet.getTime("start_time");
            Time endTime = resultSet.getTime("end_time");

            LocalDate startLocalDate = startDate.toLocalDate();
            LocalDate endLocalDate = endDate.toLocalDate();
            LocalTime startLocalTime = startTime.toLocalTime();
            LocalTime endLocalTime = endTime.toLocalTime();

            LocalDateTime startLocalDateTime = startLocalDate.atTime(startLocalTime);
            LocalDateTime endLocalDateTime = endLocalDate.atTime(endLocalTime);

            booking.setChamberId(chamberId);
            booking.setStartDate(startLocalDateTime);
            booking.setEndDate(endLocalDateTime);
            bookings.add(booking);
        }
        return bookings;
    }

    public static int findChamber(int chamberId) throws SQLException {
        sql = "SELECT * FROM example.chamber WHERE chamber_id=?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, chamberId);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.getInt("chamber_id");
    }

    public void deleteChamber(int chamberId) throws SQLException {
        sql = "DELETE FROM example.booking WHERE chamber_id=?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, chamberId);
        ps.executeQuery();
        connection.commit();
    }

    public void recoverTimeSlotTable(Booking booking) throws SQLException {
        LocalDateTime endTime = booking.getEndDate();
        LocalDateTime currentTime = booking.getStartDate();

        while (!currentTime.isAfter(endTime)) {
            sql = "SELECT * FROM example.time_slot WHERE chamber_id=? AND date=? And hour=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, booking.getChamberId());
            ps.setDate(2, Date.valueOf(currentTime.toLocalDate()));
            ps.setInt(3, currentTime.getHour());
            ResultSet resultSet = ps.executeQuery();
            int booked_slots = resultSet.getInt("booked_slots");
            sql = "UPDATE example.time_slots SET booked_slots = ? WHERE chamber_id=? AND date=? And hour=?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, booked_slots + 1);
            ps.setInt(2, booking.getChamberId());
            ps.setDate(3, Date.valueOf(currentTime.toLocalDate()));
            ps.setInt(4, currentTime.getHour());
            ps.executeUpdate();
            connection.commit();
            currentTime = currentTime.plusHours(1);
        }

    }
}
