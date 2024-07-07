package io.ylab.repo;

import io.ylab.managment.ConnectionManager;
import io.ylab.managment.ResultResponse;
import io.ylab.managment.UserManager;
import io.ylab.managment.enums.ChamberTypeEnum;
import io.ylab.managment.enums.ResponseEnum;
import io.ylab.model.Booking;
import io.ylab.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class BookingRepo {

    private final Connection connection;
    private final ChamberRepo chamberRepo;

    public BookingRepo() {
        this.connection = ConnectionManager.getConnection();
        this.chamberRepo = new ChamberRepo();
    }

    public ResultResponse save(Booking booking) {
        try {
            String sql = "INSERT INTO example.booking (user_id, chamber_number, start_date, end_date, start_time, end_time, is_coworking) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getChamberNumber());
            ps.setDate(3, Date.valueOf(booking.getStartDate().toLocalDate()));
            ps.setDate(4, Date.valueOf(booking.getEndDate().toLocalDate()));
            ps.setTime(5, Time.valueOf(booking.getStartDate().toLocalTime()));
            ps.setTime(6, Time.valueOf(booking.getEndDate().toLocalTime()));
            ps.setBoolean(7, chamberRepo.isCoworking(booking.getChamberNumber()));
            ps.executeQuery();
            connection.commit();
            return new ResultResponse(true, ResponseEnum.BOOKING_SUCCESS_ADD);
        } catch (SQLException e) {
            return new ResultResponse(false, ResponseEnum.SQL_ERROR);
        }

    }


    //TODO
    public ResultResponse delete(int chamberId) {
        try {
            String sql = "DELETE FROM example.booking WHERE id=? AND user_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, chamberId);
            ps.setInt(2, UserManager.getUserId());
            int result = ps.executeUpdate();
            if (result == 1) {
                connection.commit();
                return new ResultResponse(true, ResponseEnum.BOOKING_SUCCESS_DELETE);
            }
            return new ResultResponse(false, ResponseEnum.BOOKING_FAILURE_DELETE);

        } catch (SQLException e) {
            return new ResultResponse(false, ResponseEnum.SQL_ERROR);
        }

    }

    public ResultSet getBookingsInPeriod(Date startDate, Date endDate, int chamberNumber) {
        try {
            String sql = "SELECT * FROM example.booking WHERE start_date >= ? AND  end_date <= ? AND chamber_number = ?";
            PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ps.setInt(3, chamberNumber);
            return ps.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ResultResponse getUserBookings() {
        ArrayList<Booking> bookings;
        String sql = "SELECT * FROM example.booking WHERE user_id=?";
        StringBuilder answer = new StringBuilder();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, UserManager.getUserId());
            ResultSet resultSet = ps.executeQuery();
            bookings = getBookings(resultSet);
            if (bookings.isEmpty()) {
                return new ResultResponse(true, ResponseEnum.NO_BOOKED_ROOMS);
            }
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                answer.append(String.format("%d - Аудитория %d %s - %s\n",
                        chamberRepo.findChamberId(booking.getChamberNumber()),
                        booking.getChamberNumber(),
                        booking.getStartDate(),
                        booking.getEndDate()));

            }
            return new ResultResponse(true, ResponseEnum.TEXT, answer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //Вспомогающие методы

    public ArrayList<Booking> getBookings(ResultSet resultSet) throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        while (resultSet.next()) {

            int chamberNumber = resultSet.getInt("chember_number");
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

            Booking booking = new Booking(chamberNumber, startLocalDateTime, endLocalDateTime);
            bookings.add(booking);
        }
        return bookings;
    }




}
