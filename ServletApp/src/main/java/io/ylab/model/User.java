package io.ylab.model;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;

    public User(String name, String surname, String email, String phoneNumber, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }
}
