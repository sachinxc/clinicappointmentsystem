package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewAppointmentScreen {
    private Stage primaryStage;
    private NavigationBar navBar;

    public ViewAppointmentScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.navBar = new NavigationBar(primaryStage); // Create a new NavigationBar instance
    }

    public Scene getScene() throws IOException {
        VBox root = new VBox();
        root.getChildren().add(navBar.getNavBar()); // Add the navigation bar to the root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/ViewAppointmentScreen.fxml"));
        VBox bookLayout = loader.load(); // Loads the rest of the FXML
        root.getChildren().add(bookLayout); // Add the screen layout below the nav bar
        return new Scene(root, 600, 400);
    }
}
