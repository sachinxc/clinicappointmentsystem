package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NavigationBar {
    private final Stage primaryStage;
    private HBox navBar; // Store the navigation bar

    @FXML
    private Button homeNavButton;
    @FXML
    private Button bookNavButton;
    @FXML
    private Button viewNavButton;
    @FXML
    private Button manageNavButton;
    @FXML
    private Button paymentNavButton;

    public NavigationBar(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadFXML(); // Load FXML when creating an instance
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/NavigationBar.fxml"));
            loader.setController(this);
            navBar = loader.load(); // Load and assign to navBar
            initializeButtons(); // Set button actions
        } catch (IOException e) {
            e.printStackTrace(); // Handle your exceptions better
        }
    }

    // Method to get the HBox containing the navigation buttons
    public HBox getNavBar() {
        return navBar;
    }

    //the below method is implemented to set the buttons actions when clicked, it will take to the respective screen
    private void initializeButtons() {
        homeNavButton.setOnAction(e -> {
            MainScreen mainScreen = new MainScreen(primaryStage);
            switchToScene(mainScreen.getScene());
        });

        bookNavButton.setOnAction(e -> {
            BookAppointmentScreen bookScreen = new BookAppointmentScreen(primaryStage);
            try {
                switchToScene(bookScreen.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        viewNavButton.setOnAction(e -> {
            ViewAppointmentScreen viewScreen = new ViewAppointmentScreen(primaryStage);
            try {
                switchToScene(viewScreen.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        manageNavButton.setOnAction(e -> {
            AppointmentManagerScreen manageScreen = new AppointmentManagerScreen(primaryStage);
            try {
                switchToScene(manageScreen.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        paymentNavButton.setOnAction(e -> {
            List<models.Appointment> appointments = new database.DatabaseHandler().getAllAppointments();
            PaymentsManagerScreen paymentScreen = new PaymentsManagerScreen(primaryStage, appointments);
            try {
                switchToScene(paymentScreen.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void switchToScene(Scene newScene) {
        boolean isFullScreen = primaryStage.isFullScreen();
        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();

        primaryStage.setScene(newScene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setFullScreen(isFullScreen);
    }
}
