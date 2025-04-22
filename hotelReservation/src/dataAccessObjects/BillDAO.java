package dataAccessObjects;

import database.DataAccess;
import models.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // Method to add a bill to the database
    public void addBill(Bill bill) throws SQLException {
        String query = "INSERT INTO Bill (amountToPay, discountRate, taxRate, isPaid, checkoutTime) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, bill.getAmountToPay());
            preparedStatement.setDouble(2, bill.getDiscountRate());
            preparedStatement.setDouble(3, bill.getTaxRate());
            preparedStatement.setBoolean(4, bill.isPaid());
            preparedStatement.setTimestamp(5, bill.isPaid() ? new Timestamp(System.currentTimeMillis()) : null);
            preparedStatement.executeUpdate();
        }
    }

    // Method to get a bill by ID
    public Bill getBill(int billId) throws SQLException {
        String query = "SELECT * FROM Bill WHERE billId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("billId"),
                        resultSet.getDouble("amountToPay"),
                        resultSet.getDouble("taxRate")
                );
                bill.setDiscountRate(resultSet.getDouble("discountRate"));
                bill.setPaid(resultSet.getBoolean("isPaid"));
                bill.setCheckoutTime(resultSet.getTimestamp("checkoutTime"));
                return bill;
            }
        }
        return null;
    }

    // Method to get all bills
    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bill";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("billId"),
                        resultSet.getDouble("amountToPay"),
                        resultSet.getDouble("taxRate")
                );
                bill.setDiscountRate(resultSet.getDouble("discountRate"));
                bill.setPaid(resultSet.getBoolean("isPaid"));
                bill.setCheckoutTime(resultSet.getTimestamp("checkoutTime"));
                bills.add(bill);
            }
        }
        return bills;
    }

    // Method to get unpaid bills only
    public List<Bill> getUnpaidBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bill WHERE isPaid = false";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("billId"),
                        resultSet.getDouble("amountToPay"),
                        resultSet.getDouble("taxRate")
                );
                bill.setDiscountRate(resultSet.getDouble("discountRate"));
                bill.setPaid(resultSet.getBoolean("isPaid"));
                bill.setCheckoutTime(resultSet.getTimestamp("checkoutTime"));
                bills.add(bill);
            }
        }
        return bills;
    }

    // Method to update a bill's information
    public void updateBill(Bill bill) throws SQLException {
        String query = "UPDATE Bill SET amountToPay = ?, discountRate = ?, taxRate = ?, isPaid = ?, checkoutTime = ? WHERE billId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, bill.getAmountToPay());
            preparedStatement.setDouble(2, bill.getDiscountRate());
            preparedStatement.setDouble(3, bill.getTaxRate());
            preparedStatement.setBoolean(4, bill.isPaid());
            preparedStatement.setTimestamp(5, bill.isPaid() ? new Timestamp(System.currentTimeMillis()) : null);
            preparedStatement.setInt(6, bill.getBillId());
            preparedStatement.executeUpdate();
        }
    }

    // Method to delete a bill by ID
    public void deleteBill(int billId) throws SQLException {
        String query = "DELETE FROM Bill WHERE billId = ?";
        try (Connection connection = DataAccess.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, billId);
            preparedStatement.executeUpdate();
        }
    }
}
