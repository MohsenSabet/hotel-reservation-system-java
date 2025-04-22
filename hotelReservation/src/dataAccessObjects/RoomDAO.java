package dataAccessObjects;

import database.DataAccess;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Method to add a room to the database
    public void addRoom(Room room) throws SQLException {
        String query = "INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, room.getRoomType());
            preparedStatement.setDouble(2, room.getRate());
            preparedStatement.setInt(3, room.getCapacity());
            preparedStatement.setBoolean(4, room.isAvailable());
            preparedStatement.executeUpdate();
        }
    }

    // Method to get a room by ID
    public Room getRoom(int roomId) throws SQLException {
        String query = "SELECT * FROM Room WHERE roomId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String roomType = resultSet.getString("roomType");
                double rate = resultSet.getDouble("rate");
                int capacity = resultSet.getInt("capacity");
                boolean isAvailable = resultSet.getBoolean("isAvailable");

                Room room = createRoomObject(roomId, roomType, rate, capacity);
                room.setAvailable(isAvailable);
                return room;
            }
        }
        return null;
    }

    // Method to get all rooms
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Room";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int roomId = resultSet.getInt("roomId");
                String roomType = resultSet.getString("roomType");
                double rate = resultSet.getDouble("rate");
                int capacity = resultSet.getInt("capacity");
                boolean isAvailable = resultSet.getBoolean("isAvailable");

                Room room = createRoomObject(roomId, roomType, rate, capacity);
                room.setAvailable(isAvailable);
                rooms.add(room);
            }
        }
        return rooms;
    }

    // Method to update a room's information
    public void updateRoom(Room room) throws SQLException {
        String query = "UPDATE Room SET roomType = ?, rate = ?, capacity = ?, isAvailable = ? WHERE roomId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, room.getRoomType());
            preparedStatement.setDouble(2, room.getRate());
            preparedStatement.setInt(3, room.getCapacity());
            preparedStatement.setBoolean(4, room.isAvailable());
            preparedStatement.setInt(5, room.getRoomId());
            preparedStatement.executeUpdate();
        }
    }

    // Method to delete a room by ID
    public void deleteRoom(int roomId) throws SQLException {
        String query = "DELETE FROM Room WHERE roomId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomId);
            preparedStatement.executeUpdate();
        }
    }

    // Method to get all available rooms
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Room WHERE isAvailable = true";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int roomId = resultSet.getInt("roomId");
                String roomType = resultSet.getString("roomType");
                double rate = resultSet.getDouble("rate");
                int capacity = resultSet.getInt("capacity");
                boolean isAvailable = resultSet.getBoolean("isAvailable");

                Room room = createRoomObject(roomId, roomType, rate, capacity);
                room.setAvailable(isAvailable);
                rooms.add(room);
            }
        }
        return rooms;
    }

    // Method to get available room by type
    public Room getAvailableRoomByType(Class<? extends Room> roomType) throws SQLException {
        String roomTypeName = getRoomTypeName(roomType);
        String query = "SELECT * FROM Room WHERE roomType = ? AND isAvailable = true LIMIT 1";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, roomTypeName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Room room = createRoomObject(
                        resultSet.getInt("roomId"),
                        resultSet.getString("roomType"),
                        resultSet.getDouble("rate"),
                        resultSet.getInt("capacity")
                );
                room.setAvailable(resultSet.getBoolean("isAvailable"));
                return room;
            }
        }
        return null;
    }
    
    
    public int countAvailableRoomsByType(Class<? extends Room> roomType) throws SQLException {
        String roomTypeName = getRoomTypeName(roomType);
        String query = "SELECT COUNT(*) AS count FROM Room WHERE roomType = ? AND isAvailable = true";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, roomTypeName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        }
        return 0;
    }

    // Method to update room availability
    public void updateRoomAvailability(int roomId, boolean isAvailable) throws SQLException {
        String query = "UPDATE Room SET isAvailable = ? WHERE roomId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, isAvailable);
            preparedStatement.setInt(2, roomId);
            preparedStatement.executeUpdate();
        }
    }

    private String getRoomTypeName(Class<? extends Room> roomType) {
        if (roomType == SingleRoom.class) {
            return "Single";
        } else if (roomType == DoubleRoom.class) {
            return "Double";
        } else if (roomType == DeluxeRoom.class) {
            return "Deluxe";
        } else if (roomType == PentHouse.class) {
            return "PentHouse";
        } else {
            throw new IllegalArgumentException("Unknown room type: " + roomType.getName());
        }
    }

    // Method to create a Room object based on the room type
    private Room createRoomObject(int roomId, String roomType, double rate, int capacity) {
        switch (roomType) {
            case "Single":
                return new SingleRoom(roomId, rate);
            case "Double":
                return new DoubleRoom(roomId, rate);
            case "Deluxe":
                return new DeluxeRoom(roomId, rate);
            case "PentHouse":
                return new PentHouse(roomId, rate);
            default:
                throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
    }
}
