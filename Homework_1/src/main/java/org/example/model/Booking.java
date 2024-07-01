package org.example.model;


import java.time.LocalDateTime;

public class Booking implements Cloneable {
    private static int id = 0;
    private User user;
    private int chamberId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
//    private int chamberNumber;

    public int getChamberId() {
        return chamberId;
    }

    public void setChamberId(int chamberId) {
        this.chamberId = chamberId;
    }

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


    public Booking() {
    }

    public Booking(User user, LocalDateTime startDate, LocalDateTime endDate, int chamberId) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.chamberId = chamberId;
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
