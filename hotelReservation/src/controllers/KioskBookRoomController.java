package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.*;
import service.RoomService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KioskBookRoomController {

    @FXML
    private Spinner<Integer> numAdultsSpinner, numChildrenSpinner, singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseRoomSpinner;
    @FXML
    private DatePicker checkInDatePicker, checkOutDatePicker;
    @FXML
    private Button proceedButton, cancelButton, clearButton;
    @FXML
    private VBox rootPane;

    @FXML
    private Label roomSuggestionLabel;
    
    @FXML
    private Label singleRateLabel, singleCapacityLabel;
    @FXML
    private Label doubleRateLabel, doubleCapacityLabel;
    @FXML
    private Label deluxeRateLabel, deluxeCapacityLabel;
    @FXML
    private Label penthouseRateLabel, penthouseCapacityLabel;

    private RoomService roomService;
    private Reservation reservation;

    private static final double TORONTO_TAX_RATE = 13.0; // Toronto tax rate

    @FXML
    private void initialize() {
        roomService = new RoomService();

        loadRoomData();

        numAdultsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        numChildrenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));

        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));
        setCheckInDatePickerFactory();
        setCheckOutDatePickerFactory();

        checkInDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkOutDatePicker.setValue(newValue.plusDays(1));
            setCheckOutDatePickerFactory();
        });

        proceedButton.setDisable(true);
        proceedButton.setOnAction(event -> handleProceed());
        clearButton.setOnAction(event -> handleClear());
        cancelButton.setOnAction(event -> handleCancel());

        numAdultsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateInputs();
            updateRoomSuggestion();
        });
        numChildrenSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateInputs();
            updateRoomSuggestion();
        });
        checkInDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
        checkOutDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
        singleRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
        doubleRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
        deluxeRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
        penthouseRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs());
    }

    private void setCheckInDatePickerFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                } else {
                    setDisable(false);
                }
            }
        };
        checkInDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void setCheckOutDatePickerFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                LocalDate checkInDate = checkInDatePicker.getValue();
                if (item.isBefore(LocalDate.now()) || item.isBefore(checkInDate.plusDays(1)) || item.isAfter(checkInDate.plusDays(90))) {
                    setDisable(true);
                } else {
                    setDisable(false);
                }
                if (checkInDate != null && !item.isBefore(checkInDate) && item.isBefore(checkOutDatePicker.getValue())) {
                    setStyle("-fx-background-color: #80cbc4;"); // Highlight the date range
                }
            }
        };
        checkOutDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void loadRoomData() {
        try {
            // Fetch the available rooms and their counts
            Room singleRoom = roomService.getAvailableRoomByType(SingleRoom.class);
            Room doubleRoom = roomService.getAvailableRoomByType(DoubleRoom.class);
            Room deluxeRoom = roomService.getAvailableRoomByType(DeluxeRoom.class);
            Room penthouseRoom = roomService.getAvailableRoomByType(PentHouse.class);

            int singleRoomCount = roomService.countAvailableRoomsByType(SingleRoom.class);
            int doubleRoomCount = roomService.countAvailableRoomsByType(DoubleRoom.class);
            int deluxeRoomCount = roomService.countAvailableRoomsByType(DeluxeRoom.class);
            int penthouseRoomCount = roomService.countAvailableRoomsByType(PentHouse.class);

            // Update UI components for each room type
            updateRoomUI(singleRoom, singleRoomSpinner, singleRateLabel, singleCapacityLabel, singleRoomCount);
            updateRoomUI(doubleRoom, doubleRoomSpinner, doubleRateLabel, doubleCapacityLabel, doubleRoomCount);
            updateRoomUI(deluxeRoom, deluxeRoomSpinner, deluxeRateLabel, deluxeCapacityLabel, deluxeRoomCount);
            updateRoomUI(penthouseRoom, penthouseRoomSpinner, penthouseRateLabel, penthouseCapacityLabel, penthouseRoomCount);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error loading room data from database.");
        }
    }

    private void updateRoomUI(Room room, Spinner<Integer> roomSpinner, Label rateLabel, Label capacityLabel, int availableCount) {
        if (room != null) {
            // Set the spinner to show available room counts
            roomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, availableCount, 0));

            // Update rate and capacity labels with room information
            rateLabel.setText(String.format("$%.2f", room.getRate()));
            capacityLabel.setText(String.valueOf(room.getCapacity()));
        } else {
            // If no room is available, disable spinner and show "N/A" for rate and capacity
            roomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
            rateLabel.setText("N/A");
            capacityLabel.setText("N/A");
        }
    }


    private void updateRoomSuggestion() {
        try {
            int numAdults = numAdultsSpinner.getValue();
            int numChildren = numChildrenSpinner.getValue();
            int totalGuests = numAdults + numChildren;

            int singleRoomCount = roomService.countAvailableRoomsByType(SingleRoom.class);
            int doubleRoomCount = roomService.countAvailableRoomsByType(DoubleRoom.class);
            int deluxeRoomCount = roomService.countAvailableRoomsByType(DeluxeRoom.class);
            int penthouseRoomCount = roomService.countAvailableRoomsByType(PentHouse.class);

            int suggestedSingleRooms = 0;
            int suggestedDoubleRooms = 0;
            int suggestedDeluxeRooms = 0;
            int suggestedPenthouseRooms = 0;

            // First, try to allocate single and double rooms
            if (singleRoomCount * 2 >= totalGuests) {
                suggestedSingleRooms = (int) Math.ceil((double) totalGuests / 2);
            } else {
                suggestedSingleRooms = singleRoomCount;
                int remainingGuests = totalGuests - (singleRoomCount * 2);
                if (doubleRoomCount * 4 >= remainingGuests) {
                    suggestedDoubleRooms = (int) Math.ceil((double) remainingGuests / 4);
                } else {
                    suggestedDoubleRooms = doubleRoomCount;
                    remainingGuests -= (doubleRoomCount * 4);

                    // Then, consider deluxe rooms
                    if (deluxeRoomCount * 2 >= remainingGuests) {
                        suggestedDeluxeRooms = (int) Math.ceil((double) remainingGuests / 2);
                    } else {
                        suggestedDeluxeRooms = deluxeRoomCount;
                        remainingGuests -= (deluxeRoomCount * 2);

                        // Finally, consider penthouse rooms
                        if (penthouseRoomCount * 2 >= remainingGuests) {
                            suggestedPenthouseRooms = (int) Math.ceil((double) remainingGuests / 2);
                        } else {
                            suggestedPenthouseRooms = penthouseRoomCount;
                            remainingGuests -= (penthouseRoomCount * 2);

                            // If there are still remaining guests, it means we cannot accommodate all guests
                            if (remainingGuests > 0) {
                                showErrorAlert("Not enough rooms available to accommodate all guests.");
                            }
                        }
                    }
                }
            }

            StringBuilder suggestion = new StringBuilder("Suggested Rooms: ");
            if (suggestedSingleRooms > 0) {
                suggestion.append(suggestedSingleRooms).append(" Single ");
            }
            if (suggestedDoubleRooms > 0) {
                suggestion.append(suggestedDoubleRooms).append(" Double ");
            }
            if (suggestedDeluxeRooms > 0) {
                suggestion.append(suggestedDeluxeRooms).append(" Deluxe ");
            }
            if (suggestedPenthouseRooms > 0) {
                suggestion.append(suggestedPenthouseRooms).append(" Penthouse ");
            }

            roomSuggestionLabel.setText(suggestion.toString().trim());
        } catch (SQLException e) {
            showErrorAlert("Error retrieving room data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateInputs() {
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        int numAdults = numAdultsSpinner.getValue();
        int numChildren = numChildrenSpinner.getValue();
        int totalGuests = numAdults + numChildren;

        int singleRoomCount = singleRoomSpinner.getValue();
        int doubleRoomCount = doubleRoomSpinner.getValue();
        int deluxeRoomCount = deluxeRoomSpinner.getValue();
        int penthouseRoomCount = penthouseRoomSpinner.getValue();

        // Calculate total capacity
        int totalCapacity = (singleRoomCount * 2) + (doubleRoomCount * 4) + (deluxeRoomCount * 2) + (penthouseRoomCount * 2);

        boolean isValid = checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate) && (singleRoomCount + doubleRoomCount + deluxeRoomCount + penthouseRoomCount > 0) && totalGuests <= totalCapacity;
        proceedButton.setDisable(!isValid);
    }

    @FXML
    private void handleProceed() {
        createReservation();
        loadCustomerInfo();
    }

    private void createReservation() {
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();

        Date checkIn = Date.from(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date checkOut = Date.from(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Room> rooms = new ArrayList<>();
        addRoomsToList(rooms, SingleRoom.class, singleRoomSpinner.getValue());
        addRoomsToList(rooms, DoubleRoom.class, doubleRoomSpinner.getValue());
        addRoomsToList(rooms, DeluxeRoom.class, deluxeRoomSpinner.getValue());
        addRoomsToList(rooms, PentHouse.class, penthouseRoomSpinner.getValue());

        double totalCost = rooms.stream().mapToDouble(room -> room.getRate() * getDuration(checkIn, checkOut)).sum();
        Bill bill = new Bill(0, totalCost, 0, TORONTO_TAX_RATE, false);

        // Now the reservation also stores the number of adults and children
        reservation = new Reservation(0, new Date(), checkIn, checkOut, reservation != null ? reservation.getGuest() : null, rooms, bill, ReservationSource.KIOSK, numAdultsSpinner.getValue(), numChildrenSpinner.getValue());
    }

    // Helper method to calculate duration in days
    private long getDuration(Date checkIn, Date checkOut) {
        long diffInMillies = Math.abs(checkOut.getTime() - checkIn.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    // Helper method to add rooms to the list based on type and count
    private void addRoomsToList(List<Room> rooms, Class<? extends Room> roomType, int count) {
        try {
            for (int i = 0; i < count; i++) {
                Room room = roomService.getAvailableRoomByType(roomType);
                if (room != null) {
                    rooms.add(room);
                    // Notice that we are not marking rooms as unavailable yet. This will be done after confirmation.
                } else {
                    showErrorAlert("Not enough " + roomType.getSimpleName() + "s available.");
                    return;
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Error retrieving room data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCustomerInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/KioskCustomerInfo.fxml"));
            Parent customerInfoView = loader.load();

            KioskCustomerInfoController KioskcustomerInfoController = loader.getController();
            KioskcustomerInfoController.setReservation(reservation); // Pass the reservation object

            rootPane.getChildren().clear();
            rootPane.getChildren().add(customerInfoView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the reservation when coming back from the CustomerInfoController
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        restoreRoomSelections();
    }

    private void restoreRoomSelections() {
        // Set the spinners and other fields based on the existing reservation
        if (reservation != null) {
            // Update spinners based on the selected rooms in the reservation
            int singleRooms = (int) reservation.getRooms().stream().filter(room -> room instanceof SingleRoom).count();
            int doubleRooms = (int) reservation.getRooms().stream().filter(room -> room instanceof DoubleRoom).count();
            int deluxeRooms = (int) reservation.getRooms().stream().filter(room -> room instanceof DeluxeRoom).count();
            int penthouseRooms = (int) reservation.getRooms().stream().filter(room -> room instanceof PentHouse).count();

            singleRoomSpinner.getValueFactory().setValue(singleRooms);
            doubleRoomSpinner.getValueFactory().setValue(doubleRooms);
            deluxeRoomSpinner.getValueFactory().setValue(deluxeRooms);
            penthouseRoomSpinner.getValueFactory().setValue(penthouseRooms);

            // Restore number of adults and children
            numAdultsSpinner.getValueFactory().setValue(reservation.getNumAdults());
            numChildrenSpinner.getValueFactory().setValue(reservation.getNumChildren());

            // Restore other fields such as the check-in and check-out dates
            checkInDatePicker.setValue(reservation.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            checkOutDatePicker.setValue(reservation.getCheckOut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            // Adjust the suggestion label or other UI elements based on the restored data
            updateRoomSuggestion();
        }
    }

    @FXML
    private void handleClear() {
        numAdultsSpinner.getValueFactory().setValue(1);
        numChildrenSpinner.getValueFactory().setValue(0);
        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));
        setCheckOutDatePickerFactory();
        singleRoomSpinner.getValueFactory().setValue(0);
        doubleRoomSpinner.getValueFactory().setValue(0);
        deluxeRoomSpinner.getValueFactory().setValue(0);
        penthouseRoomSpinner.getValueFactory().setValue(0);
        proceedButton.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel the booking?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                navigateToMainPage(); // Ensure this points to the KioskMainPage
            }
        });
    }

    private void navigateToMainPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/KioskMainPage.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
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
