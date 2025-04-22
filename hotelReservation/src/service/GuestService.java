package service;

import dataAccessObjects.GuestDAO;
import models.Guest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GuestService {
    private GuestDAO guestDAO;

    public GuestService() {
        this.guestDAO = new GuestDAO();
    }

    public void addGuest(Guest guest, Connection connection) throws SQLException {
        guestDAO.addGuest(guest, connection);
    }

    public Guest getGuest(int guestId) throws SQLException {
        return guestDAO.getGuest(guestId);
    }

    public List<Guest> getAllGuests() throws SQLException {
        return guestDAO.getAllGuests();
    }

    public void updateGuest(Guest guest) throws SQLException {
        guestDAO.updateGuest(guest);
    }

    public void deleteGuest(int guestId) throws SQLException {
        guestDAO.deleteGuest(guestId);
    }

    public Guest getGuestByPhone(long phone) throws SQLException {
        return guestDAO.getGuestByPhone(phone);
    }
}
