package org.example.managment;

import org.example.enumManagment.ResponseEnum;
import org.example.model.Role;
import org.example.model.User;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.command.BaseCommandAbs.commandOrBackOption;

public class UserManager {
    private boolean isAuthorized;
    private User user;
    private final HashMap<String, User> userList = new HashMap<>();
    static String wrong_answer = "Неверный формат ответа";

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

    public ResultResponse registering(String name, String surname, String email, String phoneNumber, String password) {
        if (!userList.containsKey(email)){
            setUser(new User(name, surname, email, phoneNumber, password));
            setAuthorized(true);
            userList.put(email, user);
            return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH);
        }else {
            return new ResultResponse(false, ResponseEnum.ALREADY_REGISTRATED);
        }

    }

    public ResultResponse authorizing(String email, String password) {
        if (userList.containsKey(email)) {
            User inUser = userList.get(email);
            if (inUser.getPassword().equals(password)) {
                setUser(inUser);
                setAuthorized(true);
                return new ResultResponse(true, ResponseEnum.SUCCESS_AUTH);
            }
        }
        return new ResultResponse(false, ResponseEnum.WRONG_DATA);

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
