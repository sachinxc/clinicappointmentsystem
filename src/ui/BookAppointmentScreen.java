package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BookAppointmentScreen {
    private Stage primaryStage;
    private NavigationBar navBar;

    public BookAppointmentScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.navBar = new NavigationBar(primaryStage); // Create a new NavigationBar instance
    }

    public Scene getScene() throws IOException {
        VBox root = new VBox();
        root.getChildren().add(navBar.getNavBar()); // Add the navigation bar to the root

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/BookAppointmentScreen.fxml"));
        VBox bookLayout = loader.load(); // Loads the rest of the FXML
        root.getChildren().add(bookLayout); // Add the scren layout below the nav bar

        Scene scene = new Scene(root, 600, 400);

        // Adding the CSS file to the scene
        scene.getStylesheets().add(getClass().getResource("resources/bookAppointment.css").toExternalForm());

        return scene;
    }
}
