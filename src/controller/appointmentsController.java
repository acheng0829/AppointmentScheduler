package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Main;
import model.Appointment;
import model.Contact;
import model.Customer;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static controller.mainController.getAppointmentsList;
import static helper.JDBC.connection;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * This class is the controller responsible for the Customers Window(customers.fxml)
 */
public class appointmentsController {
    public TableView<Appointment> AppointmentsTable;

    public TableColumn AppointmentID;
    public TableColumn Title;
    public TableColumn Description;
    public TableColumn Location;
    public TableColumn Contact;
    public TableColumn Type;

    public TableColumn StartDate;
    public TableColumn EndDate;
    public TableColumn StartTime;
    public TableColumn EndTime;

    public TableColumn CustomerID;
    public TableColumn UserID;

    @FXML private RadioButton viewByWeek;
    @FXML private RadioButton viewByMonth;
    @FXML private ToggleGroup viewToggleGroup;
    @FXML private Button CloseButton;

    public static ObservableList<Contact> contactsList;
    public static ObservableList<String> contactNames;
    public static ObservableList<Contact> getContactsList() {
        return contactsList;
    }
    public static ObservableList<String> getContactNames() { return  contactNames; }

    public HashMap<Integer, Contact> IDToContactMap;
    public static HashMap<String, Contact> NameToContactMap;
    public static HashMap<Integer, Customer> IDToCustomerMap;

