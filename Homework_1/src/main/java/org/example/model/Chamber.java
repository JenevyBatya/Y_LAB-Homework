package org.example.model;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ResultResponse;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.example.managment.ConnectionManager.connection;

public class Chamber {
    private final int number;
    private final String name;
    private final String description;
    private static HashMap<LocalDate, ArrayList<Booking>> timeTable = null;
    private static final LocalDate today = LocalDate.now();
    private static final LocalDate plusThreeMonthsDay = today.plusMonths(3);
    private static ChamberTypeEnum chamberTypeEnum = null;
    private static final HashMap<LocalDate, HashMap<LocalDateTime, Integer>> coworkingTimeSlot = new HashMap<>();

    public Chamber(int number, String name, String description, HashMap<LocalDate, ArrayList<Booking>> timeTable, ChamberTypeEnum chamberTypeEnum, int peopleAmount) {
        this.number = number;
        this.name = name;
        this.description = description;
        Chamber.timeTable = timeTable;
        Chamber.chamberTypeEnum = chamberTypeEnum;

        LocalDate currentDay = LocalDate.now();
        if (chamberTypeEnum == ChamberTypeEnum.HALL) {
            while (!currentDay.isAfter(plusThreeMonthsDay)) {
                timeTable.put(currentDay, new ArrayList<>());
                currentDay = currentDay.plusDays(1);
            }
        } else {
            while (!currentDay.isAfter(plusThreeMonthsDay)) {
                coworkingTimeSlot.put(currentDay, new HashMap<>());
                LocalDateTime currentDayTime = currentDay.atStartOfDay();
                while (currentDayTime.getHour() != 23) {
                    coworkingTimeSlot.get(currentDay).put(currentDayTime, peopleAmount);
                    currentDayTime = currentDayTime.plusHours(1);
                }
                coworkingTimeSlot.get(currentDay).put(currentDayTime, peopleAmount);
                currentDay = currentDay.plusDays(1);
            }
        }
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<LocalDate, ArrayList<Booking>> getTimeTable() {
        return timeTable;
    }

    public LocalDate getToday() {
        return today;
    }

    public LocalDate getPlusThreeMonthsDay() {
        return plusThreeMonthsDay;
    }

    public ChamberTypeEnum getChamberTypeEnum() {
        return chamberTypeEnum;
    }

    public HashMap<LocalDate, HashMap<LocalDateTime, Integer>> getCoworkingTimeSlot() {
        return coworkingTimeSlot;
    }

    public static ResultResponse add(Booking newBooking) {
        ResultResponse resultResponse;

        if (isBookingDateInvalid(newBooking)) {
            return createErrorResponse(ResponseEnum.WRONG_DATA);
        }

        if (isChamberTypeHall()) {
            resultResponse = handleHallBooking(newBooking);
        } else {
            resultResponse = handleCoworkingBooking(newBooking);
        }

        if (resultResponse.isStatus()) {
            try {
                addBookingDB(newBooking);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return resultResponse;
    }

    private static boolean isBookingDateInvalid(Booking newBooking) {
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        return startDate.isBefore(today) || endDate.isAfter(plusThreeMonthsDay);
    }

    private static ResultResponse createErrorResponse(ResponseEnum responseEnum) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setStatus(false);
        resultResponse.setResponse(responseEnum);
        return resultResponse;
    }

    private static boolean isChamberTypeHall() {
        return chamberTypeEnum == ChamberTypeEnum.HALL;
    }

    private static ResultResponse handleHallBooking(Booking newBooking) {
        if (isHallBookingOccupied(newBooking)) {
            return createErrorResponse(ResponseEnum.OCCUPIED);
        }

        addHallBookings(newBooking);

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setStatus(true);
        resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
        return resultResponse;
    }

    private static boolean isHallBookingOccupied(Booking newBooking) throws SQLException {
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        LocalDate date = startDate;
        String sql = "SELECT ";
        PreparedStatement ps = connection.prepareStatement(sql);
        while (!date.isAfter(endDate)) {

            ArrayList<Booking> bookings = timeTable.get(date);
            if (bookings != null) {
                for (Booking booking : bookings) {
                    if (booking.getStartDate().isBefore(newBooking.getEndDate()) &&
                            booking.getEndDate().isAfter(newBooking.getStartDate())) {
                        return true;
                    }
                }
            }
            date = date.plusDays(1);
        }
        return false;
    }

    private static void addHallBookings(Booking newBooking) {
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            Booking helperBooking;
            if (date.equals(startDate) && date.equals(endDate)) {
                timeTable.get(date).add(newBooking);
                break;
            } else {
                helperBooking = newBooking.clone();
                if (date.equals(startDate)) {
                    helperBooking.setEndDate(date.atTime(LocalTime.of(23, 59)));
                } else if (date.equals(endDate)) {
                    helperBooking.setStartDate(date.atTime(LocalTime.of(0, 0)));
                } else {
                    helperBooking.setStartDate(date.atTime(LocalTime.of(0, 0)));
                    helperBooking.setEndDate(date.atTime(LocalTime.of(23, 59)));
                }
                timeTable.get(date).add(helperBooking);
            }
            date = date.plusDays(1);
        }
    }

    private static ResultResponse handleCoworkingBooking(Booking newBooking) {
        if (isCoworkingBookingOccupied(newBooking)) {
            return createErrorResponse(ResponseEnum.OCCUPIED);
        }

        addCoworkingBookings(newBooking);

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setStatus(true);
        resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
        return resultResponse;
    }

    private static boolean isCoworkingBookingOccupied(Booking newBooking) {
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            LocalDateTime currentDateTime = newBooking.getStartDate();
            HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots = coworkingTimeSlot.get(date);

            while (currentDateTime.isBefore(newBooking.getEndDate())) {
                if (coworkingCurrentDateTimeSlots.getOrDefault(currentDateTime, 0) == 0) {
                    return true;
                }
                currentDateTime = currentDateTime.plusHours(1);
                if (currentDateTime.getHour() == 0) {
                    break;
                }
            }

            date = date.plusDays(1);
        }
        return false;
    }

