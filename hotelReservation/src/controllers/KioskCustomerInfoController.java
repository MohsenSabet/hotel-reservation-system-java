package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.Guest;
import models.Reservation;
import models.ReservationSource;
import models.Room;
import service.GuestService;
import service.ReservationService;
import service.RoomService;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import database.DataAccess;

public class KioskCustomerInfoController {

    @FXML
    private VBox rootPane;
    @FXML
    private ComboBox<String> titleComboBox;
    @FXML
    private TextField firstNameField, lastNameField, emailField, phoneField, streetAddressField, cityField, stateField, postalCodeField, countryField;
    @FXML
    private Button confirmButton, cancelButton, clearButton, backButton, proceedButton;
    @FXML
    private Label mandatoryLabel;
    @FXML
    private GridPane summaryGrid;

    private Reservation reservation;
    private GuestService guestService;
    private ReservationService reservationService;
    private RoomService roomService;
    private boolean vaildated = false ;

    @FXML
    private void initialize() {
        guestService = new GuestService();
        reservationService = new ReservationService();
        roomService = new RoomService();

        titleComboBox.getItems().addAll("Mr.", "Mrs.", "Ms.", "Miss", "Mx.", "Dr.", "Prof.", "Rev.", "Sir", "Madam", "Lord", "Lady", "Capt.", "Major", "Col.", "Lt. Col.", "Lt.", "Cmdr.", "Adm.", "Chief", "Sgt.", "Officer");

        proceedButton.setOnAction(event -> handleProceed());
        confirmButton.setOnAction(event -> handleConfirm());
        clearButton.setOnAction(event -> handleClear());
        cancelButton.setOnAction(event -> handleCancel());
        backButton.setOnAction(event -> handleBack());

        confirmButton.setDisable(true); // Start with Confirm button disabled
        summaryGrid.setVisible(false); // Hide the summary grid until "Proceed with Booking" is clicked
        mandatoryLabel.setVisible(false);

        titleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> restrictNumericInput(phoneField));