    /**
     * Lambda Expression included: For each Contact object, add the name of the Contact to local Observablelist 'contactNames.'  This list is used to display all the Contact Names in a ComboBox when adding or updating an appointment.
     * Initializes lists, hashmaps and tables needed to display appointments
     */
    @FXML
    private void initialize()  {
        try {
            contactsList = JDBC.getAllContacts();
            contactNames = FXCollections.observableArrayList();

            //Lambda expression
            getContactsList().forEach(Contact -> contactNames.add(Contact.getContactName()));
            InitializeContactsMaps();
            setAllContacts();
            mapAppointmentToCustomer();
            setTableColumns();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * set's table columns for the table "Appointments Table," called from initialize() method
     */
    private void setTableColumns() {
        try {
            AppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            Title.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
            Description.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
            Location.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
            Contact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            Type.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
            StartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            EndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            StartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            EndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            UserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
            AppointmentsTable.setItems(getAppointmentsList());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Uses a hashmap to map an int Customer ID to the Customer object
     */
    private void mapAppointmentToCustomer() {
        IDToCustomerMap = new HashMap<>();
         try {
             for (Customer customer : mainController.getAllCustomers()) {
                 IDToCustomerMap.put(customer.getCustomerID(), customer);
             }
         } catch (Exception e) {
             System.out.println(e);
         }
         addAppointmentToCustomer();
    }

    /**
     * Uses the IDToCustomerMap hashmap to add each appointment to it's respective Customer object
     */
    private void addAppointmentToCustomer() {
        try {
            for (Appointment appointment : getAppointmentsList()) {
                Customer customer = IDToCustomerMap.get(appointment.getCustomerID());
                customer.addToAppointmentsList(appointment);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Initializes the Contact ID -> Contact map
     * Initializes the Contact Name -> Contact map
     * This is used to set the Contact and Contact Name in each Appointment object.
     */
    private void InitializeContactsMaps() {
        IDToContactMap = new HashMap<>();
        NameToContactMap = new HashMap<>();
        for(Contact contact : getContactsList()) {
            IDToContactMap.put(contact.getContactId(), contact);
            NameToContactMap.put(contact.getContactName(), contact);
        }
    }

    /**
     * The hashmap IDToContactMap is used to set the Contact object and Contact Name for each Appointment object.
     */
    private void setAllContacts() {
        for(Appointment appointment : getAppointmentsList()) {
            int contactID = appointment.getContactID();
            appointment.setContact(IDToContactMap.get(contactID));
            appointment.setContactName(IDToContactMap.get(contactID).getContactName());
        }
    }

    /**
     * Attempts to open the Add Appointment Window(addappointment.fxml)
     */
    @FXML
    private void Add() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/addappointment.fxml"));
            Parent addAppointment = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(addAppointment));
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Window");
            alert.setContentText("The Add Appointment Window could not be opened.");
            alert.showAndWait();
        }
    }

    /**
     * Attempts to open the Update Appointment Window(updateappointment.fxml)
     */
    @FXML
    private void Update() {
        try {
            Appointment appointment = AppointmentsTable.getSelectionModel().getSelectedItem();
            updateappointmentController.GetSelectedAppointment(appointment);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/updateappointment.fxml"));
            Parent updateAppointment = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(updateAppointment));
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Update Appointment Window");
            alert.setContentText("An Appointment must be selected in order to Modify it");
            alert.showAndWait();
        }
    }

    /**
     * Gets selected Appointment and opens a popup box asking the user if they want the selected Appointment to be deleted.
     */
    @FXML
    public void Delete() {
        try {
            Appointment appointment = AppointmentsTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete Appointment with ID: " + appointment.getAppointmentID() +
                    "? Appointment Type: " + appointment.getAppointmentType());
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                deleteAppointment(appointment);
            }
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not Delete Customer");
            alert.setContentText("A Customer is not selected.");
            alert.showAndWait();
        }
    }

    /**
     * @param appointment the selected appointment from Delete() is based in
     * Attempts to delete the selected appointment from the appointment list, database, finally updates the AppointmentsTable.
     */
    public void deleteAppointment(Appointment appointment) {
        try {
            deleteFromList(appointment);
            deleteFromDatabase(appointment);
            AppointmentsTable.setItems(mainController.getAppointmentsList());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * @param appointment the selected appointment from deleteAppointment(Appointment) is based in.
     * Attempts to delete the selected appointment from the database.
     */
    public void deleteFromDatabase(Appointment appointment) {
        try {
            String deleteQuery = "DELETE FROM APPOINTMENTS WHERE Appointment_ID=?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setInt(1, appointment.getAppointmentID());
            ps.execute();
            ps.close();
        } catch (Exception e) { System.out.println(e); }
    }

    /**
     * @param appointment the selected appointment from deleteAppointment(Appointment) is based in.
     * Attempts to delete the selected appointment from the Appointment list.
     */
    public void deleteFromList(Appointment appointment) {
        try {
            for(int i=0; i<mainController.getAppointmentsList().size(); i++) {
                if(mainController.getAppointmentsList().get(i).getAppointmentID()==appointment.getAppointmentID()) {
                    mainController.getAppointmentsList().remove(i);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Shows Appointments occurring within a week when this radio button is selected.
     */
    @FXML
    private void weekViewSelected() {
        try {
            ObservableList<Appointment> appointmentsThisWeek = FXCollections.observableArrayList();

            LocalDateTime start = LocalDateTime.now().minusWeeks(1);
            LocalDateTime end = LocalDateTime.now().plusWeeks(1);

            if (getAppointmentsList() != null)
                getAppointmentsList().forEach(appointment -> {
                    if (appointment.getEndDateAndTime().isAfter(start) && appointment.getEndDateAndTime().isBefore(end)) {
                        appointmentsThisWeek.add(appointment);
                    }
                    AppointmentsTable.setItems(appointmentsThisWeek);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows Appointments occurring this month when this radio button is selected.
     */
    @FXML
    private void monthViewSelected() {
        try {
            ObservableList<Appointment> appointmentsThisMonth = FXCollections.observableArrayList();
            LocalDate start = LocalDate.now().with(firstDayOfMonth());
            LocalDate end = LocalDate.now().with(lastDayOfMonth());

            if (getAppointmentsList() != null)
                getAppointmentsList().forEach(appointment -> {
                    if ((appointment.getStartDate().isAfter(start) && appointment.getStartDate().isBefore(end)) ||
                            appointment.getStartDate().isEqual(start) || appointment.getStartDate().isEqual(end)) {
                        appointmentsThisMonth.add(appointment);
                    }
                    AppointmentsTable.setItems(appointmentsThisMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the Appointments window.
     */
    @FXML
    private void CloseButtonAction() {
        try {
            Stage stage = (Stage) CloseButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
