package io.ylab.repo;

import io.ylab.managment.ConnectionManager;
import io.ylab.managment.UserManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRepo {

    public static boolean isAdmin() throws SQLException {
        int userId = UserManager.getUserId();
        String sql = "SELECT count(*) FROM example.admin WHERE user_id=?";
        PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.getInt("count") != 0;


    }
}
