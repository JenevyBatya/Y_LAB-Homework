package org.example.managment;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.model.Chamber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static org.example.command.BaseCommandAbs.commandOrBackOption;
import static org.example.managment.ConnectionManager.connection;

public class ChamberManager {
//    private final HashMap<Integer, Chamber> chamberList = new HashMap<>();

//    public void registerChambers() {
//        chamberList.put(1, new Chamber(1, "Лекционный зал", "Основной зал для лекций на 500 человек", new HashMap<>(), ChamberTypeEnum.HALL, 500));
//        chamberList.put(2, new Chamber(2, "Поминальный зал", "Хороший зал, душевный, на 50 человек", new HashMap<>(), ChamberTypeEnum.HALL, 50));
//        chamberList.put(3, new Chamber(3, "Главный коворкинг", "Основной коворкинг на 200 человек", new HashMap<>(), ChamberTypeEnum.COWORKING, 200));
//        chamberList.put(4, new Chamber(4, "Библиотека", "Читальный зал на 70 человек", new HashMap<>(), ChamberTypeEnum.COWORKING, 70));
//    }

//    public HashMap<Integer, Chamber> getChamberList() {
//        return chamberList;
//    }

    public ResultResponse gettingNumber() throws GettingBackToMain {
        int num;
        while (true) {
            try {
                System.out.println("Введите номер новой аудитории:");
                num = Integer.parseInt(commandOrBackOption());

                if (!chamberExists(num).isStatus()) {
                    return new ResultResponse(true, ResponseEnum.SUCCESS, String.valueOf(num));
                } else {
                    System.out.println("Данная аудитория уже доступна для резервации");
                }
            } catch (NumberFormatException e) {
                System.out.println(ResponseEnum.WRONG_FORMAT);
            } catch (SQLException e) {
                System.out.println("Ошибка при доступе к базе данных: " + e.getMessage());
            }
        }
    }

    public ResultResponse chamberExists(int num) throws SQLException {
        int count = 0;
        String sql = "SELECT number FROM example.chamber WHERE number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, num);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    count++;
                }
                return new ResultResponse(count > 0, ResponseEnum.SUCCESS);
            }
        }
    }

    public ResultResponse gettingNumberToDelete() throws GettingBackToMain {
        int num;
        while (true) {
            try {
                System.out.println("Введите номер аудитории, которую хотите удалить:");
                num = Integer.parseInt(commandOrBackOption());
                if (chamberExists(num).isStatus()) {
                    return new ResultResponse(true, ResponseEnum.SUCCESS, String.valueOf(num));
                } else {
                    System.out.println("Данной аудитории не существует");
                }

            } catch (NumberFormatException e) {
                System.out.println(ResponseEnum.WRONG_FORMAT);
            } catch (SQLException e) {
                System.out.println("Ошибка при доступе к базе данных: " + e.getMessage());
            }
        }
    }

    public ResultResponse gettingName() throws GettingBackToMain {
        System.out.println("Введите название аудитории:");
        return new ResultResponse(true, ResponseEnum.SUCCESS, commandOrBackOption());
    }

    public ResultResponse gettingDescription() throws GettingBackToMain {
        System.out.println("Введите описание аудитории:");
        return new ResultResponse(true, ResponseEnum.SUCCESS, commandOrBackOption());
    }

    public ResultResponse gettingType() throws GettingBackToMain {
        String line;
        while (true) {
            System.out.println("Введите тип новой аудитории: Коворкинг, Зал");
            line = commandOrBackOption();
            switch (line) {
                case "Коворкинг":
                    return new ResultResponse(true, ResponseEnum.SUCCESS, ChamberTypeEnum.COWORKING);
                case "Зал":
                    return new ResultResponse(true, ResponseEnum.SUCCESS, ChamberTypeEnum.HALL);
                default:
                    System.out.println(ResponseEnum.WRONG_DATA);
            }
        }
    }

    public ResultResponse gettingAudienceCapacity() throws GettingBackToMain {
        int capacity;
        while (true) {
            try {
                System.out.println("Введите вместимость коворкинга:");
                capacity = Integer.parseInt(commandOrBackOption());

                return new ResultResponse(true, ResponseEnum.SUCCESS, String.valueOf(capacity));


            } catch (NumberFormatException e) {
                System.out.println(ResponseEnum.WRONG_FORMAT);
            }
        }
    }
}