    private static void addCoworkingBookings(Booking newBooking) {
        LocalDateTime startDateTime = newBooking.getStartDate();
        LocalDateTime endDateTime = newBooking.getEndDate();
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots = coworkingTimeSlot.get(date);
            if (date.equals(startDate) && date.equals(endDate)) {
                LocalDateTime currentDateTime = startDateTime;
                while (currentDateTime.isBefore(endDateTime)) {
                    coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                    currentDateTime = currentDateTime.plusHours(1);
                }
                break;
            } else {
                addBookingForCoworkingDay(startDateTime, endDateTime, date, coworkingCurrentDateTimeSlots);
            }
            date = date.plusDays(1);
        }
    }

    private static void addBookingForCoworkingDay(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate date, HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots) {
        if (date.equals(startDateTime.toLocalDate())) {
            LocalDateTime currentDateTime = startDateTime;
            while (currentDateTime.getHour() != 23) {
                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                currentDateTime = currentDateTime.plusHours(1);
            }
            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
        } else if (date.equals(endDateTime.toLocalDate())) {
            LocalDateTime currentDateTime = date.atStartOfDay();
            while (currentDateTime.isBefore(endDateTime)) {
                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                currentDateTime = currentDateTime.plusHours(1);
            }
        } else {
            LocalDateTime currentDateTime = date.atStartOfDay();
            while (currentDateTime.getHour() != 23) {
                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                currentDateTime = currentDateTime.plusHours(1);
            }
            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
        }
    }

    private static void addBookingDB(Booking booking) throws SQLException {
        String sql = "INSERT INTO example.booking (user_id, chamber_id, start_date, end_date, start_time, end_time, is_coworking) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, booking.getUser().getId());
        ps.setInt(2, booking.getChamberId());
        ps.setDate(3, Date.valueOf(booking.getStartDate().toLocalDate()));
        ps.setDate(4, Date.valueOf(booking.getEndDate().toLocalDate()));
        ps.setTime(5, Time.valueOf(booking.getStartDate().toLocalTime()));
        ps.setTime(6, Time.valueOf(booking.getEndDate().toLocalTime()));
        ps.setBoolean(7, false);
        ps.executeQuery();
        connection.commit();

    }
}
