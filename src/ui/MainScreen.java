package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class MainScreen {
    private final Stage primaryStage;

    public MainScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // method to return the scene
    public Scene getScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/MainScreen.fxml"));
            VBox root = loader.load();

            // Create the navigation bar
            NavigationBar navBar = new NavigationBar(primaryStage);
            root.getChildren().add(navBar.getNavBar());

            // Return the scene for this screen with the set resolution
            return new Scene(root, 1280, 720);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error (e.g., show an alert or log it)
            return null;
        }
    }

    // method to show the scene which is the main screen
    public void show() {
        primaryStage.setTitle("Aurora Appointment Management System"); //the apps title name
        primaryStage.setScene(getScene());  // Call getScene() to get the scene and set it
        primaryStage.show();
    }
}
