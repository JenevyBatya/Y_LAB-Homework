package io.ylab.model;

import io.ylab.managment.UserManager;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
//    private final int chamberId;
    private int chamberNumber;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Booking(int chamberNumber, LocalDateTime startDate, LocalDateTime endDate) {
        this.chamberNumber = chamberNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        userId = UserManager.getUserId();

    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getChamberNumber() {
        return chamberNumber;
    }
    //    public int getChamberId() {
//        return chamberId;
//    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
