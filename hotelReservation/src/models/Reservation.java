package models;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Reservation {
    private int bookId;
    private Date bookDate;
    private Date checkIn;
    private Date checkOut;
    private Guest guest;
    private List<Room> rooms;
    private Bill bill; // Associated bill for the reservation
    private ReservationSource source; // Source of the reservation
    private int numAdults; // Number of adults in the reservation
    private int numChildren; // Number of children in the reservation

    public Reservation(int bookId, Date bookDate, Date checkIn, Date checkOut, Guest guest, List<Room> rooms, Bill bill, ReservationSource source, int numAdults, int numChildren) {
        this.bookId = bookId;
        this.bookDate = bookDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guest = guest;
        this.rooms = rooms;
        this.bill = bill;
        this.source = source;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public Date getBookDate() { return bookDate; }
    public void setBookDate(Date bookDate) { this.bookDate = bookDate; }

    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }

    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }

    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
    
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    
    public ReservationSource getSource() { return source; }
    public void setSource(ReservationSource source) { this.source = source; }

    public int getNumAdults() { return numAdults; }
    public void setNumAdults(int numAdults) { this.numAdults = numAdults; }

    public int getNumChildren() { return numChildren; }
    public void setNumChildren(int numChildren) { this.numChildren = numChildren; }

    // Method to calculate the duration of stay
    public long getDuration() {
        long diffInMillies = Math.abs(checkOut.getTime() - checkIn.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    // Method to calculate the total cost of the reservation
    public double calculateTotalCost() {
        return rooms.stream().mapToDouble(room -> room.getRate() * getDuration()).sum();
    }

    // Method to check if the reservation dates are valid
    public boolean isValidDates() {
        return checkIn.before(checkOut);
    }

    // Method to print reservation details
    public void printReservationDetails() {
        System.out.println("Reservation ID: " + bookId);
        System.out.println("Booking Date: " + bookDate);
        System.out.println("Check-In Date: " + checkIn);
        System.out.println("Check-Out Date: " + checkOut);
        System.out.println("Guest: " + guest.getFirstName() + " " + guest.getLastName());
        System.out.println("Number of Adults: " + numAdults);
        System.out.println("Number of Children: " + numChildren);
        rooms.forEach(room -> {
            System.out.println("Room Type: " + room.getRoomType());
            System.out.println("Room Rate: " + room.getRate());
        });
        System.out.println("Duration: " + getDuration() + " days");
        System.out.println("Total Cost: " + calculateTotalCost());
        System.out.println("Bill Status: " + (bill.isPaid() ? "Paid" : "Unpaid"));
        System.out.println("Reservation Source: " + source);
    }
}
