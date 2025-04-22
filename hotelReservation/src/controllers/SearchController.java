package controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import models.Bill;
import models.Reservation;
import service.ReservationService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class SearchController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Reservation> resultsTableView;
    @FXML
    private TableColumn<Reservation, String> reservationIdColumn;
    @FXML
    private TableColumn<Reservation, String> clientNameColumn;
    @FXML
    private TableColumn<Reservation, String> clientEmailColumn;
    @FXML
    private TableColumn<Reservation, String> clientPhoneColumn;

    private ReservationService reservationService;
    private ObservableList<Reservation> reservationList;
    private List<Reservation> allReservations;

    public SearchController() {
        this.reservationService = new ReservationService();
        this.reservationList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        // Set up table columns
        reservationIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getBookId())));
        clientNameColumn.setCellValueFactory(data -> {
            String title = data.getValue().getGuest().getTitle();
            String fullName = (title != null ? title + " " : "") + data.getValue().getGuest().getFirstName() + " " + data.getValue().getGuest().getLastName();
            return new ReadOnlyStringWrapper(fullName);
        });
        clientEmailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGuest().getEmail()));
        clientPhoneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getGuest().getPhone())));

        resultsTableView.setItems(reservationList);

        // Load all reservations initially
        loadAllReservations();

        // Set up live search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations(newValue));

        // Handle row double-click to show details
        resultsTableView.setOnMouseClicked(this::handleRowClick);
    }

    private void loadAllReservations() {
        try {
            allReservations = reservationService.getAllReservations();
            reservationList.setAll(allReservations);  // Show all reservations initially
        } catch (SQLException e) {
            showErrorAlert("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterReservations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            reservationList.setAll(allReservations);  // Show all reservations if search term is empty
        } else {
            List<Reservation> filteredResults = allReservations.stream()
                    .filter(reservation ->
                            reservation.getGuest().getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                            reservation.getGuest().getLastName().toLowerCase().contains(searchText.toLowerCase()) ||
                            reservation.getGuest().getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            String.valueOf(reservation.getGuest().getPhone()).contains(searchText))
                    .collect(Collectors.toList());

            reservationList.setAll(filteredResults);
        }
    }

    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Check for double-click
            Reservation selectedReservation = resultsTableView.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                showReservationDetails(selectedReservation);
            }
        }
    }

    private void showReservationDetails(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservation Details");
        alert.setHeaderText("Details for Reservation ID: " + reservation.getBookId());

        Bill bill = reservation.getBill();
        StringBuilder content = new StringBuilder();
        
 
        String title = reservation.getGuest().getTitle();
        if (title != null) {
            content.append("Guest: ").append(title).append(" ");
        }
        content.append(reservation.getGuest().getFirstName()).append(" ")
               .append(reservation.getGuest().getLastName()).append("\n");
        
        content.append("Email: ").append(reservation.getGuest().getEmail()).append("\n");
        content.append("Phone: ").append(reservation.getGuest().getPhone()).append("\n");
        content.append("Address: ").append(reservation.getGuest().getAddress()).append("\n");
        content.append("Check-In Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(reservation.getCheckIn())).append("\n");
        content.append("Check-Out Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(reservation.getCheckOut())).append("\n");
        content.append("Number of Adults: ").append(reservation.getNumAdults()).append("\n");
        content.append("Number of Children: ").append(reservation.getNumChildren()).append("\n");
        content.append("Bill Status: ").append(bill.isPaid() ? "Paid" : "Unpaid").append("\n");
        content.append("Amount Before Discount: $").append(bill.getAmountToPay()).append("\n");
        content.append("Discount Rate: ").append(bill.getDiscountRate()).append("%\n");
        content.append("Discount Amount: $").append(bill.getAmountToPay() * (bill.getDiscountRate() / 100)).append("\n");
        content.append("Amount After Discount: $").append(bill.getAmountToPay() - (bill.getAmountToPay() * (bill.getDiscountRate() / 100))).append("\n");
        content.append("Tax Rate: ").append(bill.getTaxRate()).append("%\n");
        content.append("Final Amount (After Tax): $").append(bill.calculateFinalAmount()).append("\n");
        
        if (bill.getCheckoutTime() != null) {
            content.append("Checkout Time: ").append(bill.getCheckoutTime()).append("\n");
        }
        
        content.append("Reservation Source: ").append(reservation.getSource()).append("\n");

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);  
        alert.setContentText(message);
        alert.showAndWait();
    }
}
