package io.ylab.repo;

import io.ylab.managment.ConnectionManager;
import io.ylab.managment.ResultResponse;
import io.ylab.managment.enums.ChamberTypeEnum;
import io.ylab.managment.enums.ResponseEnum;
import io.ylab.model.Chamber;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static io.ylab.repo.AdminRepo.isAdmin;

public class ChamberRepo {
    private final Connection connection;

    public ChamberRepo() {
        this.connection = ConnectionManager.getConnection();
    }

    public ResultResponse save(Chamber chamber) {
        try {
            String sql = "INSERT INTO example.chamber (number, name, description, capacity, type) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, chamber.getNumber());
            preparedStatement.setString(2, chamber.getName());
            preparedStatement.setString(3, chamber.getDescription());
            preparedStatement.setInt(4, chamber.getCapacity());
            preparedStatement.setString(5, chamber.getType().toString());
            preparedStatement.executeUpdate();
            connection.commit();
            return new ResultResponse(true, ResponseEnum.BOOKING_SUCCESS_ADD);
        } catch (SQLException e) {
            return new ResultResponse(false, ResponseEnum.SQL_ERROR);
        }

    }

    public ResultResponse delete(int chamberNumber) {
        try {
            String sql = "DELETE FROM example.chamber WHERE chamber_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, findChamberId(chamberNumber));
            int result = ps.executeUpdate();
            if (result == 1) {
                connection.commit();
                return new ResultResponse(true, ResponseEnum.CHAMBER_DELETE_SUCCESS);
            }
        } catch (SQLException e) {
            return new ResultResponse(false, ResponseEnum.SQL_ERROR);
        }
        return new ResultResponse(false, ResponseEnum.CHAMBER_DELETE_FAILURE);
    }

    public boolean isCoworking(int chamberNumber) throws SQLException {
        String sql = "SELECT type FROM example.chamber where number=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, chamberNumber);
        ResultSet resultSet = ps.getResultSet();
        return Objects.equals(resultSet.getString("type"), ChamberTypeEnum.HALL.toString());
    }

    public Integer findChamberId(int chamberNumber) throws SQLException {
        String sql = "SELECT id FROM example.chamber WHERE number=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, chamberNumber);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("id");
        }
        return null;

    }

    public int findCapacityOfChamber(int chamberNumber) throws SQLException {
        int capacity;
        String sql = "SELECT capacity FROM example.chamber WHERE id=?";
        PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(sql);
        ps.setInt(1, findChamberId(chamberNumber));
        capacity = ps.executeQuery().getInt("capacity");
        return capacity;
    }
}
