package io.ylab.model;

import java.sql.Date;

public class TimeSlot {
    private int id;
    private Date date;
    private int hour;
    private int chamberNumber;
    private int bookedSlots;
    private int totalSlots;

    public Date getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getChamberNumber() {
        return chamberNumber;
    }

    public int getBookedSlots() {
        return bookedSlots;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setChamberNumber(int chamberNumber) {
        this.chamberNumber = chamberNumber;
    }

    public void setBookedSlots(int bookedSlots) {
        this.bookedSlots = bookedSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }
}
