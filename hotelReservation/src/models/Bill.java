package models;

import java.sql.Timestamp;

public class Bill {
    private int billId;
    private double amountToPay;
    private double discountRate; // Discount rate as a percentage (e.g., 10 for 10%)
    private double taxRate; // Tax rate as a percentage (e.g., 5 for 5%)
    private boolean isPaid; // Payment status
    private Timestamp checkoutTime; // Timestamp for when the bill was paid

    // Constructor
    public Bill(int billId, double amountToPay, double taxRate) {
        this.billId = billId;
        this.amountToPay = amountToPay;
        this.taxRate = taxRate;
        this.discountRate = 0; // Default discount rate is 0%
        this.isPaid = false; // Default payment status is unpaid
        this.checkoutTime = null; // No checkout time initially
    }

    // Additional Constructor
    public Bill(int billId, double amountToPay, double discountRate, double taxRate, boolean isPaid) {
        this.billId = billId;
        this.amountToPay = amountToPay;
        this.discountRate = discountRate;
        this.taxRate = taxRate;
        this.isPaid = isPaid;
        this.checkoutTime = isPaid ? new Timestamp(System.currentTimeMillis()) : null;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
        if (isPaid) {
            this.checkoutTime = new Timestamp(System.currentTimeMillis());
        }
    }

    public Timestamp getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(Timestamp checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    // Method to calculate the final amount
    public double calculateFinalAmount() {
        double discountAmount = amountToPay * (discountRate / 100);
        double taxedAmount = (amountToPay - discountAmount) * (1 + (taxRate / 100));
        return taxedAmount;
    }

    // Method to apply a discount
    public void applyDiscount(double discountRate) {
        if (discountRate < 0 || discountRate > 25) { // max discount rate is 25%
            throw new IllegalArgumentException("Invalid discount rate. It should be between 0 and 25.");
        }
        this.discountRate = discountRate;
    }

    // Method to print the bill details
    public void printBill() {
        System.out.println("Bill ID: " + billId);
        System.out.println("Amount to Pay: " + amountToPay);
        System.out.println("Discount Rate: " + discountRate + "%");
        System.out.println("Tax Rate: " + taxRate + "%");
        System.out.println("Final Amount: " + calculateFinalAmount());
        System.out.println("Payment Status: " + (isPaid ? "Paid" : "Unpaid"));
        if (checkoutTime != null) {
            System.out.println("Checkout Time: " + checkoutTime);
        }
    }

    // Method to validate the bill amount
    public boolean isValidAmount() {
        return amountToPay > 0;
    }
}
