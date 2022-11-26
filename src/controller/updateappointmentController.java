package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Optional;

import static helper.JDBC.connection;

public class updateappointmentController {

    @FXML private TextField AppointmentID;
    @FXML private TextField Title;
    @FXML private TextField Description;
    @FXML private TextField Location;
    @FXML private ComboBox Contact;

    @FXML private TextField Type;
    @FXML private TextField StartDate;
    @FXML private TextField EndDate;
    @FXML private TextField StartTime;
    @FXML private TextField EndTime;
    @FXML private TextField CustomerID;
    @FXML private TextField UserID;

    private static Appointment selectedAppointment;

    @FXML private javafx.scene.control.Button cancelButton;
    @FXML private javafx.scene.control.Button updateButton;

    @FXML
    private void initialize() {
        CustomerID.setText(Integer.toString(selectedAppointment.getCustomerID()));
        AppointmentID.setText(Integer.toString(selectedAppointment.getAppointmentID()));
        Title.setText(selectedAppointment.getAppointmentTitle());
        Description.setText(selectedAppointment.getAppointmentDescription());
        Location.setText(selectedAppointment.getAppointmentLocation());
        Type.setText(selectedAppointment.getAppointmentType());
        StartDate.setText(String.valueOf(selectedAppointment.getStartDate()));
        EndDate.setText(String.valueOf(selectedAppointment.getEndDate()));
        StartTime.setText(String.valueOf(selectedAppointment.getStartTime()));
        EndTime.setText(String.valueOf(selectedAppointment.getEndTime()));
        UserID.setText(Integer.toString(selectedAppointment.getUserID()));

        Contact.setValue(selectedAppointment.getContactName()); //convert contact id to name
        Contact.setItems(appointmentsController.getContactNames());
    }


    /**
     * checks if appointment is made within valid business hours, defined as 08:00-22:00.
     * @param start starting time and date of the attempted appointment
     * @param end ending time and date of the attempted appointment
     * @return false if the appointment is made outside Business hours, true if the appointment is within business hours.
     */
    private boolean withinBusinessHours(LocalDateTime start, LocalDateTime end) {
        //must convert user's zone to EST
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment unable to be made because it is outside of business hours(8:00-22:00 EST)");
            Optional<ButtonType> confirmation = alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * @param appointmentID the ID of the appointment
     * @param customerID the ID of the customer of whom the appointment is to be made.
     * @param start starting date and time of the attempted appointment
     * @param end ending date and time of the attempted appointment
     * @return false if the appointment has an overlap with another, true if there is no overlap.
     */
    private boolean noAppointmentOverlap(int appointmentID, int customerID, LocalDateTime start, LocalDateTime end) {
        Boolean noOverlap = true;
        try {
            Customer customer = appointmentsController.IDToCustomerMap.get(customerID); //get customer object associated with this ID
            if (customerID == customer.getCustomerID()) {
                for (Appointment appointment : customer.getAppointmentsList()) {
                    if ((appointment.getAppointmentID() != appointmentID) && (start.isBefore(appointment.getEndDateAndTime()) && appointment.getStartDateAndTime().isBefore(end) ||
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

    @FXML
    private void Update() {
        try {
            int customerID = Integer.parseInt(this.CustomerID.getText());
            int appointmentID = selectedAppointment.getAppointmentID();
            LocalTime startTime = LocalTime.parse(this.StartTime.getText());
            LocalTime endTime = LocalTime.parse(this.EndTime.getText());
            LocalDate startDate = LocalDate.parse(this.StartDate.getText());
            LocalDate endDate = LocalDate.parse(this.EndDate.getText());
            LocalDateTime localStart = LocalDateTime.of(startDate, startTime);
            LocalDateTime localEnd = LocalDateTime.of(endDate, endTime);

            ZoneId zone = ZoneId.systemDefault(); //get zone for user
            ZonedDateTime startUserZoned = localStart.atZone(zone);
            ZonedDateTime endUserZoned = localEnd.atZone(zone);

            ZonedDateTime startUTC = startUserZoned.withZoneSameInstant(zone.of("UTC"));
            ZonedDateTime endUTC = endUserZoned.withZoneSameInstant(zone.of("UTC"));

            LocalDateTime startUTCDateTime = LocalDateTime.ofInstant(startUTC.toInstant(), zone.of("UTC"));
            LocalDateTime endUTCDateTime = LocalDateTime.ofInstant(endUTC.toInstant(), zone.of("UTC"));

            Boolean withinBusinessHours = withinBusinessHours(localStart, localEnd);
            Boolean noAppointmentOverlap = noAppointmentOverlap(appointmentID, customerID, localStart, localEnd);

            if(withinBusinessHours == true && noAppointmentOverlap == true) {
                String contactName = (String) Contact.getSelectionModel().getSelectedItem();
                selectedAppointment.setContactName(contactName);
                selectedAppointment.setContact(appointmentsController.NameToContactMap.get(contactName));
                selectedAppointment.setContactID(appointmentsController.NameToContactMap.get(contactName).getContactId());

                selectedAppointment.setAppointmentTitle(this.Title.getText());
                selectedAppointment.setAppointmentDescription(this.Description.getText());
                selectedAppointment.setAppointmentLocation(this.Location.getText());
                selectedAppointment.setAppointmentType(this.Type.getText());
                selectedAppointment.setCustomerID(customerID);
                selectedAppointment.setUserID(Integer.parseInt(this.UserID.getText()));

                selectedAppointment.setStartDate(startDate);
                selectedAppointment.setEndDate(endDate);
                selectedAppointment.setStartTime(startTime);
                selectedAppointment.setEndTime(endTime);
                selectedAppointment.setStartDateAndTime(localStart);
                selectedAppointment.setEndDateAndTime(localEnd);

                updateAppointment(selectedAppointment, startUTCDateTime, endUTCDateTime);
                updateAppointmentList();
                Stage stage = (Stage) updateButton.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void updateAppointmentList() {
        try{
            for(int i=0; i<mainController.getAppointmentsList().size(); i++) {
                if(mainController.getAppointmentsList().get(i).getAppointmentID()==selectedAppointment.getAppointmentID()) {
                    mainController.getAppointmentsList().set(i, selectedAppointment);
                }
            }
        } catch (Exception e) { System.out.println(e); }
    }

    @FXML
    private void Cancel()  {
        try {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void GetSelectedAppointment(Appointment appointment) {
        selectedAppointment = appointment;
    }

    public void updateAppointment(Appointment appointment, LocalDateTime startUTC, LocalDateTime endUTC) throws SQLException {

        String updateSQLQuery = "UPDATE APPOINTMENTS SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ?, Start = ?, End = ? WHERE Appointment_ID = ?";

        PreparedStatement ps = connection.prepareStatement(updateSQLQuery);
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
        ps.setInt(11, appointment.getAppointmentID());
        ps.execute();
    }
}
