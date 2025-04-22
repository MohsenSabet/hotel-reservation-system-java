/**********************************************
Project
Course:APD545 - Summer
Last Name:Sabet
First Name:Mohsen
ID:113205165
Section:NBB
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature MS
Date:2024-08-12
**********************************************/

package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Server; // Import the Server class

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Start the server in a new thread
            new Thread(() -> {
                Server.main(null); // This will start the server
            }).start();

            Parent root = FXMLLoader.load(getClass().getResource("/views/WelcomePage.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
            primaryStage.setTitle("ABC Hotel Reservation System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
