package service;

import dataAccessObjects.BillDAO;
import models.Bill;

import java.sql.SQLException;
import java.util.List;

public class BillService {
    private BillDAO billDAO;

    public BillService() {
        this.billDAO = new BillDAO();
    }

    public void addBill(Bill bill) throws SQLException {
        billDAO.addBill(bill);
    }

    public Bill getBill(int billId) throws SQLException {
        return billDAO.getBill(billId);
    }

    public List<Bill> getAllBills() throws SQLException {
        return billDAO.getAllBills();
    }

    public List<Bill> getUnpaidBills() throws SQLException {
        return billDAO.getUnpaidBills();
    }

    public void updateBill(Bill bill) throws SQLException {
        // Check if the bill is being marked as paid
        if (bill.isPaid()) {
            bill.setCheckoutTime(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        billDAO.updateBill(bill);
    }

    public void deleteBill(int billId) throws SQLException {
        billDAO.deleteBill(billId);
    }
}
