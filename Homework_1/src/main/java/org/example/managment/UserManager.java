package org.example.managment;

import org.example.enumManagment.ResponseEnum;
import org.example.model.Role;
import org.example.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.command.BaseCommandAbs.commandOrBackOption;
import static org.example.managment.ConnectionManager.connection;

public class UserManager {
    private boolean isAuthorized;
    private User user;
    private final HashMap<String, User> userList = new HashMap<>();
    static String wrong_answer = "Неверный формат ответа";
    private String sql;
    private PreparedStatement ps;

    public UserManager() {
        isAuthorized = false;
        User admin = new User("Mao", "Mao", "a", "89999999999", "a");
        admin.setRole(Role.ADMIN);
        userList.put("a", admin);
        User commoner = new User("Mao", "Mao", "b", "89999999999", "b");
        userList.put("b", commoner);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        this.isAuthorized = authorized;
        System.out.println();
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ResultResponse registering(String name, String surname, String email, String phoneNumber, String password){
        try {
            if (checkingEmail(email) == null) {
                setUser(new User(name, surname, email, phoneNumber, password));
                setAuthorized(true);
                sql = "INSERT INTO example.user (name, surname,email,phone_number, password) VALUES (?,?,?,?,?)";
                ps = connection.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, surname);
                ps.setString(3, email);
                ps.setString(4, phoneNumber);
                ps.setString(5, password);
                ps.execute();
                connection.commit();
                return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH);
            } else {
                return new ResultResponse(false, ResponseEnum.ALREADY_REGISTRATED);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ResultResponse authorizing(String email, String password) {
        try {
            ResultSet resultSet = checkingEmail(email);
            if (resultSet != null) {
                String possiblePassword = resultSet.getString("password");
                if (possiblePassword.equals(password)) {
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    String phoneNumber = resultSet.getString("phone_number");
                    User user = new User(name, surname, email, phoneNumber, password);
                    setUser(user);
                    setAuthorized(true);
                    return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH);
                }
                return new ResultResponse(false, ResponseEnum.WRONG_DATA);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ResultResponse(false, ResponseEnum.WRONG_DATA);

    }

    private ResultSet checkingEmail(String email) throws SQLException {
        sql = "SELECT * FROM example.user WHERE email==?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet resultSet = ps.executeQuery();
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        return count == 0 ? null : resultSet;
    }

    public static ResultResponse gettingPassword() throws GettingBackToMain {
        System.out.println("Пароль:");
        String line = commandOrBackOption();
        return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH, line);
    }

    public static ResultResponse gettingEmail() throws GettingBackToMain {
        System.out.println("Электронная почта:");
        String line = commandOrBackOption();
        return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH, line);
    }

    public static ResultResponse gettingNameAndSurname() throws GettingBackToMain {
        System.out.println("Ваши имя и фамилия через запятую:");
        while (true) {
            try {
                String data = commandOrBackOption();
                String[] datas = data.split(" ");
                if (datas.length != 2) {
                    throw new IllegalArgumentException();
                } else {
                    return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH, data);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(wrong_answer);
            }
        }
    }


    public static ResultResponse gettingPhoneNumber() throws GettingBackToMain {
        System.out.println("Российский номер телефона:");
        String regex = "(\\+7|8)\\s?\\(?\\d{3}\\)?\\s?\\d{3}[-.\\s]?\\d{2}[-.\\s]?\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            try {
                String data = commandOrBackOption();
                Matcher matcher = pattern.matcher(data);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException();
                } else {
                    return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH, data);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(wrong_answer);
            }
        }
    }

}
