package main;

import helper.JDBC;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.Locale;


/**
 * This is the Main class in the program, this program is a GUI Appointment Scheduler
 * Appointment, and all related information is stored in a Database.
 * This program gets Appointment information from the Database, and can also update the Database.
 */
public class Main extends Application{
    /**
     * The Login page is displayed.
     * @param primaryStage The primary stage for the program
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * This is the Main method in the program.
     * Attempts to first establish a connection to the Database, if connection is successful start method is called.
     * The connection is closed when all windows close.  If unsuccessful, an error message is printed.
     * @param args The arguments of type "String" that this method will be able to accept
     */
    public static void main(String[] args) {
        //Locale.setDefault(new Locale("fr"));
        Boolean connected = JDBC.openConnection();
        if(connected == true) {
            launch();
            JDBC.closeConnection();
        }
        else { System.out.println("Unable to Establish Connection to Database"); }
    }
}
