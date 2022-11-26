package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

/**
 * This class is the controller responsible for the Reports Window(reports.fxml)
 */
public class reportsController {
    @FXML private ComboBox ContactComboBox;
    @FXML private TableView<Appointment> ContactAppointmentsTable;
    @FXML private ComboBox TypeComboBox;
    @FXML private ComboBox MonthComboBox;

    @FXML private Label TypesLabel;
    @FXML private Label MonthsLabel;
    @FXML private Label userID;
    @FXML private Label upcomingAppointmentDate;
    @FXML private Label upcomingAppointmentTime;
    @FXML private Button CloseButton;

    public TableColumn AppointmentID;
    public TableColumn Title;
    public TableColumn Description;
    public TableColumn Type;
    public TableColumn StartDateAndTime;
    public TableColumn EndDateAndTime;
    public TableColumn CustomerID;

    private HashMap<String, Integer> typesCountMap;
    private HashMap<String, Integer> monthsCountMap;
    private ObservableList<Appointment> appointmentsList;
    private ObservableList<Contact> contactsList;

    /**
     * Initializes the table that displays each contact's appointments, the ComboBox for each Contact,
     * the ComboBoxes for total number of appointments by type and month, and the next upcoming appointment is for the logged in user.
     */
    @FXML
    private void initialize() {
        try {
            appointmentsList = JDBC.getAllAppointments();
            contactsList = JDBC.getAllContacts();
            typesCountMap = new HashMap<>();
            monthsCountMap = new HashMap<>();

            setInitialComboBoxes();

            findNextAppointment(appointmentsList); //Personal Report
            setContactAppointmentsTable();
            TypeComboBoxAction();
            MonthComboBoxAction();
            ContactComboBoxAction();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Fills the initial comboboxes with respective items and initial value.
     */
    private void setInitialComboBoxes() {
        try {
            ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
            ObservableList<String> appointmentMonths = initializeMonthsMap();
            ObservableList<String> contactNames = addContactNamesToList();

            initializeTypesMap(appointmentsList);
            getAllAppointmentTypes(appointmentTypes);
            countMonths(appointmentsList);

            TypeComboBox.setValue(appointmentTypes.get(0));
            TypeComboBox.setItems(appointmentTypes);
            MonthComboBox.setValue(appointmentMonths.get(0));
            MonthComboBox.setItems(appointmentMonths);
            ContactComboBox.setValue(contactNames.get(0));
            ContactComboBox.setItems(contactNames);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Adds Contact Names to the contactNames list.
     * @return the list of all contact names in String format.
     */
    private ObservableList<String> addContactNamesToList() {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        try {
            for(Contact contact : contactsList) { contactNames.add(contact.getContactName()); }
        } catch (Exception e) {
            System.out.println(e);
        }
        return contactNames;
    }

    /**
     * Sets the initial table with the proper values.
     */
    private void setContactAppointmentsTable() {
        AppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        Title.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        Description.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        Type.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        StartDateAndTime.setCellValueFactory(new PropertyValueFactory<>("startDateAndTime"));
        EndDateAndTime.setCellValueFactory(new PropertyValueFactory<>("endDateAndTime"));
        CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * @param userId the ID of the current user.
     * @return a List of all appointments for the current user logged in.
     */
    private List<Appointment> getUserAppointments(int userId) {
        List<Appointment> userAppointments = new ArrayList<>();
        //for each appointment, add to userAppointments list if userID matches
        for(Appointment appointment : appointmentsList) {
            if(appointment.getUserID() == userId) {
                userAppointments.add(appointment);
            }
        }
        return userAppointments;
    }

    /**
     * My personal report to find the next upcoming appointment for the user logged in.
     * @param appointmentsList List of all appointments in the database.
     */
    private void findNextAppointment(ObservableList<Appointment> appointmentsList) {
        int userId = loginController.getUserID();
        userID.setText(String.valueOf(userId));

        List<Appointment> userAppointments = getUserAppointments(userId);

        //sort appointment start date and time
        //Lambda Expression to sort userAppointments list by the appointments start date and time.
        Comparator<Appointment> compareByDateTime = (Appointment a, Appointment b) -> a.getStartDateAndTime().compareTo(b.getStartDateAndTime());
        Collections.sort(userAppointments, compareByDateTime);

        //find first appointment that's after the current time
        for(Appointment appointment : userAppointments) {
            LocalDateTime currentTime = LocalDateTime.now();

            if(appointment.getStartDateAndTime().isAfter(currentTime)) {
                //System.out.println("Appointment: " + appointment.getAppointmentID() + " is after the current time: " + currentTime);
                String startDate = appointment.getStartDate().toString();
                String startTime = appointment.getStartTime().toString();
                upcomingAppointmentDate.setText(startDate);
                upcomingAppointmentTime.setText(startTime);
                break; //finds the first appointments after the current date, no need for further appointment checking.
            }
        }
    }

    /**
     * Counts how many appointments are in each month and puts the result in the hashmap monthsCountMap.
     * @param appointmentsList List of all appointments in the database.
     */
    private void countMonths(ObservableList<Appointment> appointmentsList) {
        try {
            for(Appointment appointment : appointmentsList) {
                Month month = appointment.getStartDate().getMonth();
                String monthString = month.toString();
                if(monthsCountMap.containsKey(monthString)) {
                    monthsCountMap.put(monthString, monthsCountMap.get(monthString) + 1);
                }
                else { monthsCountMap.put(monthString, 1); }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Counts how many appointments there are for each type and puts each amount in the map typesCountMap.
     * @param appointmentsList List of all appointments in the database.
     */
    private void initializeTypesMap(ObservableList<Appointment> appointmentsList) {
        try {
            for(Appointment appointment : appointmentsList) {
                String type = appointment.getAppointmentType();
                if(typesCountMap.containsKey(type)) {
                    typesCountMap.put(type, typesCountMap.get(type) + 1);
                }
                else { typesCountMap.put(type, 1); }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Gets all Appointment Type names and populates the list 'types.'
     * @param types list of all appointment types(initially empty).
     */
    private void getAllAppointmentTypes(ObservableList<String> types) {
        for (String key: typesCountMap.keySet()){ types.add(key); }
    }

    /**
     * Gets the selected Contact, then adds all appointments that have this contact to the appointmentsForContact list.
     * The appointmentsForContact list is then displayed in the ContactAppointmentsTable.
     */
    @FXML
    private void ContactComboBoxAction() {
        ObservableList<Appointment> appointmentsForContact = FXCollections.observableArrayList();
        String contactName = (String) ContactComboBox.getSelectionModel().getSelectedItem();
        int contactID = -1;
        for(Contact contact : contactsList) {
            if(contact.getContactName().equals(contactName)) { contactID = contact.getContactId(); }
        }

        for(Appointment appointment : appointmentsList) {
            if(appointment.getContactID() == contactID) { appointmentsForContact.add(appointment); }
        }

        ContactAppointmentsTable.setItems(appointmentsForContact);
    }

    /**
     * Gets the Type selected from the Combo Box.
     * The number of Appointment of this Type is displayed in TypesLabel.
     */
    @FXML
    private void TypeComboBoxAction() {
        String typeSelection = (String) TypeComboBox.getSelectionModel().getSelectedItem();
        TypesLabel.setText(String.valueOf(typesCountMap.get(typeSelection)));
    }

    /**
     * Gets the Month selected from the Combo Box.
     * The number of Appointments in this Month is displayed in MonthsLabel.
     */
    @FXML
    private void MonthComboBoxAction() {
        String monthSelection = (String) MonthComboBox.getSelectionModel().getSelectedItem();
        MonthsLabel.setText(String.valueOf(monthsCountMap.get(monthSelection)));
    }


    /**
     * Initializes a list of with all 12 months, also puts each month as a key in the hashmap 'monthsCountMap.'
     * @return the list of 12 Months.
     */
    private ObservableList<String> initializeMonthsMap() {
        ObservableList<String> monthsList = FXCollections.observableArrayList();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i<months.length-1; i++) {
            String month = months[i].toUpperCase();
            monthsList.add(month);
            monthsCountMap.put(month, 0);
        }
        return monthsList;
    }

    /**
     * Closes the report window.
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