        // Attempt to restore data if available
        restoreGuestInformation();
    }
    
    private void validateInputs() {
        boolean allFieldsFilled = !firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() && !phoneField.getText().isEmpty();

        boolean validEmail = Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", emailField.getText());
        boolean validPhone = phoneField.getText().matches("\\d+");

        // Check if the first name is filled out
        if (firstNameField.getText().isEmpty()) {
            firstNameField.setStyle("-fx-border-color: red;");
        } else {
            firstNameField.setStyle("");
        }

        // Check if the last name is filled out
        if (lastNameField.getText().isEmpty()) {
            lastNameField.setStyle("-fx-border-color: red;");
        } else {
            lastNameField.setStyle("");
        }

        // Check if the email is valid
        if (!validEmail) {
            emailField.setStyle("-fx-border-color: red;");
        } else {
            emailField.setStyle("");
        }

        // Check if the phone number is valid
        if (!validPhone) {
            phoneField.setStyle("-fx-border-color: red;");
        } else {
            phoneField.setStyle("");
        }

        // Enable or disable the Confirm button based on validation
        if (allFieldsFilled && validEmail && validPhone) {
        	vaildated = true;
        } else {
            confirmButton.setDisable(true); // Disable Confirm button if validation fails
        }
    }
    private void restrictNumericInput(TextField textField) {
        if (!textField.getText().matches("\\d*")) {
            textField.setText(textField.getText().replaceAll("[^\\d]", ""));
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;

        if (reservation != null) {
        
            if (reservation.getGuest() != null) {
             
                setGuestInformation(reservation.getGuest());
            }
        }
    }

    private void restoreGuestInformation() {
        if (reservation != null && reservation.getGuest() != null) {
            setGuestInformation(reservation.getGuest());
        }
    }

    public void setGuestInformation(Guest guest) {
        if (guest != null) {

            titleComboBox.setValue(guest.getTitle());
            firstNameField.setText(guest.getFirstName());
            lastNameField.setText(guest.getLastName());
            emailField.setText(guest.getEmail());
            phoneField.setText(String.valueOf(guest.getPhone()));

            String[] addressParts = guest.getAddress().split(", ");
            if (addressParts.length >= 5) {
                streetAddressField.setText(addressParts[0]);
                cityField.setText(addressParts[1]);
                stateField.setText(addressParts[2]);
                postalCodeField.setText(addressParts[3]);
                countryField.setText(addressParts[4]);
            }
        }
    }

    @FXML
    private void handleProceed() {
        validateInputs();
        if (vaildated) {
            summaryGrid.setVisible(true); // Make the booking summary visible
            updateBookingSummary(); // Update the summary with the latest data
            confirmButton.setDisable(false); // Enable Confirm button
        } else {
            showErrorAlert("Please fill out all required fields with valid information before proceeding.");
        }
    }

    @FXML
    private void handleConfirm() {
        try (Connection connection = DataAccess.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            Guest guest = new Guest(
                titleComboBox.getValue(),
                firstNameField.getText(),
                lastNameField.getText(),
                String.format("%s, %s, %s, %s, %s", streetAddressField.getText(), cityField.getText(), stateField.getText(), postalCodeField.getText(), countryField.getText()),
                Long.parseLong(phoneField.getText()),
                emailField.getText()
            );

            guestService.addGuest(guest, connection);
            reservation.setGuest(guest);
            reservation.setSource(ReservationSource.KIOSK);

            // Save the reservation to the database
            reservationService.addReservation(reservation);

            // Mark rooms as unavailable in the database only after confirmation
            List<Room> rooms = reservation.getRooms();
            for (Room room : rooms) {
                roomService.updateRoomAvailability(room.getRoomId(), false);
            }

            connection.commit(); // Commit transaction

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Reservation confirmed successfully!");

            // Handle the "OK" button action
            successAlert.setOnHidden(evt -> navigateToAdminPage());
            successAlert.showAndWait();

        } catch (Exception e) {
            showErrorAlert("Error confirming reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        if (reservation != null) {
            try {
                Long phone = phoneField.getText().isEmpty() ? 0L : Long.parseLong(phoneField.getText());

                Guest guest = new Guest(
                    titleComboBox.getValue(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    String.format("%s, %s, %s, %s, %s", streetAddressField.getText(), cityField.getText(), stateField.getText(), postalCodeField.getText(), countryField.getText()),
                    phone,
                    emailField.getText()
                );

                reservation.setGuest(guest);
                
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid phone number. Please enter a valid number.");
                return;
            }
        }

        loadView("/views/KioskBookRoom.fxml", reservation);
    }

    @FXML
    private void handleClear() {
        titleComboBox.setValue("Mr.");
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        streetAddressField.clear();
        cityField.clear();
        stateField.clear();
        postalCodeField.clear();
        countryField.clear();
        confirmButton.setDisable(true); // Disable Confirm button on clear
        summaryGrid.setVisible(false); // Hide the summary grid on clear
    }

    @FXML
    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel the booking?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                navigateToAdminPage();
            }
        });
    }

    private void navigateToAdminPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/KioskMainPage.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxml, Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            // Ensure the correct controller is used
            if (fxml.contains("KioskBookRoom.fxml")) {
                KioskBookRoomController controller = loader.getController();
                controller.setReservation(reservation);  // Pass the reservation back to the controller
            }

            StackPane contentPane = (StackPane) rootPane.getScene().lookup("#contentPane");
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateBookingSummary() {
        if (reservation != null) {
            summaryGrid.getChildren().clear(); // Clear previous summary

            // Create a SimpleDateFormat for the desired date format
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            summaryGrid.add(new Label("Check-In Date:"), 0, 0);
            summaryGrid.add(new Label(dateFormatter.format(reservation.getCheckIn())), 1, 0);

            summaryGrid.add(new Label("Check-Out Date:"), 0, 1);
            summaryGrid.add(new Label(dateFormatter.format(reservation.getCheckOut())), 1, 1);

            summaryGrid.add(new Label("Number of Adults:"), 0, 2);
            summaryGrid.add(new Label(String.valueOf(reservation.getNumAdults())), 1, 2);

            summaryGrid.add(new Label("Number of Children:"), 0, 3);
            summaryGrid.add(new Label(String.valueOf(reservation.getNumChildren())), 1, 3);

            // Group rooms by type and show the quantity
            Map<String, Integer> roomCounts = new HashMap<>();
            double roomRate = 0;

            for (Room room : reservation.getRooms()) {
                roomRate = room.getRate(); 
                roomCounts.put(room.getRoomType(), roomCounts.getOrDefault(room.getRoomType(), 0) + 1);
            }

            summaryGrid.add(new Label("Rooms:"), 0, 4);
            VBox roomsBox = new VBox();
            for (Map.Entry<String, Integer> entry : roomCounts.entrySet()) {
                roomsBox.getChildren().add(new Label(entry.getValue() + " " + entry.getKey() + " rooms @ $" + roomRate));
            }
            summaryGrid.add(roomsBox, 1, 4);

            summaryGrid.add(new Label("Total Cost:"), 0, 5);
            summaryGrid.add(new Label("$" + reservation.calculateTotalCost()), 1, 5);
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
