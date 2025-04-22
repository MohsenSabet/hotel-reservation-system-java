package controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Bill;
import models.Reservation;
import service.BillService;
import service.ReservationService;
import service.RoomService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BillServiceController {

    @FXML
    private TextField phoneNumberField;
    @FXML
    private TableView<Reservation> reservationTableView;
    @FXML
    private TableColumn<Reservation, String> nameColumn;
    @FXML
    private TableColumn<Reservation, Long> phoneColumn;
    @FXML
    private TableColumn<Reservation, Integer> bookingIdColumn;
    @FXML
    private ListView<String> billingListView;
    @FXML
    private Spinner<Integer> discountSpinner;
    @FXML
    private Button checkoutButton, cancelButton;

    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ObservableList<String> billingList = FXCollections.observableArrayList();

    private BillService billService = new BillService();
    private ReservationService reservationService = new ReservationService();
    private RoomService roomService = new RoomService();

    @FXML
    private void initialize() {
        // Initialize discount spinner with values from 0 to 25 in steps of 5
        discountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 25, 0, 5));

        // Initialize table columns
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGuest().getFirstName() + " " + data.getValue().getGuest().getLastName()));
        phoneColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getGuest().getPhone()));
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

        // Set items to reservation table view
        reservationTableView.setItems(reservationList);

        // Add listeners to filter reservations by phone number and display billing information
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> filterReservationList(newValue));
        reservationTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayBillingInformation(newValue);
                discountSpinner.getValueFactory().setValue(0); // Reset discount spinner when a different bill is selected
            }
        });

        // Set items to billing list view
        billingListView.setItems(billingList);

        // Load unpaid reservations into the table
        loadUnpaidReservations();
    }

    private void loadUnpaidReservations() {
        try {
            List<Reservation> unpaidReservations = reservationService.getUnpaidReservations(); // Updated to load only unpaid reservations
            reservationList.setAll(unpaidReservations);
        } catch (SQLException e) {
            showErrorAlert("Error loading unpaid reservations: " + e.getMessage());
        }
    }

    private void filterReservationList(String phoneNumber) {
        ObservableList<Reservation> filteredList = FXCollections.observableArrayList();
        for (Reservation reservation : reservationList) {
            if (String.valueOf(reservation.getGuest().getPhone()).contains(phoneNumber)) {
                filteredList.add(reservation);
            }
        }
        reservationTableView.setItems(filteredList);
    }

    private void displayBillingInformation(Reservation reservation) {
        billingList.clear();
        Bill bill = reservation.getBill();
        if (bill != null) {
            billingList.add("Bill ID: " + bill.getBillId());
            billingList.add("Amount to Pay: $" + bill.getAmountToPay());
            billingList.add("Discount: " + bill.getDiscountRate() + "%");
            billingList.add("Tax: " + bill.getTaxRate() + "%");
            billingList.add("Final Amount: $" + bill.calculateFinalAmount());
            billingList.add("Payment Status: " + (bill.isPaid() ? "Paid" : "Unpaid"));
            if (bill.getCheckoutTime() != null) {
                billingList.add("Checkout Time: " + bill.getCheckoutTime().toString());
            }
        }
    }

    @FXML
    private void handleApplyDiscount() {
        int discount = discountSpinner.getValue();
        Reservation selectedReservation = reservationTableView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Bill bill = selectedReservation.getBill();
            double originalAmount = bill.getAmountToPay();
            bill.applyDiscount(discount);
            try {
                billService.updateBill(bill);
                displayBillingInformation(selectedReservation);
                double discountedAmount = bill.calculateFinalAmount();
                showSuccessAlert("Discount applied successfully!\nOriginal Amount: $" + originalAmount + "\nDiscounted Amount: $" + discountedAmount);
            } catch (SQLException e) {
                showErrorAlert("Error applying discount: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCheckout() {
        Reservation selectedReservation = reservationTableView.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Bill bill = selectedReservation.getBill();
            bill.setPaid(true); // Mark the bill as paid
            try {
                // Update the bill in the database
                billService.updateBill(bill);
                
                // Mark the associated rooms as available
                roomService.updateRoomAvailabilityAfterCheckout(selectedReservation.getRooms());
                
                // Reload the list of unpaid reservations
                loadUnpaidReservations();
                
                // Clear the billing information display
                billingList.clear();
                
                // Show a confirmation message
                showSuccessAlert("Checkout successful! The bill has been paid and the room is now available.");
                
            } catch (SQLException e) {
                showErrorAlert("Error during checkout: " + e.getMessage());
            }
        } else {
            showErrorAlert("No reservation selected.");
        }
    }


    @FXML
    private void handleCancel() {
        navigateToAdminPage();
    }

    private void navigateToAdminPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AdminPage.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
