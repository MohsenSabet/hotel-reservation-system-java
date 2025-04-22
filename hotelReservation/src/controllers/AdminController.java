package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Client;
import service.*;

import java.io.IOException;

public class AdminController {

    @FXML
    private StackPane contentPane;
    @FXML
    private Button logoutButton;

    private Client client; // Add a Client instance

    public AdminController() {
        new ReservationService();
        new BillService();
        new GuestService();
        new RoomService();
        new LoginService();
    }

    // Method to set the client instance after login
    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleLogout() {
        // Confirm logout action
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // Send logout request to the server
                    if (client != null) {
                        client.logout(); // Notify the server and close the connection
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                navigateToWelcome();
            }
        });
    }

    private void navigateToWelcome() {
        try {
            // Load the login view
            Parent root = FXMLLoader.load(getClass().getResource("/views/WelcomePage.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookRoom() {
        loadView("/views/BookRoom.fxml");
    }

    @FXML
    private void handleBillingService() {
        loadView("/views/BillingService.fxml");
    }

    @FXML
    private void handleCurrentBookings() {
        loadView("/views/CurrentBookings.fxml");
    }

    @FXML
    private void handleAvailableRooms() {
        loadView("/views/AvailableRooms.fxml");
    }

    @FXML
    private void handleSearch() {
        loadView("/views/Search.fxml");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StackPane getContentPane() {
        return contentPane;
    }
}
