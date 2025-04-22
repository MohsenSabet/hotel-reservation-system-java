package dataAccessObjects;

import database.DataAccess;
import models.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservationDAO {

    // Method to add a reservation to the database
    public void addReservation(Reservation reservation) throws SQLException {
        Connection connection = DataAccess.getConnection();
        try {
            connection.setAutoCommit(false); // Start transaction

            // First, ensure the guest exists
            Guest guest = reservation.getGuest();
            if (guest.getGuestId() == 0 || !guestExists(guest.getGuestId(), connection)) {
                addGuest(guest, connection);
            }

            // Then, add the bill
            String billQuery = "INSERT INTO Bill (amountToPay, discountRate, taxRate, isPaid) VALUES (?, ?, ?, ?)";
            try (PreparedStatement billStatement = connection.prepareStatement(billQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                Bill bill = reservation.getBill();
                billStatement.setBigDecimal(1, BigDecimal.valueOf(bill.getAmountToPay()));
                billStatement.setBigDecimal(2, BigDecimal.valueOf(bill.getDiscountRate()));
                billStatement.setBigDecimal(3, BigDecimal.valueOf(bill.getTaxRate()));
                billStatement.setBoolean(4, bill.isPaid());
                billStatement.executeUpdate();
                ResultSet generatedKeys = billStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bill.setBillId(generatedKeys.getInt(1));
                }
            }

            // Then, add the reservation
            String query = "INSERT INTO Reservation (bookDate, checkIn, checkOut, guestId, billId, source, numAdults, numChildren) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setDate(1, new Date(reservation.getBookDate().getTime()));
                preparedStatement.setDate(2, new Date(reservation.getCheckIn().getTime()));
                preparedStatement.setDate(3, new Date(reservation.getCheckOut().getTime()));
                preparedStatement.setInt(4, reservation.getGuest().getGuestId());
                preparedStatement.setInt(5, reservation.getBill().getBillId());
                preparedStatement.setString(6, reservation.getSource().toString());
                preparedStatement.setInt(7, reservation.getNumAdults());
                preparedStatement.setInt(8, reservation.getNumChildren());
                preparedStatement.executeUpdate();

                // Retrieve the generated reservation ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int reservationId = generatedKeys.getInt(1);
                    reservation.setBookId(reservationId);

                    // Insert the rooms associated with the reservation
                    addReservationRooms(reservationId, reservation.getRooms(), connection);
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            throw e;
        } finally {
            connection.setAutoCommit(true); // Reset autocommit
            connection.close(); // Close connection
        }
    }

    // Method to check if a guest exists
    private boolean guestExists(int guestId, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Guest WHERE guestId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, guestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    // Method to add a guest to the database
    private void addGuest(Guest guest, Connection connection) throws SQLException {
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
                guest.setGuestId(generatedKeys.getInt(1));
            }
        }
    }

    // Method to add the rooms associated with a reservation
    private void addReservationRooms(int reservationId, List<Room> rooms, Connection connection) throws SQLException {
        String query = "INSERT INTO ReservationRoom (reservationId, roomId) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            Set<Integer> roomIds = new HashSet<>();
            for (Room room : rooms) {
                if (roomIds.add(room.getRoomId())) { // Ensure unique roomIds
                    preparedStatement.setInt(1, reservationId);
                    preparedStatement.setInt(2, room.getRoomId());
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
        }
    }

    // Method to get a reservation by ID
    public Reservation getReservation(int bookId) throws SQLException {
        String query = "SELECT * FROM Reservation WHERE bookId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int guestId = resultSet.getInt("guestId");
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(bookId, connection);

                return new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),  // Retrieve numAdults
                        resultSet.getInt("numChildren") // Retrieve numChildren
                );
            }
        }
        return null;
    }

    // Method to get the rooms associated with a reservation
    private List<Room> getRoomsForReservation(int reservationId, Connection connection) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT roomId FROM ReservationRoom WHERE reservationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reservationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int roomId = resultSet.getInt("roomId");
                Room room = new RoomDAO().getRoom(roomId);
                if (room != null) {
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    // Method to get all reservations with guests and rooms
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservation";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int guestId = resultSet.getInt("guestId");
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(resultSet.getInt("bookId"), connection);

                reservations.add(new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),  // Retrieve numAdults
                        resultSet.getInt("numChildren") // Retrieve numChildren
                ));
            }
        }
        return reservations;
    }

    // Method to get all unpaid reservations
    public List<Reservation> getUnpaidReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservation r JOIN Bill b ON r.billId = b.billId WHERE b.isPaid = false";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int guestId = resultSet.getInt("guestId");
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(resultSet.getInt("bookId"), connection);

                reservations.add(new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),  // Retrieve numAdults
                        resultSet.getInt("numChildren") // Retrieve numChildren
                ));
            }
        }
        return reservations;
    }

    // Method to update a reservation's information
    public void updateReservation(Reservation reservation) throws SQLException {
        String query = "UPDATE Reservation SET bookDate = ?, checkIn = ?, checkOut = ?, guestId = ?, billId = ?, source = ?, numAdults = ?, numChildren = ? WHERE bookId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, new Date(reservation.getBookDate().getTime()));
            preparedStatement.setDate(2, new Date(reservation.getCheckIn().getTime()));
            preparedStatement.setDate(3, new Date(reservation.getCheckOut().getTime()));
            preparedStatement.setInt(4, reservation.getGuest().getGuestId());
            preparedStatement.setInt(5, reservation.getBill().getBillId());
            preparedStatement.setString(6, reservation.getSource().toString());
            preparedStatement.setInt(7, reservation.getNumAdults());  // Update numAdults
            preparedStatement.setInt(8, reservation.getNumChildren()); // Update numChildren
            preparedStatement.setInt(9, reservation.getBookId());
            preparedStatement.executeUpdate();

            // Update the rooms associated with the reservation
            updateReservationRooms(reservation.getBookId(), reservation.getRooms(), connection);
        }
    }

    // Method to update the rooms associated with a reservation
    private void updateReservationRooms(int reservationId, List<Room> rooms, Connection connection) throws SQLException {
        // Delete existing rooms
        String deleteQuery = "DELETE FROM ReservationRoom WHERE reservationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.executeUpdate();
        }

        // Add new rooms
        addReservationRooms(reservationId, rooms, connection);
    }

    // Method to delete a reservation by ID
    public void deleteReservation(int bookId) throws SQLException {
        String query = "DELETE FROM Reservation WHERE bookId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.executeUpdate();

            // Delete associated rooms
            deleteReservationRooms(bookId, connection);
        }
    }

    // Method to delete the rooms associated with a reservation
    private void deleteReservationRooms(int reservationId, Connection connection) throws SQLException {
        String query = "DELETE FROM ReservationRoom WHERE reservationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.executeUpdate();
        }
    }

    // Method to get reservations by guest ID
    public List<Reservation> getReservationsByGuest(int guestId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservation WHERE guestId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, guestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(resultSet.getInt("bookId"), connection);

                reservations.add(new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),  // Retrieve numAdults
                        resultSet.getInt("numChildren") // Retrieve numChildren
                ));
            }
        }
        return reservations;
    }
    
    public List<Reservation> getReservationsByRoom(int roomId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.* FROM Reservation r " +
                       "JOIN ReservationRoom rr ON r.bookId = rr.reservationId " +
                       "WHERE rr.roomId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int guestId = resultSet.getInt("guestId");
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(resultSet.getInt("bookId"), connection);

                reservations.add(new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),  // Retrieve numAdults
                        resultSet.getInt("numChildren") // Retrieve numChildren
                ));
            }
        }
        return reservations;
    }
    
    public List<Reservation> searchReservations(String searchTerm) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM Reservation r " +
                       "JOIN Guest g ON r.guestId = g.guestId " +
                       "WHERE g.phone LIKE ? OR g.email LIKE ? OR g.firstName LIKE ? OR g.lastName LIKE ?";
        
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);
            preparedStatement.setString(3, searchPattern);
            preparedStatement.setString(4, searchPattern);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int guestId = resultSet.getInt("guestId");
                int billId = resultSet.getInt("billId");

                Guest guest = new GuestDAO().getGuest(guestId);
                Bill bill = new BillDAO().getBill(billId);
                List<Room> rooms = getRoomsForReservation(resultSet.getInt("bookId"), connection);

                reservations.add(new Reservation(
                        resultSet.getInt("bookId"),
                        resultSet.getDate("bookDate"),
                        resultSet.getDate("checkIn"),
                        resultSet.getDate("checkOut"),
                        guest,
                        rooms,
                        bill,
                        ReservationSource.valueOf(resultSet.getString("source")),
                        resultSet.getInt("numAdults"),
                        resultSet.getInt("numChildren")
                ));
            }
        }
        return reservations;
    }

    
    
}
