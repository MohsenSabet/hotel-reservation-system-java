package service;

import dataAccessObjects.ReservationDAO;
import dataAccessObjects.RoomDAO;
import database.DataAccess;
import models.Reservation;
import models.Room;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
    }

    public void addReservation(Reservation reservation) throws SQLException {
        try (Connection connection = DataAccess.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Add reservation
            reservationDAO.addReservation(reservation);

            // Mark rooms as occupied
            for (Room room : reservation.getRooms()) {
                roomDAO.updateRoomAvailability(room.getRoomId(), false);
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw e;
        }
    }

    public Reservation getReservation(int bookId) throws SQLException {
        return reservationDAO.getReservation(bookId);
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.getAllReservations();
    }
    
    public List<Reservation> getUnpaidReservations() throws SQLException {
        return reservationDAO.getUnpaidReservations();
    }
    
    public List<Reservation> getReservationsForRoom(int roomId) throws SQLException {
        return reservationDAO.getReservationsByRoom(roomId);
    }
    

    public void updateReservation(Reservation reservation) throws SQLException {
        try (Connection connection = DataAccess.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Update reservation
            reservationDAO.updateReservation(reservation);

            // Update room availability
            for (Room room : reservation.getRooms()) {
                roomDAO.updateRoomAvailability(room.getRoomId(), false);
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteReservation(int bookId) throws SQLException {
        try (Connection connection = DataAccess.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Get reservation to retrieve rooms
            Reservation reservation = reservationDAO.getReservation(bookId);

            // Delete reservation
            reservationDAO.deleteReservation(bookId);

            // Mark rooms as available
            for (Room room : reservation.getRooms()) {
                roomDAO.updateRoomAvailability(room.getRoomId(), true);
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Reservation> getReservationsByGuest(int guestId) throws SQLException {
        return reservationDAO.getReservationsByGuest(guestId);
    }
    
    public List<Reservation> searchReservations(String searchTerm) throws SQLException {
        return reservationDAO.searchReservations(searchTerm);
    }
    
}
