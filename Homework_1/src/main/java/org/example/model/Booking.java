package org.example.model;


import java.time.LocalDateTime;

public class Booking implements Cloneable {
    private static int id = 0;
    private User user;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int chamberNumber;

    public void upId() {
        id++;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getChamberNumber() {
        return chamberNumber;
    }

    public Booking(User user, LocalDateTime startDate, LocalDateTime endDate, int chamberNumber) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.chamberNumber = chamberNumber;
        upId();
    }


    @Override
    public Booking clone() {
        try {
            return (Booking) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
