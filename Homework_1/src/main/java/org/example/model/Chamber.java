package org.example.model;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ResultResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.HashMap;

public class Chamber {
    private int number;
    private String name;
    private String description;
    private HashMap<LocalDate, ArrayList<Booking>> timeTable = new HashMap<>();
    private LocalDate today = LocalDate.now();
    private LocalDate plusThreeMonthsDay = today.plusMonths(3);
    private ChamberTypeEnum chamberTypeEnum;
    private int peopleAmount;

    private HashMap<LocalDate, HashMap<LocalDateTime, Integer>> coworkingTimeSlot = new HashMap<>();

    public Chamber() {
    }

    public Chamber(int number, String name, String description, HashMap<LocalDate, ArrayList<Booking>> timeTable, ChamberTypeEnum chamberTypeEnum, int peopleAmount) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.timeTable = timeTable;
        this.chamberTypeEnum = chamberTypeEnum;
        this.peopleAmount = peopleAmount;

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
                    if (coworkingTimeSlot.get(currentDay).isEmpty()) {
                        coworkingTimeSlot.put(currentDay, new HashMap<>());
                        coworkingTimeSlot.get(currentDay).put(currentDayTime, peopleAmount);
                    } else {
                        coworkingTimeSlot.get(currentDay).put(currentDayTime, peopleAmount);
                    }
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


    public ResultResponse add(Booking newBooking) {
        ResultResponse resultResponse = new ResultResponse();
        LocalDateTime startDateTime = newBooking.getStartDate();
        LocalDate startDate = newBooking.getStartDate().toLocalDate();


        LocalDate date = newBooking.getStartDate().toLocalDate();

        LocalDateTime endDateTime = newBooking.getEndDate();
        LocalDate endDate = newBooking.getEndDate().toLocalDate();
        if (date.isBefore(today) && endDate.isAfter(plusThreeMonthsDay)) {
            //TODO
            resultResponse.setStatus(false);
            resultResponse.setResponse(ResponseEnum.WRONG_DATA);
            return resultResponse;
        }
        if (chamberTypeEnum == ChamberTypeEnum.HALL) {

            while (!date.isAfter(endDate)) {

                ArrayList<Booking> bookings = timeTable.get(date);
                if (bookings == null){
                    continue;
                }
                for (Booking booking : bookings) {
                    if (booking.getStartDate().isAfter(newBooking.getStartDate()) && booking.getEndDate().isBefore(newBooking.getEndDate())) {
                        resultResponse.setStatus(false);
                        resultResponse.setResponse(ResponseEnum.OCCUPIED);
                        return resultResponse;
                    }
                }
                date = date.plusDays(1);
            }
            date = newBooking.getStartDate().toLocalDate();
            Booking helperBooking;
            while (!date.isAfter(endDate)) {
                if (date.equals(startDate) && date.equals(endDate)) {
                    timeTable.get(date).add(newBooking);
                    break;
                } else {
                    if (date.equals(startDate)) {
                        helperBooking = newBooking.clone();
                        helperBooking.setEndDate(date.atTime(LocalTime.of(23, 59)));
                    } else if (date.equals(endDate)) {
                        helperBooking = newBooking.clone();
                        helperBooking.setStartDate(date.atTime(LocalTime.of(0, 0)));
                    } else {
                        helperBooking = newBooking.clone();
                        helperBooking.setStartDate(date.atTime(LocalTime.of(0, 0)));
                        helperBooking.setEndDate(date.atTime(LocalTime.of(23, 59)));
                    }
                }
                timeTable.get(date).add(helperBooking);
                date = date.plusDays(1);
            }
            resultResponse.setStatus(true);
            resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
            newBooking.getUser().addBooking(newBooking);
            return resultResponse;


        } else {
            while (!date.isAfter(endDate)) {
                LocalDateTime currentDateTime = newBooking.getStartDate();
                HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots = coworkingTimeSlot.get(date);
                while (currentDateTime != newBooking.getEndDate() && currentDateTime.getHour() != 23) {
                    if (coworkingCurrentDateTimeSlots.get(currentDateTime) == 0) {
                        resultResponse.setStatus(false);
                        resultResponse.setResponse(ResponseEnum.OCCUPIED);
                        return resultResponse;
                    }
                }
                if (currentDateTime.getHour() == 23 && !currentDateTime.equals(newBooking.getEndDate())) {
                    if (coworkingCurrentDateTimeSlots.get(currentDateTime) == 0) {
                        resultResponse.setStatus(false);
                        resultResponse.setResponse(ResponseEnum.OCCUPIED);
                        return resultResponse;
                    }
                }
                date = date.plusDays(1);
            }

            date = newBooking.getStartDate().toLocalDate();

            while (!date.isAfter(endDate)) {

                HashMap<LocalDateTime, Integer> coworkingCurrentDateTimeSlots = coworkingTimeSlot.get(date);
                if (date.equals(startDate) && date.equals(endDate)) { //Бронь на один день
                    LocalDateTime currentDateTime = startDateTime;
                    while (currentDateTime != endDateTime) {
                        coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                        currentDateTime = currentDateTime.plusHours(1);
                    }
                    break;
                } else { //Бронь на несколько дней
                    if (date.equals(startDate)) { //Первый день
                        LocalDateTime currentDateTime = startDateTime;
                        while (currentDateTime.getHour() != 23) {
                            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                            currentDateTime = currentDateTime.plusHours(1);
                        }
                        coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);

                    } else if (date.equals(endDate)) { //Последний день
                        LocalDateTime currentDateTime = startDateTime.toLocalDate().atStartOfDay();
                        while (currentDateTime != endDateTime) {
                            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                            currentDateTime = currentDateTime.plusHours(1);
                        }
                    } else { //В диапазоне
                        LocalDateTime currentDateTime = startDateTime.toLocalDate().atStartOfDay();
                        while (currentDateTime.getHour() != 23) {
                            coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                            currentDateTime = currentDateTime.plusHours(1);
                        }
                        coworkingCurrentDateTimeSlots.computeIfPresent(currentDateTime, (key, value) -> value - 1);
                    }
                }
                date = date.plusDays(1);
            }
            resultResponse.setStatus(true);
            resultResponse.setResponse(ResponseEnum.SUCCESS_BOOKING);
            newBooking.getUser().addBooking(newBooking);
            return resultResponse;
        }
    }

    public ChamberTypeEnum getChamberTypeEnum() {
        return chamberTypeEnum;
    }


    public HashMap<LocalDate, HashMap<LocalDateTime, Integer>> getCoworkingTimeSlot() {
        return coworkingTimeSlot;
    }

}
