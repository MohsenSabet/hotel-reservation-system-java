package models;

public abstract class Room {
    protected int roomId;
    protected String roomType;
    protected double rate;
    protected int capacity;
    protected boolean isAvailable;

    // Constructor
    public Room(int roomId, String roomType, double rate, int capacity) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.rate = rate;
        this.capacity = capacity;
        this.isAvailable = true; // Assuming the room is available when created
    }

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    // Method to check availability
    public boolean checkAvailability() {
        return isAvailable;
    }

    // Method to book the room
    public void bookRoom() {
        if (isAvailable) {
            isAvailable = false;
            System.out.println("Room " + roomId + " has been booked.");
        } else {
            System.out.println("Room " + roomId + " is not available.");
        }
    }

    // Method to mark room as available
    public void makeAvailable() {
        isAvailable = true;
        System.out.println("Room " + roomId + " is now available.");
    }

    // Method to calculate the cost
    public double calculateCost(int days) {
        return rate * days;
    }

    // Method to display room details
    public void displayRoomDetails() {
        System.out.println("Room ID: " + roomId);
        System.out.println("Room Type: " + roomType);
        System.out.println("Rate: " + rate);
        System.out.println("Capacity: " + capacity);
        System.out.println("Availability: " + (isAvailable ? "Available" : "Not Available"));
    }
}
