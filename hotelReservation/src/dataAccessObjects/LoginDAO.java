package dataAccessObjects;

import database.DataAccess;
import models.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    // Method to add a login to the database
    public void addLogin(Login login) throws SQLException {
        String query = "INSERT INTO Login (loginId, loginPassword) VALUES (?, ?)";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, login.getLoginId());
            preparedStatement.setString(2, login.getLoginPassword());
            preparedStatement.executeUpdate();
        }
    }

    // Method to get a login by ID
    public Login getLogin(int loginId) throws SQLException {
        String query = "SELECT * FROM Login WHERE loginId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, loginId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Login(
                        resultSet.getInt("loginId"),
                        resultSet.getString("loginPassword")
                );
            }
        }
        return null;
    }

    // Method to validate login credentials
    public boolean validateLogin(int loginId, String inputPassword) throws SQLException {
        String query = "SELECT * FROM Login WHERE loginId = ? AND loginPassword = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, loginId);
            preparedStatement.setString(2, inputPassword);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // Method to update a login's password
    public void updateLoginPassword(int loginId, String newPassword) throws SQLException {
        String query = "UPDATE Login SET loginPassword = ? WHERE loginId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, loginId);
            preparedStatement.executeUpdate();
        }
    }

    // Method to delete a login by ID
    public void deleteLogin(int loginId) throws SQLException {
        String query = "DELETE FROM Login WHERE loginId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, loginId);
            preparedStatement.executeUpdate();
        }
    }
}
