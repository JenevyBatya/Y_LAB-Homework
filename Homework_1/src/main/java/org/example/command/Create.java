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
import org.example.model.TimeSlot;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.example.enumManagment.HelperNameEnum.*;
import static org.example.managment.ConnectionManager.connection;
import static org.example.model.Chamber.*;

/**
 * Класс Create отвечает за выполнение команды создания брони или получения таблицы занятости аудитории.
 * Он наследует {@link BaseCommandAbs} и реализует интерфейс {@link BaseCommand}.
 */
public class Create extends BaseCommandAbs implements BaseCommand {

    // Форматы для различных типов дат
    private final String formatterBookPattern = "dd.MM.yyyy HH.mm";
    private final String formatterPeriodPattern = "dd.MM.yyyy";
    private final String formatterCoworkingPattern = "dd.MM.yyyy HH";
    private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatterBook = DateTimeFormatter.ofPattern(formatterBookPattern);
    private final DateTimeFormatter formatterPeriod = DateTimeFormatter.ofPattern(formatterPeriodPattern);
    private final DateTimeFormatter formatterCoworking = DateTimeFormatter.ofPattern(formatterCoworkingPattern);

    private HelperNameEnum[] text;
    private PreparedStatement ps;
    private String sql;
    private ResultSet resultSet;

