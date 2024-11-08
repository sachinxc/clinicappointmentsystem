package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Appointment;

import java.io.IOException;
import java.util.List;

public class PaymentsManagerScreen {
    private Stage primaryStage;
    private NavigationBar navBar;

    public PaymentsManagerScreen(Stage primaryStage, List<Appointment> appointments) {
        this.primaryStage = primaryStage;
        this.navBar = new NavigationBar(primaryStage); // Initialize NavigationBar
    }

    public Scene getScene() throws IOException {
        VBox root = new VBox();
        root.getChildren().add(navBar.getNavBar()); // Add Navigation Bar

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/PaymentsManagerScreen.fxml"));
        VBox paymentLayout = loader.load(); // Loads the FXML for PaymentsManagerScreen
        root.getChildren().add(paymentLayout); // Add the FXML layout to the root

        return new Scene(root, 450, 600);
    }
}
