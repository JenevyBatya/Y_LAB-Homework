package io.ylab.repo;

import io.ylab.managment.ConnectionManager;
import io.ylab.managment.ResultResponse;
import io.ylab.managment.UserManager;
import io.ylab.managment.enums.ResponseEnum;
import io.ylab.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepo {
    private final Connection connection;

    public UserRepo() {
        this.connection = ConnectionManager.getConnection();
    }

    public ResultResponse save(User user) {
        try {
            if (checkingEmail(user.getEmail()).next()) {
                return new ResultResponse(false, ResponseEnum.USER_EXISTS);
            }
            String sql = "INSERT INTO example.user (name, surname,email,phone_number, password) VALUES (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getPassword());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                connection.commit();
                UserManager.authorize(userId);
                return new ResultResponse(true, ResponseEnum.USER_ADD_SUCCESS, userId);
            } else {
                return new ResultResponse(false, ResponseEnum.SQL_ERROR);
            }
        } catch (SQLException e) {
            return new ResultResponse(false, ResponseEnum.SQL_ERROR);
        }
    }

    public ResultResponse authorize(String email, String password) {
        try {
            ResultSet resultSet = checkingEmail(email);
            if (resultSet.next()) {
                String possiblePassword = resultSet.getString("password");
                if (possiblePassword.equals(password)) {
                    UserManager.authorize(resultSet.getInt("id"));
                    return new ResultResponse(true, ResponseEnum.USER_AUTH_SUCCESS, UserManager.getToken());
                }
                return new ResultResponse(false, ResponseEnum.USER_AUTH_WRONG_DATA);
            }
        } catch (SQLException e) {
            System.out.println(ResponseEnum.SQL_ERROR);
        }
        return new ResultResponse(false, ResponseEnum.USER_AUTH_WRONG_DATA);
    }

    private ResultSet checkingEmail(String email) throws SQLException {
        String sql = "SELECT * FROM example.user WHERE email=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        return ps.executeQuery();
    }
}
