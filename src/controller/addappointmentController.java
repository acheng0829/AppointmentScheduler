package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.*;
import java.util.HashSet;
import java.util.Optional;

import static helper.JDBC.connection;

/**
 * This controller is responsible for the Add Appointment Window(addappointment.fxml)
 */
public class addappointmentController {

    @FXML private TextField AppointmentID;
    @FXML private TextField Title;
    @FXML private TextField Description;
    @FXML private TextField Location;
    @FXML private TextField Type;
    @FXML private TextField StartDate;
    @FXML private TextField EndDate;
    @FXML private TextField StartTime;
    @FXML private TextField EndTime;
    @FXML private TextField CustomerID;
    @FXML private TextField UserID;

    @FXML private javafx.scene.control.Button cancelButton;
    @FXML private javafx.scene.control.Button addButton;
    @FXML private ComboBox Contact;

    /**
     * Initializes the Contact Combobox, sets the initial value to the first Contact name
     */
    @FXML
    private void initialize() {
        Contact.setItems(appointmentsController.getContactNames());
        Contact.setValue(appointmentsController.getContactNames().get(0));
    }

    /**
     * checks if appointment is made within valid business hours, defined as 08:00-22:00.
     * @param start starting time and date of the attempted appointment
     * @param end ending time and date of the attempted appointment
     * @return false if the appointment is made outside Business hours, true if the appointment is within business hours.
     */
    private boolean withinBusinessHours(LocalDateTime start, LocalDateTime end) {
        //convert user's zone to EST
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime startUserZoned = start.atZone(zone);
        ZonedDateTime endUserZoned = end.atZone(zone);

        ZonedDateTime startEST = startUserZoned.withZoneSameInstant(zone.of("America/New_York"));
        ZonedDateTime endEST = endUserZoned.withZoneSameInstant(zone.of("America/New_York"));

        LocalDateTime startDateTime = LocalDateTime.ofInstant(startEST.toInstant(), zone.of("America/New_York"));
        LocalDateTime endDateTime = LocalDateTime.ofInstant(endEST.toInstant(), zone.of("America/New_York"));

        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        LocalTime businessStartTime = LocalTime.of(8, 0, 0);
        LocalTime businessEndTime = LocalTime.of(22, 0, 0);

        if (startTime.isBefore(businessStartTime) || startTime.isAfter(businessEndTime) ||
                endTime.isBefore(businessStartTime) || endTime.isAfter(businessEndTime)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment unable to be made because it is outside of business hours(8:00-22:00)");
            Optional<ButtonType> confirmation = alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * @param customerID the ID of the customer of whom the appointment is to be made.
     * @param start starting date and time of the attempted appointment
     * @param end ending date and time of the attempted appointment
     * @return false if the appointment has an overlap with another, true if there is no overlap.
     */
    private boolean noAppointmentOverlap(int customerID, LocalDateTime start, LocalDateTime end) {
        Boolean noOverlap = true;
        try {
            Customer customer = appointmentsController.IDToCustomerMap.get(customerID); //get customer object associated with this ID
            if (customerID == customer.getCustomerID()) {
                for (Appointment appointment : customer.getAppointmentsList()) {
                    if ((start.isBefore(appointment.getEndDateAndTime()) && appointment.getStartDateAndTime().isBefore(end) ||
                            appointment.getStartDateAndTime().isBefore(end) && start.isBefore(appointment.getEndDateAndTime())))
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "An Appointment could not be made.  Appointment Overlap with Appointment: " + appointment.getAppointmentID() +
                                " Starts: " + appointment.getStartTime() + " Ends: " + appointment.getEndTime());
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        noOverlap = false;
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return noOverlap;
    }

    /**
     * Attempts to add an Appointment to the database, and to the Appointments list when this button is pressed.
     */
    @FXML
    private void Add()  {
        try {
            int ID = generateID();
            String title = this.Title.getText();
            String description = this.Description.getText();
            String location = this.Location.getText();
            String contactName = (String) Contact.getSelectionModel().getSelectedItem();
            int contactID = appointmentsController.NameToContactMap.get(contactName).getContactId();
            String type = this.Type.getText();
            int customerID = Integer.parseInt(this.CustomerID.getText());
            int userID = Integer.parseInt(this.UserID.getText());

            LocalDate startDate = LocalDate.parse(this.StartDate.getText());
            LocalDate endDate = LocalDate.parse(this.EndDate.getText());
            LocalTime startTime = LocalTime.parse(this.StartTime.getText());
            LocalTime endTime = LocalTime.parse(this.EndTime.getText());
            LocalDateTime localStart = LocalDateTime.of(startDate, startTime);
            LocalDateTime localEnd = LocalDateTime.of(endDate, endTime);

            //start and end are currently in user's timezone, convert start and end to UTC
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime startUserZoned = localStart.atZone(zone);
            ZonedDateTime endUserZoned = localEnd.atZone(zone);


            ZonedDateTime startUTC = startUserZoned.withZoneSameInstant(zone.of("UTC"));
            ZonedDateTime endUTC = endUserZoned.withZoneSameInstant(zone.of("UTC"));

            LocalDateTime start = LocalDateTime.ofInstant(startUTC.toInstant(), zone.of("UTC"));
            LocalDateTime end = LocalDateTime.ofInstant(endUTC.toInstant(), zone.of("UTC"));

            Boolean withinBusinessHours = withinBusinessHours(localStart, localEnd);
            Boolean noAppointmentOverlap = noAppointmentOverlap(customerID, localStart, localEnd);

            if(withinBusinessHours == true && noAppointmentOverlap == true) {
                Appointment appointment = new Appointment(ID, title, description,
                        location, type, localStart, localEnd, customerID,
                        userID, contactID);

                //add appointment to each customer
                appointmentsController.IDToCustomerMap.get(customerID).addToAppointmentsList(appointment);
                appointment.setContactName(contactName);
                appointment.setContact(appointmentsController.NameToContactMap.get(contactName));
                addAppointment(appointment, start, end);
                mainController.getAppointmentsList().add(appointment);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Attempts to add an Appointment to the database.
     * @param appointment the Appointment that has been created in Add() is passed in order to be added into the database.
     * @param startUTC
     * @param endUTC
     */
    public void addAppointment (Appointment appointment, LocalDateTime startUTC, LocalDateTime endUTC) {
        try {
            String query = "INSERT INTO APPOINTMENTS (Appointment_ID, Title, Description, Location, Type, " +
                    "Customer_ID, User_ID, Contact_ID, Start, End) VALUES (?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = connection.prepareStatement(query );
            ps.setInt(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getAppointmentTitle());
            ps.setString(3, appointment.getAppointmentDescription());
            ps.setString(4, appointment.getAppointmentLocation());
            ps.setString(5, appointment.getAppointmentType());
            ps.setInt(6, appointment.getCustomerID());
            ps.setInt(7, appointment.getUserID());
            ps.setInt(8, appointment.getContactID());
            ps.setTimestamp(9, Timestamp.valueOf(startUTC));
            ps.setTimestamp(10, Timestamp.valueOf(endUTC));
            ps.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Pressing the Cancel button calls the closeWindow method.
     */
    @FXML
    private void Cancel () {closeWindow(cancelButton); }

    /**
     * Pressing the Cancel button calls the closeWindow() method.
     * @param button a button that should initiates the exiting of this window is passed in.
     */
    private void closeWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * Starting from 1, for each iteration, keep adding 1 until an ID in the Appointment list has not been taken is found.
     * @return the lowest positive integer that has not been taken as the new Appointment ID.
     */
    private int generateID() {
        try {
            HashSet<Integer> setOfTakenIDs = new HashSet<>();
            int ID = 1;
            for(Appointment appointment : mainController.getAppointmentsList()) {
                setOfTakenIDs.add(appointment.getAppointmentID());
            }
            while (true) {
                if(!setOfTakenIDs.contains(ID)) { return ID; }
                else { ID+=1; }
            }
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }
}
