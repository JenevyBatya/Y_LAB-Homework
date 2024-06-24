package org.example.command;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.HelperNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;
import org.example.model.Chamber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.example.enumManagment.HelperNameEnum.*;

public class Create extends BaseCommandAbs implements BaseCommand {
    String formatterBookPattern = "dd.MM.yyyy HH.mm";
    String formatterPeriodPattern = "dd.MM.yyyy";
    String formatterCoworkingPattern = "dd.MM.yyyy HH";
    DateTimeFormatter formatterBook = DateTimeFormatter.ofPattern(formatterBookPattern);
    DateTimeFormatter formatterPeriod = DateTimeFormatter.ofPattern(formatterPeriodPattern);
    DateTimeFormatter formatterCoworking = DateTimeFormatter.ofPattern(formatterCoworkingPattern);
    HelperNameEnum[] text;

    public Create(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Create.class.getName());
    }


    @Override
    public ResultResponse action() {
        System.out.println(ResponseEnum.ONLY_3_MONTHS);
        ResultResponse resultResponse = null;
        int num = 0;
        String line;
        boolean flag = false;

        while (true) {
            try {
                checkingAuthorization();
                text = new HelperNameEnum[]{Rooms};
                for (HelperNameEnum helper : text) {
                    System.out.println(helper + ": " + helper.getText());
                }
                System.out.println(HelperNameEnum.Number.getText());

                line = commandOrBackOption(); //Проверка, является ли выходом
                if (line.equals("Rooms")) {
                    roomsOption().printData();
                } else {
                    num = Integer.parseInt(line);
                    flag = true;
                }

            } catch (GettingBackToMain e) {
                return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат номера аудитории или команды");
                flag = false;
                continue;
            } catch (IllegalAccessException e) {
                return new ResultResponse(true, ResponseEnum.NO_AUTHORIZATION_YET);
            }

            if (chamberList.containsKey(num)) { //Если есть аудитория
                while (true) {
                    Chamber chamber = chamberList.get(num);
                    text = new HelperNameEnum[]{Table, Book};
                    for (HelperNameEnum helper : text) {
                        System.out.println(helper + ": " + helper.getText());
                    }

                    try {
                        line = commandOrBackOption();
                        resultResponse = switch (line) {
                            case "Book" -> bookOption(chamber);
                            case "Table" -> tableOption(chamber);
                            default -> new ResultResponse(false, ResponseEnum.UNKNOWN_COMMAND);
                        };
                    } catch (GettingBackToMain e) {
                        break;
                    }
                    if (!resultResponse.getResponse().equals(ResponseEnum.OCCUPIED) && line.equals("Book")) {
                        return resultResponse;
                    } else {
                        resultResponse.printData();
                    }
                }


            } else {
                if (flag)
                    System.out.println("Данная аудитория отстутсвует");
            }
        }
    }


    public ResultResponse bookOption(Chamber chamber) throws GettingBackToMain {
        LocalDateTime[] dates;
        if (chamber.getChamberTypeEnum().equals(ChamberTypeEnum.HALL)) {
            dates = checkDateFormat(formatterBook);
        } else {
            dates = checkDateFormat(formatterCoworking);
        }

        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];
        Booking booking = new Booking(userManager.getUser(), startDate, endDate, chamber.getNumber());
        return chamber.add(booking);

    }


    public ResultResponse roomsOption() {
        StringBuilder answer = new StringBuilder();
        if (chamberList.isEmpty()) {
//            answer.append("Нет зарегистрированных аудиторий");
            return new ResultResponse(true, ResponseEnum.NO_ROOMS_DETECTED);
        } else {
            for (Map.Entry<Integer, Chamber> chamber : chamberList.entrySet()) {
                answer.append("Аудитория ").append(chamber.getKey()).append(". ").append(chamber.getValue().getName()).append(" - ").append(chamber.getValue().getDescription()).append("\n");
            }
        }

        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());
    }

    public ResultResponse tableOption(Chamber chamber) throws GettingBackToMain {
        String line;
        while (true) {
            text = new HelperNameEnum[]{All, Period};
            for (HelperNameEnum helper : text) {
                System.out.println(helper + ": " + helper.getText());
            }

            try {
                line = commandOrBackOption();
            } catch (GettingBackToMain e) {
                throw new GettingBackToMain();
            }
            switch (line) {
                case "All":
                    return allOption(chamber);
                case "Period":
                    return periodOption(chamber);
                default:
                    System.out.println(ResponseEnum.UNKNOWN_COMMAND);
            }
        }
    }


    public ResultResponse allOption(Chamber chamber) {
        return showSlot(chamber.getToday(), chamber.getPlusThreeMonthsDay(), chamber.getTimeTable(), chamber);
    }

    public ResultResponse periodOption(Chamber chamber) throws GettingBackToMain {

        System.out.println("Напиши даты, слоты в которых вы хотите просмотреть, в формате: dd.mm.yyyy - dd.mm.yyyy");

        LocalDateTime[] dates = checkDateFormat(formatterPeriod);
        LocalDate startDate = dates[0].toLocalDate();
        LocalDate endDate = dates[1].toLocalDate();
        LocalDate timeTableStart = chamber.getToday();
        LocalDate timeTableEnd = chamber.getPlusThreeMonthsDay();

        HashMap<LocalDate, ArrayList<Booking>> timeTable = chamber.getTimeTable();

        if (!startDate.isBefore(timeTableStart) && !startDate.isAfter(timeTableEnd)) { //Если начальная дата в этих 3 месяцах
            return showSlot(startDate, endDate, timeTable, chamber);

        } else if (!endDate.isBefore(chamber.getToday()) && !endDate.isAfter(chamber.getPlusThreeMonthsDay())) { //Если начальная не в них, то конечная - да
            return showSlot(timeTableStart, endDate, timeTable, chamber);
        } else if (startDate.isBefore(chamber.getToday()) && endDate.isAfter(chamber.getPlusThreeMonthsDay())) { //если обе даты включают эти три месяца
            return allOption(chamber);
        } else { //Если ни одна дата не в отрезке, но отрезок внутри этих дат
            ResultResponse resultResponse = new ResultResponse();
            resultResponse.setStatus(false);
            resultResponse.setResponse(ResponseEnum.NONAVAILABLE_SLOT);
            return resultResponse;
        }

    }

    public LocalDateTime[] checkDateFormat(DateTimeFormatter formatter) throws GettingBackToMain {
        String format;
        if (formatter.equals(formatterBook)) {
            format = formatterBookPattern;
        } else if (formatter.equals(formatterCoworking)) {
            format = formatterCoworkingPattern + ".00";
        } else {
            format = formatterPeriodPattern;
        }
        //TODO:Coworking time limit with x:00 +++
        System.out.printf("Напиши даты желаемой брони в формате: %s - %s \r\n", format, format);
        while (true) {
            try {
                String dates = commandOrBackOption();
                String[] dateParts = dates.split(" - ");
                if (dateParts.length != 2) {
                    throw new IllegalArgumentException();
                }
                LocalDateTime startDate;
                LocalDateTime endDate;
                if (formatter.equals(formatterCoworking)) {
                    startDate = LocalDateTime.parse(dateParts[0], formatterBook);
                    endDate = LocalDateTime.parse(dateParts[1], formatterBook);
                    if (startDate.getMinute() != 0 || endDate.getMinute() != 0) {
                        throw new IllegalArgumentException();
                    }
                }else if (formatter.equals(formatterPeriod)){
                    LocalDate startLocalDate = LocalDate.parse(dateParts[0], formatterPeriod);
                    LocalDate endLocalDate = LocalDate.parse(dateParts[1], formatterPeriod);
                    startDate = startLocalDate.atStartOfDay();
                    endDate = endLocalDate.atStartOfDay();
                }else {
                    startDate = LocalDateTime.parse(dateParts[0], formatter);
                    endDate = LocalDateTime.parse(dateParts[1], formatter);
                }


                if (!endDate.isAfter(startDate) && !endDate.equals(startDate)) {
                    throw new IllegalStateException();
                }
                return new LocalDateTime[]{startDate, endDate};

            } catch (DateTimeParseException | IllegalArgumentException e) {
                System.out.printf("Неверный формат. Пожалуйста, следуйте формату: %s - %s\n", format, format);
            } catch (IllegalStateException e) {
                System.out.println("Неправильно задан диапозон дат. Вторая дата должна быть позде первой \n");
            }

        }
    }

    public ResultResponse showSlot(LocalDate currentDate, LocalDate endDate, HashMap<LocalDate, ArrayList<Booking>> timeTable, Chamber chamber) {
        StringBuilder answer = new StringBuilder();

        if (chamber.getChamberTypeEnum() == ChamberTypeEnum.HALL) {
            if (timeTable.isEmpty()) {
                return new ResultResponse(true, ResponseEnum.NO_AVAILABLE_SLOTS_DETECTED);
            }
            while (!currentDate.isAfter(endDate)) {
                if (timeTable.get(currentDate).isEmpty()) {
                    answer.append(currentDate);
                    answer.append(" Свободно весь день\r\n");
                } else {
                    ArrayList<String> timeSlots = new ArrayList<>();
                    timeSlots.add("00:00");
                    timeSlots.add("23:59");
                    answer.append("Свободные слоты: ");
                    ArrayList<Booking> bookings = timeTable.get(currentDate);
                    for (Booking booking : bookings) {
                        int startMinutes = booking.getStartDate().getMinute();
                        int endMinutes = booking.getEndDate().getMinute();
                        String startTime = booking.getStartDate().getHour() + ":" + startMinutes / 10 + startMinutes % 10;
                        String endTime = booking.getEndDate().getHour() + ":" + endMinutes / 10 + endMinutes % 10;
                        if (!timeSlots.contains(startTime)) {
                            timeSlots.add(startTime);
                        } else {
                            timeSlots.remove(startTime);
                        }

                        if (!timeSlots.contains(endTime)) {
                            timeSlots.add(endTime);
                        } else {
                            timeSlots.remove(endTime);
                        }
                        Collections.sort(timeSlots);
                        for (int i = 0; i < timeSlots.size() - 1; i += 2) {
                            answer.append(timeSlots.get(i)).append(" - ").append(timeSlots.get(i + 1)).append(", ");
                        }
                        answer.append("\n");

                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        } else {
            if (chamber.getCoworkingTimeSlot().isEmpty()) {
                return new ResultResponse(true, ResponseEnum.NO_AVAILABLE_SLOTS_DETECTED);
            }
            while (!currentDate.isAfter(endDate)) {
                HashMap<LocalDateTime, Integer> timeSlots = chamber.getCoworkingTimeSlot().get(currentDate);
                System.out.println(currentDate);
                for (Map.Entry<LocalDateTime, Integer> slot : timeSlots.entrySet())
                    System.out.printf("\t %d:%d%d  -  %d \n", slot.getKey().getHour(), slot.getKey().getMinute() / 10, slot.getKey().getMinute() % 10, slot.getValue());
                currentDate = currentDate.plusDays(1);
            }
        }
        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());

    }
}

