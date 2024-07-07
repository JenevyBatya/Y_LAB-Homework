package io.ylab.service;

import io.ylab.managment.ConnectionManager;
import io.ylab.managment.ResultResponse;
import io.ylab.managment.UserManager;
import io.ylab.managment.enums.ResponseEnum;
import io.ylab.model.Booking;
import io.ylab.model.TimeSlot;
import io.ylab.repo.BookingRepo;
import io.ylab.repo.ChamberRepo;
import io.ylab.repo.TimeSlotRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;


public class BookingService {
    private final BookingRepo bookingRepo = new BookingRepo();
    private final TimeSlotRepo timeSlotRepo = new TimeSlotRepo();
    private final ChamberRepo chamberRepo = new ChamberRepo();

    public ResultResponse addBooking(Booking booking) throws SQLException {
        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            if (chamberRepo.isCoworking(booking.getChamberNumber())) {
                return handleCoworkingBooking(booking);
            } else {
                return handleHallBooking(booking);
            }
        }
        return new ResultResponse(false, ResponseEnum.INVALID_DATA);
    }

    public ResultResponse deleteBooking(int bookingId) {
        return null;
    }


    //Вспомогательные функции

    /*
     * Бронирование зала
     */
    private ResultResponse handleHallBooking(Booking newBooking) throws SQLException {
        if (isHallBookingOccupied(newBooking)) {
            return new ResultResponse(false, ResponseEnum.CHAMBER_OCCUPIED);
        }
        return bookingRepo.save(newBooking);
    }

    /*
     * Проверка занятости по выбранному времени зала
     */
    private boolean isHallBookingOccupied(Booking newBooking) throws SQLException {
        Date startDate = Date.valueOf(newBooking.getStartDate().toLocalDate());
        Date endDate = Date.valueOf(newBooking.getEndDate().toLocalDate());
        ResultSet resultSet = bookingRepo.getBookingsInPeriod(startDate, endDate, newBooking.getChamberNumber());
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
    /*
     * Бронирование места в коворкинге
     */

    private ResultResponse handleCoworkingBooking(Booking newBooking) throws SQLException {
        TreeMap<Date, ArrayList<TimeSlot>> timeMap = new TreeMap<>();
        if (isCoworkingBookingOccupied(newBooking, timeMap)) {
            return new ResultResponse(false, ResponseEnum.CHAMBER_OCCUPIED);
        }
        return addCoworkingBookings(newBooking, timeMap);
    }

    /*
     * Проверка занятости по выбранному времени мест в коворккинге
     */
    private boolean isCoworkingBookingOccupied(Booking newBooking, TreeMap<Date, ArrayList<TimeSlot>> timeMap) throws SQLException {
        LocalDateTime startDateTime = newBooking.getStartDate();
        LocalDateTime currentDateTime = newBooking.getStartDate();
        LocalDateTime endDateTime = newBooking.getEndDate();
        ResultSet resultSet = timeSlotRepo.getCoworkingTimeSlotsInPeriod(startDateTime, endDateTime, newBooking);
        getTimeMapForCoworking(resultSet, timeMap);

        int capacity = chamberRepo.findCapacityOfChamber(newBooking.getChamberNumber());
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
                Date date = Date.valueOf(currentDateTime.toLocalDate());
                int hour = currentDateTime.getHour();
                int chamberId = chamberRepo.findChamberId(newBooking.getChamberNumber());
                timeSlotRepo.putNewHourCoworkingSlot(date, hour, capacity, chamberId);
            }
            currentDateTime = currentDateTime.plusHours(1);
        }
        return false;
    }

    public void getTimeMapForCoworking(ResultSet resultSet, TreeMap<Date, ArrayList<TimeSlot>> timeMap) throws SQLException {
        while (resultSet.next()) {
            TimeSlot timeSlot = new TimeSlot();
            Date date = resultSet.getDate("date");
            timeSlot.setDate(date);
            timeSlot.setHour(resultSet.getInt("hour"));
            timeSlot.setChamberNumber(resultSet.getInt("chamber_number"));
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
    }

    private ResultResponse addCoworkingBookings(Booking newBooking, TreeMap<Date, ArrayList<TimeSlot>> timeMap) throws SQLException {
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
                putNewDayCoworkingSlots(Date.valueOf(currentDateTime.toLocalDate()), newBooking.getChamberId());
            }

            currentDateTime = currentDateTime.plusHours(1);
        }

    }


    public static void getTimeMapForHall(ResultSet resultSet, TreeMap<Date, ArrayList<String>> timeMap) throws SQLException {
        while (resultSet.next()) {
            Date date = resultSet.getDate("date");
            String[] startTimeFull = resultSet.getTime("start_time").toString().split(":");
            String[] endTimeFull = resultSet.getTime("end_time").toString().split(":");
            int startMinutes = Integer.parseInt(startTimeFull[1]);
            int endMinutes = Integer.parseInt(endTimeFull[1]);
            String startTime = startTimeFull[0] + ":" + startMinutes / 10 + startMinutes % 10;
            String endTime = endTimeFull[0] + ":" + endMinutes / 10 + endMinutes % 10;
            try {
                if (!timeMap.get(date).contains(startTime)) {
                    timeMap.get(date).add(startTime);
                } else {
                    timeMap.get(date).remove(startTime);
                }
                if (!timeMap.get(date).contains(endTime)) {
                    timeMap.get(date).add(endTime);
                } else {
                    timeMap.get(date).remove(endTime);
                }
            } catch (NullPointerException e) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("00:00");
                arrayList.add("23:59");

                timeMap.put(date, arrayList);
            }

        }
    }


    public static boolean isChamberExist(int chamber_num) throws SQLException {
        sql = "SELECT count(*) FROM example.chamber WHERE number=?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, chamber_num);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.getInt("count") != 0;

    }
}
