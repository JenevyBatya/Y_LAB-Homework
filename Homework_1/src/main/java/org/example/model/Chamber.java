package org.example.model;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ResultResponse;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.example.managment.ConnectionManager.connection;

public class Chamber {
    private final int number;
    private final String name;
    private final String description;
    private static HashMap<LocalDate, ArrayList<Booking>> timeTable = null;
    private static final LocalDate today = LocalDate.now();
    private static final LocalDate plusThreeMonthsDay = today.plusMonths(3);
    private static ChamberTypeEnum chamberTypeEnum = null;
    static PreparedStatement ps;
    static String sql;
    private static final HashMap<LocalDate, HashMap<LocalDateTime, Integer>> coworkingTimeSlot = new HashMap<>();
    static HashMap<Date, ArrayList<TimeSlot>> timeMap = new HashMap<>();
    static int capacity;

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
        try {
            if (isBookingDateInvalid(newBooking)) {
                return createErrorResponse(ResponseEnum.WRONG_DATA);
            }
            if (isChamberTypeHall()) {
                resultResponse = handleHallBooking(newBooking);
            } else {
                resultResponse = handleCoworkingBooking(newBooking);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    private static ResultResponse handleHallBooking(Booking newBooking) throws SQLException {
        if (isHallBookingOccupied(newBooking)) {
            return createErrorResponse(ResponseEnum.OCCUPIED);
        }
        addBookingDB(newBooking);
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setStatus(true);
        resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
        return resultResponse;
    }

    private static boolean isHallBookingOccupied(Booking newBooking) throws SQLException {
        LocalDate startDate = newBooking.getStartDate().toLocalDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        String sql = "SELECT * FROM example.booking WHERE start_date >= ? AND  end_date <= ? AND chamber_id == ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(startDate));
        ps.setDate(2, Date.valueOf(endDate));
        ps.setInt(3, newBooking.getChamberId());
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            Date date = resultSet.getDate("start_date");
            Time time = resultSet.getTime("start_time");

            LocalDate localDate = date.toLocalDate();
            LocalTime localTime = time.toLocalTime();
            LocalDateTime localDateTimeStart = localDate.atTime(localTime);

            date = resultSet.getDate("end_date");
            time = resultSet.getTime("end_time");

            localDate = date.toLocalDate();
            localTime = time.toLocalTime();
            LocalDateTime localDateTimeEnd = localDate.atTime(localTime);
            if (localDateTimeStart.isBefore(newBooking.getEndDate()) && localDateTimeEnd.isAfter(newBooking.getStartDate())) {
                return true;
            }
        }
        return false;
    }

    private static ResultResponse handleCoworkingBooking(Booking newBooking) throws SQLException {
        if (isCoworkingBookingOccupied(newBooking)) {
            return createErrorResponse(ResponseEnum.OCCUPIED);
        }
        addCoworkingBookings(newBooking);
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setStatus(true);
        resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
        return resultResponse;
    }

    private static boolean isCoworkingBookingOccupied(Booking newBooking) throws SQLException {
        LocalDateTime startDateTime = newBooking.getStartDate();
        LocalDateTime currentDateTime = newBooking.getStartDate();
        LocalDateTime endDateTime = newBooking.getEndDate();

        String sql = "SELECT * FROM example.time_slot WHERE chamber_id==? AND ? < date AND date<? OR (date==? AND hour<=? OR date==? AND hour==?) ";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, newBooking.getChamberId());
        ps.setDate(2, Date.valueOf(startDateTime.toLocalDate()));
        ps.setDate(3, Date.valueOf(endDateTime.toLocalDate()));
        ps.setDate(4, Date.valueOf(startDateTime.toLocalDate()));
        ps.setInt(5, startDateTime.getHour());
        ps.setDate(6, Date.valueOf(endDateTime.toLocalDate()));
        ps.setInt(7, endDateTime.getHour());
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            TimeSlot timeSlot = new TimeSlot();
            Date date = resultSet.getDate("date");
            timeSlot.setDate(date);
            timeSlot.setHour(resultSet.getInt("hour"));
            timeSlot.setChamberId(resultSet.getInt("chamber_id"));
            timeSlot.setBookedSlots(resultSet.getInt("booked_slots"));
            timeSlot.setTotalSlots(resultSet.getInt("total_slots"));
            try {
                timeMap.get(date).add(timeSlot);
            } catch (NullPointerException e) {
                ArrayList<TimeSlot> arrayList = new ArrayList<>();
                arrayList.add(timeSlot);
                timeMap.put(date, arrayList);
            }

        }
        sql = "SELECT capacity FROM example.chamber WHERE id==?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, newBooking.getChamberId());
        capacity = ps.executeQuery().getInt("capacity");
        if (capacity <= 0) {
            return true;
        }
        while (!currentDateTime.isAfter(endDateTime)) {
            try {
                ArrayList<TimeSlot> timeSlots = timeMap.get(Date.valueOf(currentDateTime.toLocalDate()));
                for (TimeSlot timeSlot : timeSlots) {
                    if (currentDateTime.getHour() == timeSlot.getHour()) {
                        if (timeSlot.getBookedSlots() - 1 <= 0) {
                            return true;
                        }
                    }
                }
            } catch (NullPointerException e) {
                continue;
            }
            currentDateTime = currentDateTime.plusHours(1);
        }
        return false;
    }

    private static void addCoworkingBookings(Booking newBooking) throws SQLException {
        LocalDateTime currentDateTime = newBooking.getStartDate();
        LocalDateTime endDateTime = newBooking.getEndDate();

        while (!currentDateTime.isAfter(endDateTime)) {
            try {
                ArrayList<TimeSlot> timeSlots = timeMap.get(Date.valueOf(currentDateTime.toLocalDate()));
                for (TimeSlot timeSlot : timeSlots) {
                    if (currentDateTime.getHour() == timeSlot.getHour()) {
                        timeSlot.setBookedSlots(timeSlot.getBookedSlots() - 1);
                    }
                }
            } catch (NullPointerException e) {
                sql = "INSERT INTO example.time_slot (date, hour, chamber_id, booked_slots, total_slots) VALUES (?,?,?,?,?)";
                ps = connection.prepareStatement(sql);
                ps.setDate(1, Date.valueOf(currentDateTime.toLocalDate()));
                ps.setInt(2, currentDateTime.getHour());
                ps.setInt(3, newBooking.getChamberId());
                ps.setInt(4, capacity - 1);
                ps.setInt(5, capacity);
            }

            currentDateTime = currentDateTime.plusHours(1);
        }

    }

