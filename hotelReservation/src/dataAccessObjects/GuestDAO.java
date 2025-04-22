package dataAccessObjects;

import database.DataAccess;
import models.Guest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public void addGuest(Guest guest, Connection connection) throws SQLException {
        String query = "INSERT INTO Guest (title, firstName, lastName, address, phone, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, guest.getTitle());
            preparedStatement.setString(2, guest.getFirstName());
            preparedStatement.setString(3, guest.getLastName());
            preparedStatement.setString(4, guest.getAddress());
            preparedStatement.setLong(5, guest.getPhone());
            preparedStatement.setString(6, guest.getEmail());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                guest.setGuestId(generatedKeys.getInt(1)); // Set the generated guestId
            }
        }
    }

    // Method to get a guest by ID
    public Guest getGuest(int guestId) throws SQLException {
        String query = "SELECT * FROM Guest WHERE guestId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, guestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Guest guest = new Guest(
                        resultSet.getString("title"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("address"),
                        resultSet.getLong("phone"),
                        resultSet.getString("email")
                );
                guest.setGuestId(resultSet.getInt("guestId")); // Ensure the ID is set correctly
                return guest;
            }
        }
        return null;
    }

    // Method to get all guests
    public List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM Guest";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Guest guest = new Guest(
                        resultSet.getString("title"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("address"),
                        resultSet.getLong("phone"),
                        resultSet.getString("email")
                );
                guest.setGuestId(resultSet.getInt("guestId")); // Ensure the ID is set correctly
                guests.add(guest);
            }
        }
        return guests;
    }

    // Method to update a guest's information
    public void updateGuest(Guest guest) throws SQLException {
        String query = "UPDATE Guest SET title = ?, firstName = ?, lastName = ?, address = ?, phone = ?, email = ? WHERE guestId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, guest.getTitle());
            preparedStatement.setString(2, guest.getFirstName());
            preparedStatement.setString(3, guest.getLastName());
            preparedStatement.setString(4, guest.getAddress());
            preparedStatement.setLong(5, guest.getPhone());
            preparedStatement.setString(6, guest.getEmail());
            preparedStatement.setInt(7, guest.getGuestId());
            preparedStatement.executeUpdate();
        }
    }

    // Method to delete a guest by ID
    public void deleteGuest(int guestId) throws SQLException {
        String query = "DELETE FROM Guest WHERE guestId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, guestId);
            preparedStatement.executeUpdate();
        }
    }

    // Method to get a guest by phone number
    public Guest getGuestByPhone(long phone) throws SQLException {
        String query = "SELECT * FROM Guest WHERE phone = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Guest guest = new Guest(
                        resultSet.getString("title"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("address"),
                        resultSet.getLong("phone"),
                        resultSet.getString("email")
                );
                guest.setGuestId(resultSet.getInt("guestId")); // Ensure the ID is set correctly
                return guest;
            }
        }
        return null;
    }
}
