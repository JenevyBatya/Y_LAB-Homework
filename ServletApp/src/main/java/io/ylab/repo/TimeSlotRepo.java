package io.ylab.repo;

import io.ylab.managment.ConnectionManager;
import io.ylab.model.Booking;

import java.sql.*;
import java.time.LocalDateTime;

public class TimeSlotRepo {
    private final ChamberRepo chamberRepo = new ChamberRepo();

    public void putNewDayCoworkingSlots(Date date, int chamberId) throws SQLException {
        int capacity = chamberRepo.findCapacityOfChamber(chamberId);
        for (int hour = 0; hour < 24; hour++) {
            putNewHourCoworkingSlot(date, hour, capacity, chamberId);
        }
    }

    public void putNewHourCoworkingSlot(Date date, int hour, int capacity, int chamberId) throws SQLException {
        String sql = "INSERT INTO example.time_slot (date, hour, chamber_id, booked_slots, total_slots) VALUES (?,?,?,?,?)";
        PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
        ps.setDate(1, date);
        ps.setInt(2, hour);
        ps.setInt(3, chamberId);
        ps.setInt(4, capacity);
        ps.setInt(5, capacity);
        ps.executeUpdate();
        ConnectionManager.getConnection().commit();
    }


    public ResultSet getCoworkingTimeSlotsInPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime, Booking newBooking) throws SQLException {
        String sql = "SELECT * FROM example.time_slot WHERE chamber_number=? AND ? < date AND date<? OR (date=? AND hour<=? OR date=? AND hour=?) ";
        PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
        ps.setInt(1, newBooking.getChamberNumber());
        ps.setDate(2, Date.valueOf(startDateTime.toLocalDate()));
        ps.setDate(3, Date.valueOf(endDateTime.toLocalDate()));
        ps.setDate(4, Date.valueOf(startDateTime.toLocalDate()));
        ps.setInt(5, startDateTime.getHour());
        ps.setDate(6, Date.valueOf(endDateTime.toLocalDate()));
        ps.setInt(7, endDateTime.getHour());
        return ps.executeQuery();
    }
}
