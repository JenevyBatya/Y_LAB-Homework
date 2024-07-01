package org.example.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot {
    private int id;
    private Date date;
    private int hour;
    private int chamberId;
    private int bookedSlots;
    private int totalSlots;

    public TimeSlot() {
    }

    public TimeSlot(int id, int hour, int chamberId, int bookedSlots, int totalSlots) {
        this.id = id;
        this.hour = hour;
        this.chamberId = chamberId;
        this.bookedSlots = bookedSlots;
        this.totalSlots = totalSlots;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getChamberId() {
        return chamberId;
    }

    public void setChamberId(int chamberId) {
        this.chamberId = chamberId;
    }

    public int getBookedSlots() {
        return bookedSlots;
    }

    public void setBookedSlots(int bookedSlots) {
        this.bookedSlots = bookedSlots;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}