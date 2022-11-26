package controller;

import helper.JDBC;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Main;
import model.Appointment;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * This class is the controller responsible for the login screen(login.fxml)
 */
public class loginController implements Initializable  {
    private static final String userName = "sqlUser"; //"sqlUser"
    private static String password = "Passw0rd!"; //"Passw0rd!"
    private String userNameInput = "";
    private String passwordInput = "";
    private static int userId;

    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label location;
    @FXML
    private Label title;


    /**
     * In this initialize method, the labels and texts on the login window are set based on the locale.
     * The location is also set based on the zone from the system.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            ZoneId zone = ZoneId.systemDefault();

            rb = ResourceBundle.getBundle("language/login", Locale.getDefault());
            location.setText(String.valueOf(zone));
            loginButton.setText(rb.getString("login"));
            usernameLabel.setText(rb.getString("username"));
            passwordLabel.setText(rb.getString("password"));
            locationLabel.setText(rb.getString("location"));
            title.setText(rb.getString("title"));

        } catch(Exception e) { System.out.println(e); }
    }

    /**
     * LoginAction is the method that occurs everytime the button 'loginButton' is pressed
     * Login username and password is checked with the table USERS in the database
     * Every login status is logged in the file login_activity.txt
     * The main window is displayed if the user credentials are valid.
     */
    @FXML
    private void LoginAction() throws IOException {
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter outputFile = new PrintWriter(fileWriter);
            this.userNameInput = usernameField.getText();
            this.passwordInput = passwordField.getText();
            userId = JDBC.getUserID(userNameInput, passwordInput);

            if(userId > 0) {
                //prints successful login to file login_activity.txt
                outputFile.print("SUCCESSFUL LOGIN from USER: " + this.userNameInput  + " AT: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
                appointmentsWithin15Minutes(userId);

                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/mainwindow.fxml"));
                Parent mainWindow = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(mainWindow));
                stage.show();
            } else {
                //prints unsuccessful login to file login_activity.txt
                outputFile.print("UNSUCCESSFUL LOGIN from USER: " + this.userNameInput  + " AT: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

                ResourceBundle rb = ResourceBundle.getBundle("language/login", Locale.getDefault());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("Error"));
                alert.setHeaderText(rb.getString("UserNotFound"));
                alert.setContentText(rb.getString("IncorrectNamePassword"));
                alert.showAndWait();
                /*debugging
                System.out.println("Username or password is incorrect");
                System.out.println(this.userNameInput);
                System.out.println(this.passwordInput);
                 */
            }
        outputFile.close();
    }

    /**
     * @param userID the validated user ID is passed into this method in order to find all appointments within 15 minutes of the current time.
     */
    private void appointmentsWithin15Minutes(int userID) {
        ObservableList<Appointment> getAllAppointments = JDBC.getAllAppointments();
        List<Appointment> appointmentsWithin15 = new ArrayList<>();
        LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
        LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
        LocalDateTime startTime;
        boolean appointmentExists = false;

        for (Appointment appointment: getAllAppointments) {
            startTime = appointment.getStartDateAndTime();
            if ((appointment.getUserID() == userID) && (startTime.isAfter(currentTimeMinus15Min) ||
                    startTime.isEqual(currentTimeMinus15Min)) && (startTime.isBefore(currentTimePlus15Min) || (startTime.isEqual(currentTimePlus15Min)))) {
                appointmentsWithin15.add(appointment);
                appointmentExists = true;
            }
        }
        displayAppointmentsWithin15Minutes(appointmentExists, appointmentsWithin15);
    }

    /**
     * @param appointmentExists If an appointment exists within 15 minutes, a popup is displayed giving the appointment id, start time and start date
     * @param appointmentsWithin15 The list of all appointments for the current user, that are within 15 minutes
     * If there are no appointments within 15 minutes for the current user, a popup is displayed showing this information.
     */
    private void displayAppointmentsWithin15Minutes(boolean appointmentExists, List<Appointment> appointmentsWithin15) {
        ResourceBundle rb = ResourceBundle.getBundle("language/login", Locale.getDefault());
        if(appointmentExists == true) {
            for(Appointment appointment : appointmentsWithin15) {
                String displayString = rb.getString("AppointmentWithin15") + appointment.getAppointmentID() + "). " + rb.getString("ScheduledAppointment") + appointment.getStartTime() + " " +
                        rb.getString("On") + " " + appointment.getStartDate();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, displayString);
                alert.setTitle(rb.getString("Alert"));
                alert.setHeaderText(rb.getString("UpcomingAppointment"));
                Optional<ButtonType> confirmation = alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, rb.getString("NoAppointmentWithin15"));
            alert.setTitle(rb.getString("Alert"));
            alert.setHeaderText(rb.getString("NoAppointmentSoon"));
            Optional<ButtonType> confirmation = alert.showAndWait();
        }
    }

    /**
     * return the user ID for the current session
     */
    public static int getUserID() {
        return userId;
    }
}
