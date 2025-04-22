package controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Reservation;
import service.ReservationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CurrentBookingController {

    @FXML
    private TextField phoneNumberField;
    @FXML
    private Label numCurrentBookingsLabel;
    @FXML
    private TableView<Reservation> currentBookingsTableView;
    @FXML
    private TableColumn<Reservation, String> bookingIdColumn;
    @FXML
    private TableColumn<Reservation, String> customerNameColumn;
    @FXML
    private TableColumn<Reservation, String> roomTypeColumn;
    @FXML
    private TableColumn<Reservation, String> numRoomsColumn;
    @FXML
    private TableColumn<Reservation, String> numDaysColumn;
    
    @FXML
    private Button backButton;

    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ObservableList<Reservation> filteredReservationList = FXCollections.observableArrayList();
    private ReservationService reservationService;

    @FXML
    private void initialize() {
        reservationService = new ReservationService();

        // Load reservations from the database
        loadReservations();

        // Set up the table columns
        bookingIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getBookId())));
        customerNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGuest().getFirstName() + " " + data.getValue().getGuest().getLastName()));
        roomTypeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRooms().stream().map(room -> room.getRoomType()).collect(Collectors.joining(", "))));
        numRoomsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getRooms().size())));
        numDaysColumn.setCellValueFactory(data -> {
            long diff = data.getValue().getCheckOut().getTime() - data.getValue().getCheckIn().getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            return new ReadOnlyStringWrapper(String.valueOf(days));
        });

        // Add the data to the table
        currentBookingsTableView.setItems(reservationList);

        // Add a listener to the phone number field to filter the booking list
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> filterBookingList(newValue));

        // Update the number of current bookings
        updateCurrentBookingsLabel();

        // Add validation to the phone number field
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> validatePhoneNumber(newValue));
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            Date currentDate = new Date();

            // Filter out past reservations
            List<Reservation> currentReservations = reservations.stream()
                    .filter(reservation -> reservation.getCheckOut().after(currentDate))
                    .collect(Collectors.toList());

            reservationList.addAll(currentReservations);
            currentBookingsTableView.setItems(reservationList);
            updateCurrentBookingsLabel();
        } catch (SQLException e) {
            showErrorAlert("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterBookingList(String phoneNumber) {
        filteredReservationList.clear();

        for (Reservation reservation : reservationList) {
            if (String.valueOf(reservation.getGuest().getPhone()).contains(phoneNumber)) {
                filteredReservationList.add(reservation);
            }
        }

        currentBookingsTableView.setItems(filteredReservationList);
        updateCurrentBookingsLabel();
    }

    private void updateCurrentBookingsLabel() {
        numCurrentBookingsLabel.setText("Number of current bookings: " + currentBookingsTableView.getItems().size());
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d*")) {
            phoneNumberField.setText(phoneNumber.replaceAll("[^\\d]", ""));
        }
    }

    @FXML
    private void handleBack() {
        navigateToAdminPage();
    }

    private void navigateToAdminPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AdminPage.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
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
}