//    private static void addBookingForCoworkingDay(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate date, HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots) {
//        if (date.equals(startDateTime.toLocalDate())) {
//            LocalDateTime currentDateTime = startDateTime;
//            while (currentDateTime.getHour() != 23) {
//                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
//                currentDateTime = currentDateTime.plusHours(1);
//            }
//            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
//        } else if (date.equals(endDateTime.toLocalDate())) {
//            LocalDateTime currentDateTime = date.atStartOfDay();
//            while (currentDateTime.isBefore(endDateTime)) {
//                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
//                currentDateTime = currentDateTime.plusHours(1);
//            }
//        } else {
//            LocalDateTime currentDateTime = date.atStartOfDay();
//            while (currentDateTime.getHour() != 23) {
//                coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
//                currentDateTime = currentDateTime.plusHours(1);
//            }
//            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
//        }
//    }

    private static void addBookingDB(Booking booking) throws SQLException {
        String sql = "INSERT INTO example.booking (user_id, chamber_id, start_date, end_date, start_time, end_time, is_coworking) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, booking.getUser().getId());
        ps.setInt(2, booking.getChamberId());
        ps.setDate(3, Date.valueOf(booking.getStartDate().toLocalDate()));
        ps.setDate(4, Date.valueOf(booking.getEndDate().toLocalDate()));
        ps.setTime(5, Time.valueOf(booking.getStartDate().toLocalTime()));
        ps.setTime(6, Time.valueOf(booking.getEndDate().toLocalTime()));
        ps.setBoolean(7, isCoworking(booking.getChamberId()));
        ps.executeQuery();
        connection.commit();
    }

    private static boolean isCoworking(int chamberId) throws SQLException {
        String sql = "SELECT type FROM example.chamber where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, chamberId);
        ResultSet resultSet = ps.getResultSet();
        return Objects.equals(resultSet.getString("type"), ChamberTypeEnum.HALL.toString());
    }
}
