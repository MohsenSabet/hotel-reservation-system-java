package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KioskMainPageController {
    
    @FXML
    private Button startButton;


    @FXML
    private Button backButton;

    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
       
    }


    @FXML
    private void handleBookRoom() {
        loadView("/views/KioskBookRoom.fxml");
    }

    @FXML
    private void handleBack() {
        navigateToWelcomePage();
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

    private void navigateToWelcomePage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/WelcomePage.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public StackPane getContentPane() {
        return contentPane;
    }
}
