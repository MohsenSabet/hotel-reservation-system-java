package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Client;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    @FXML
    private void handleLogin() {
        String loginId = usernameField.getText();
        String password = passwordField.getText();

        try {
            Client client = new Client("localhost", 12345); 
            client.sendCredentials(loginId, password);
            String response = client.getResponse();

            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, admin!");
                navigateToAdminPage(client); // Pass the client instance to keep the connection
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                client.close(); // Close connection on failure
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the server.");
        }
    }

    private void navigateToAdminPage(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminPage.fxml"));
            Parent root = loader.load();
            AdminController controller = loader.getController();
            controller.setClient(client); // Set the client in the AdminController
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void navigateBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/WelcomePage.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToAdminPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AdminPage.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
