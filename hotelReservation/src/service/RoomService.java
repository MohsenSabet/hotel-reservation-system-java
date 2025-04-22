package service;

import dataAccessObjects.RoomDAO;
import models.Room;

import java.sql.SQLException;
import java.util.List;

public class RoomService {
    private RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public void addRoom(Room room) throws SQLException {
        roomDAO.addRoom(room);
    }

    public Room getRoom(int roomId) throws SQLException {
        return roomDAO.getRoom(roomId);
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public void updateRoom(Room room) throws SQLException {
        roomDAO.updateRoom(room);
    }

    public void deleteRoom(int roomId) throws SQLException {
        roomDAO.deleteRoom(roomId);
    }

    public List<Room> getAvailableRooms() throws SQLException {
        return roomDAO.getAvailableRooms();
    }

    public Room getAvailableRoomByType(Class<? extends Room> roomType) throws SQLException {
        return roomDAO.getAvailableRoomByType(roomType);
    }

    public int countAvailableRoomsByType(Class<? extends Room> roomType) throws SQLException {
        return roomDAO.countAvailableRoomsByType(roomType);
    }

    public void updateRoomAvailability(int roomId, boolean isAvailable) throws SQLException {
        roomDAO.updateRoomAvailability(roomId, isAvailable);
    }

    // New method to handle room availability after checkout
    public void updateRoomAvailabilityAfterCheckout(List<Room> rooms) throws SQLException {
        for (Room room : rooms) {
            room.setAvailable(true);
            updateRoomAvailability(room.getRoomId(), true);
        }
    }
}
