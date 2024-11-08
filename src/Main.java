import database.DatabaseHandler;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new DatabaseHandler();// Initialize database handler

        MainScreen mainScreen = new MainScreen(primaryStage);
        mainScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
