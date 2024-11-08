package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AppointmentManagerScreen {
    private Stage primaryStage;
    private NavigationBar navBar;

    public AppointmentManagerScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.navBar = new NavigationBar(primaryStage); // Create a new NavigationBar instance
    }

    public Scene getScene() throws IOException {
        VBox root = new VBox();
        root.getChildren().add(navBar.getNavBar()); // Add the navigation bar to the root

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/AppointmentManagerScreen.fxml"));
        VBox bookLayout = loader.load(); // Loads the rest of the FXML
        root.getChildren().add(bookLayout); // Adding the screen layout below the nav bar

        Scene scene = new Scene(root, 600, 400);

        // Adding the CSS file to the scene
        scene.getStylesheets().add(getClass().getResource("resources/appointmentManager.css").toExternalForm());
        return scene;
    }
}