package models;

import java.util.regex.Pattern;

public class Guest {
    private int guestId; // Removed static counter

    private String title;
    private String firstName;
    private String lastName;
    private String address;
    private long phone;
    private String email;

    public Guest(String title, String firstName, String lastName, String address, long phone, String email) {
        // guestId will be set by the database
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; } // Setter for guestId

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public long getPhone() { return phone; }
    public void setPhone(long phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Validation Methods
    public boolean isValidEmail() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public boolean isValidPhone() {
        String phoneString = String.valueOf(phone);
        return phoneString.length() >= 7 && phoneString.length() <= 15;
    }
}
