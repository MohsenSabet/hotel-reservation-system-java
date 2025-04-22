package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeController {

    @FXML
    private Button adminButton;

    @FXML
    private Button kioskButton;

    @FXML
    private Label dayLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private void initialize() {
        updateDateTime();
        startClock();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("dd MMM");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        dayLabel.setText(now.format(dayFormatter));
        monthLabel.setText(now.format(monthFormatter));
        timeLabel.setText(now.format(timeFormatter));
    }

    private void startClock() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateDateTime());
            }
        }, 0, 1000); // update every second
    }

    @FXML
    private void navigateToKiosk() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/KioskMainPage.fxml"));
            Stage stage = (Stage) kioskButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToAdmin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/LoginPage.fxml"));
            Stage stage = (Stage) adminButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