    /**
     * Конструктор класса Create.
     *
     * @param chamberManager объект {@link ChamberManager}, используемый для управления аудиториями.
     * @param userManager    объект {@link UserManager}, используемый для управления пользователями.
     */
    public Create(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Create.class.getName());
    }

    /**
     * Выполняет команду создания брони или получения таблицы занятости аудитории.
     *
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    @Override
    public ResultResponse action() {
        System.out.println(ResponseEnum.ONLY_3_MONTHS);
        ResultResponse resultResponse = null;
        int num = 0;
        String line = null;
        boolean flag = false;

        while (true) {
            try {
                checkingAuthorization();
                text = new HelperNameEnum[]{Rooms};
                for (HelperNameEnum helper : text) {
                    System.out.println(helper + ": " + helper.getText());
                }
                System.out.println(HelperNameEnum.Number.getText());

                line = commandOrBackOption(); // Проверка, является ли выходом
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
            } catch (SQLException e) {
                System.out.println(ResponseEnum.SQL_ERROR);
            }
            try {
                ResultResponse response = getChamberManager().chamberExists(num);
                if (response.isStatus()) { // Если аудитория существует
                    while (true) {
                        text = new HelperNameEnum[]{Table, Book};
                        for (HelperNameEnum helper : text) {
                            System.out.println(helper + ": " + helper.getText());
                        }

                        try {
                            line = commandOrBackOption();
                            resultResponse = switch (line) {
                                case "Book" -> bookOption(Integer.parseInt(response.getData()));
                                case "Table" -> tableOption(Integer.parseInt(response.getData()));
                                default -> new ResultResponse(false, ResponseEnum.UNKNOWN_COMMAND);
                            };
                        } catch (GettingBackToMain e) {
                            break;
                        } catch (SQLException e) {
                            System.out.println(ResponseEnum.SQL_ERROR);
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
            } catch (SQLException e) {
                System.out.println(ResponseEnum.SQL_ERROR);
            }
        }
    }

    /**
     * Обрабатывает вариант бронирования аудитории.
     *
     * @param chamber_id ID аудитории.
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    public ResultResponse bookOption(int chamber_id) throws GettingBackToMain, SQLException {
        LocalDateTime[] dates;
        sql = "SELECT type FROM example.chamber WHERE chamber_id = ?";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, chamber_id);
        resultSet = ps.executeQuery();
        if (resultSet.getString("type").equals(ChamberTypeEnum.HALL.toString())) {
            dates = checkDateFormat(formatterBook);
        } else {
            dates = checkDateFormat(formatterCoworking);
        }

        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];
        Booking booking = new Booking(userManager.getUser(), startDate, endDate, chamber_id);
        return add(booking);
    }

    /**
     * Возвращает список всех зарегистрированных аудиторий.
     *
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    public ResultResponse roomsOption() throws SQLException {
        StringBuilder answer = new StringBuilder();
        sql = "SELECT * FROM example.chamber";
        ps = connection.prepareStatement(sql);
        resultSet = ps.executeQuery();
        int count = 0;
        while (resultSet.next()) {
            int number = resultSet.getInt("number");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String type = resultSet.getString("type");
            answer.append("Аудитория ").append(number).append(". ").append(name).append(" - ").append(description).append(". ").append(type);
            count++;
        }
        if (count == 0) {
            return new ResultResponse(true, ResponseEnum.NO_ROOMS_DETECTED);
        }
        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());
    }

    /**
     * Обрабатывает вариант просмотра таблицы занятости аудитории.
     *
     * @param chamberId ID аудитории.
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    public ResultResponse tableOption(int chamberId) throws GettingBackToMain, SQLException {
        String line;
        while (true) {
            text = new HelperNameEnum[]{Period};
            for (HelperNameEnum helper : text) {
                System.out.println(helper + ": " + helper.getText());
            }

            try {
                line = commandOrBackOption();
            } catch (GettingBackToMain e) {
                throw new GettingBackToMain();
            }
            if (line.equals("Period")) {
                return periodOption(chamberId);
            } else {
                System.out.println(ResponseEnum.UNKNOWN_COMMAND);
            }
        }
    }

    /**
     * Обрабатывает вариант выбора периода для просмотра таблицы занятости аудитории.
     *
     * @param chamberId ID аудитории.
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    public ResultResponse periodOption(int chamberId) throws GettingBackToMain, SQLException {
        System.out.println("Напиши даты, слоты в которых вы хотите просмотреть, в формате: dd.MM.yyyy - dd.MM.yyyy");
        StringBuilder answer = new StringBuilder();
        LocalDateTime[] dates = checkDateFormat(formatterPeriod);
        LocalDate startDate = dates[0].toLocalDate();
        LocalDate endDate = dates[1].toLocalDate();

        if (isCoworking(chamberId)) {
            TreeMap<Date, ArrayList<TimeSlot>> timeMap = new TreeMap<>();
            sql = "SELECT * FROM example.time_slot WHERE chamber_id = ? AND ? <= date AND date <= ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, chamberId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            resultSet = ps.executeQuery();
            getTimeMapForCoworking(resultSet, timeMap);

            LocalDate currentDate = dates[0].toLocalDate();
            while (!currentDate.isAfter(endDate)) {
                System.out.println(currentDate);
                try {
                    ArrayList<TimeSlot> timeSlots = timeMap.get(Date.valueOf(currentDate));
                    timeSlots.sort(Comparator.comparingInt(TimeSlot::getHour));
                    for (TimeSlot timeSlot : timeSlots) {
                        answer.append(timeSlot.getHour()).append(":00 Свободно ")
                                .append(timeSlot.getBookedSlots()).append(" из  ")
                                .append(timeSlot.getTotalSlots()).append("\n");
                    }
                } catch (NullPointerException e) {
                    int capacity = findCapacityOfChamber(chamberId);
                    for (int hour = 0; hour < 24; hour++) {
                        answer.append(hour).append(":00 Свободно ")
                                .append(capacity).append(" из  ").append(capacity).append("\n");
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        } else {
            TreeMap<Date, ArrayList<String>> timeMap = new TreeMap<>();
            sql = "SELECT * FROM example.booking WHERE chamber_id = ? AND ? <= end_date AND start_date <= ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, chamberId);
            resultSet = ps.executeQuery();
            getTimeMapForHall(resultSet, timeMap);

            LocalDate currentDate = dates[0].toLocalDate();
            while (!currentDate.isAfter(endDate)) {
                answer.append(currentDate).append("\n");
                try {
                    ArrayList<String> timeSlots = timeMap.get(Date.valueOf(currentDate));
                    timeSlots.sort((o1, o2) -> {
                        LocalTime time1 = LocalTime.parse(o1, formatterTime);
                        LocalTime time2 = LocalTime.parse(o2, formatterTime);
                        return time1.compareTo(time2);
                    });
                    for (int i = 0; i < timeSlots.size() - 1; i += 2) {
                        answer.append(timeSlots.get(i)).append(" - ").append(timeSlots.get(i + 1)).append(", ");
                    }
                    answer.append("\n");
                } catch (NullPointerException e) {
                    answer.append("Свободно весь день\n");
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return new ResultResponse(true, ResponseEnum.TEXT, answer.toString());
    }

    /**
     * Проверяет формат введенных дат и возвращает массив из двух дат.
     *
     * @param formatter форматтер для проверки даты.
     * @return массив из двух дат (начало и конец).
     * @throws GettingBackToMain если команда указывает на возврат к главному меню.
     */
    public LocalDateTime[] checkDateFormat(DateTimeFormatter formatter) throws GettingBackToMain {
        String format;
        if (formatter.equals(formatterPeriod)) {
            format = formatterPeriodPattern;
        } else {
            if (formatter.equals(formatterBook)) {
                format = formatterBookPattern;
            } else {
                format = formatterCoworkingPattern + ".00";
            }
            System.out.printf("Напиши даты желаемой брони в формате: %s - %s \r\n", format, format);
        }

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
                } else if (formatter.equals(formatterPeriod)) {
                    LocalDate startLocalDate = LocalDate.parse(dateParts[0], formatterPeriod);
                    LocalDate endLocalDate = LocalDate.parse(dateParts[1], formatterPeriod);
                    startDate = startLocalDate.atStartOfDay();
                    endDate = endLocalDate.atStartOfDay();
                } else {
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
}
