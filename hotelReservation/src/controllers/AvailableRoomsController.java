package controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Reservation;
import models.Room;
import service.ReservationService;
import service.RoomService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AvailableRoomsController {

    @FXML
    private Label numAvailableRoomsLabel;
    @FXML
    private TableView<Room> availableRoomsTableView;
    @FXML
    private TableColumn<Room, String> roomIdColumn;
    @FXML
    private TableColumn<Room, String> roomTypeColumn;
    @FXML
    private TableColumn<Room, String> availabilityColumn;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button filterButton, backButton;
    @FXML
    private SplitMenuButton roomTypeMenuButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private ObservableList<Room> availableRoomsList = FXCollections.observableArrayList();
    private RoomService roomService = new RoomService();
    private ReservationService reservationService = new ReservationService();
    private String selectedRoomType = "All";  // Default value

    @FXML
    private void initialize() {
        // Set up the table columns
        roomIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getRoomId())));
        roomTypeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRoomType()));
        availabilityColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(getRoomAvailability(data.getValue())));

        // Load rooms and their availability
        loadAvailableRooms();

        // Add the data to the table
        availableRoomsTableView.setItems(availableRoomsList);

        // Update the number of available rooms
        updateAvailableRoomsLabel();

        // Restrict date pickers to today or future dates
        restrictDatePickers();

        // Set the filter button action
        filterButton.setOnAction(event -> handleFilter());

        // Set actions for room type selection
        for (MenuItem item : roomTypeMenuButton.getItems()) {
            item.setOnAction(event -> handleRoomTypeSelection(item.getText()));
        }
    }

    private void restrictDatePickers() {
        // Disable past dates for startDatePicker
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Disable past dates for endDatePicker
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Ensure end date cannot be before start date
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                endDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isBefore(newValue) || date.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });
            }
        });
    }

    private void loadAvailableRooms() {
        loadingIndicator.setVisible(true);  // Show loading indicator
        new Thread(() -> {
            try {
                List<Room> allRooms = roomService.getAllRooms();
                availableRoomsList.addAll(allRooms);
            } catch (SQLException e) {
                showErrorAlert("Error loading rooms: " + e.getMessage());
                e.printStackTrace();
            } finally {
                loadingIndicator.setVisible(false);  // Hide loading indicator
            }
        }).start();
    }

    private String getRoomAvailability(Room room) {
        try {
            List<Reservation> reservations = reservationService.getReservationsForRoom(room.getRoomId());
            List<DateRange> availableRanges = calculateAvailableDateRanges(reservations);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
            return availableRanges.stream()
                    .map(range -> {
                        Date startDate = range.getStart();
                        Date endDate = range.getEnd();

                        String startStr = startDate != null ? dateFormat.format(startDate) : "NA";
                        String endStr = endDate != null ? dateFormat.format(endDate) : "Onwards";

                        return startStr + " to " + endStr;
                    })
                    .collect(Collectors.joining("; "));

        } catch (SQLException e) {
            showErrorAlert("Error calculating availability: " + e.getMessage());
            e.printStackTrace();
            return "Error";
        }
    }

    private List<DateRange> calculateAvailableDateRanges(List<Reservation> reservations) {
        List<DateRange> availableRanges = new ArrayList<>();
        Date currentDate = new Date();
        Collections.sort(reservations, Comparator.comparing(Reservation::getCheckIn));

        Date lastEndDate = currentDate;

        for (Reservation reservation : reservations) {
            if (reservation.getCheckIn().after(lastEndDate)) {
                availableRanges.add(new DateRange(lastEndDate, reservation.getCheckIn()));
            }
            lastEndDate = reservation.getCheckOut();
        }

        // Add a range from the last reservation to infinity (or some far future date)
        availableRanges.add(new DateRange(lastEndDate, null));

        return availableRanges;
    }

    @FXML
    private void handleFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        Date start = null;
        Date end = null;

        if (startDate != null) {
            start = java.sql.Date.valueOf(startDate);
        }

        if (endDate != null) {
            end = java.sql.Date.valueOf(endDate);
        }

        filterAvailableRooms(start, end, selectedRoomType);
    }

    private void filterAvailableRooms(Date start, Date end, String roomType) {
        loadingIndicator.setVisible(true);  // Show loading indicator
        new Thread(() -> {
            List<Room> filteredRooms = availableRoomsList.stream()
                    .filter(room -> {
                        boolean matchesType = "All".equals(roomType) || roomType.equals(room.getRoomType());
                        boolean isAvailable = (start == null && end == null) || isRoomAvailableInRange(room, start, end);
                        return matchesType && isAvailable;
                    })
                    .collect(Collectors.toList());

            javafx.application.Platform.runLater(() -> {
                availableRoomsTableView.setItems(FXCollections.observableArrayList(filteredRooms));
                updateAvailableRoomsLabel();
                loadingIndicator.setVisible(false);  // Hide loading indicator
            });
        }).start();
    }

    private boolean isRoomAvailableInRange(Room room, Date start, Date end) {
        try {
            List<Reservation> reservations = reservationService.getReservationsForRoom(room.getRoomId());
            List<DateRange> availableRanges = calculateAvailableDateRanges(reservations);

            return availableRanges.stream().anyMatch(range -> {
                Date rangeStart = range.getStart();
                Date rangeEnd = range.getEnd();

                if (rangeEnd == null) {
                    return start == null || !start.before(rangeStart);
                }

                return start == null || (start.equals(rangeStart) || start.after(rangeStart))
                        && (end == null || (end.equals(rangeEnd) || end.before(rangeEnd)));
            });

        } catch (SQLException e) {
            showErrorAlert("Error checking room availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void handleRoomTypeSelection(String roomType) {
        selectedRoomType = roomType;
        roomTypeMenuButton.setText(roomType);  
        filterAvailableRooms(null, null, selectedRoomType);  // Trigger filter to show selected room type
    }

    private void updateAvailableRoomsLabel() {
        numAvailableRoomsLabel.setText("No of Available rooms: " + availableRoomsTableView.getItems().size());
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
        
    }

    private class DateRange {
        private Date start;
        private Date end;

        public DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }
    }
}
