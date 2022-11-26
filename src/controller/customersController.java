package controller;

import helper.JDBC;
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
import model.Country;
import model.Customer;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import static helper.JDBC.connection;

/**
 * This class is the controller responsible for the Customers Controller(customers.fxml)
 */
public class customersController {

    public TableView<Customer> CustomersTable;

    public TableColumn CustomerID;
    public TableColumn CustomerName;
    public TableColumn Address;
    public TableColumn PostalCode;
    public TableColumn PhoneNumber;
    public TableColumn Division;

    @FXML private Button AddCustomer;
    @FXML private Button UpdateCustomer;
    @FXML private Button DeleteCustomer;
    @FXML private Button CloseButton;

    public static ObservableList<Country> allCountries;

    public static ObservableList<Country> getAllCountries() {
        return allCountries;
    }

    public static ObservableList<FirstLevelDivision> allDivisions;

    public static ObservableList<FirstLevelDivision> getAllDivisions() {
        return allDivisions;
    }

    /**
     * The initialize method attempts to create a list of all Countries as Country objects from the database
     * It also attempts to create a list of all First Level Divisions as FirstLevelDivision objects from the database
     * @exception SQLException if there is an error of reading the data from the database
     */
    @FXML
    private void initialize() throws SQLException {
        allCountries = JDBC.getAllCountries();
        allDivisions = JDBC.getAllDivisions();
        setCountryToDivision();
        setDivisionToCustomer();

        CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        CustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        Address.setCellValueFactory(new PropertyValueFactory<>("address"));
        PostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        Division.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        CustomersTable.setItems(mainController.getAllCustomers());
    }

    /**
     * Creates a hashmap that maps the Country ID to Country object
     * Then for each FirstLevelDivision, assigns the correct Country object.
     */
    private void setCountryToDivision() {
        HashMap<Integer, Country> mapIDToCountry = new HashMap<>();

        for(Country country : allCountries) { mapIDToCountry.put(country.getCountryID(), country); }

        for(FirstLevelDivision division : allDivisions) {
            int countryID = division.getCountry_ID();
            division.setCountry(mapIDToCountry.get(countryID));
        }
    }

    /**
     * Creates a hashmap that maps the Division ID to it's Division object
     * Then for each customer, sets the Division object, the Division name, as well as the Country of the Division
     */
    private void setDivisionToCustomer() {
        HashMap<Integer, FirstLevelDivision> mapIDToDivision = new HashMap<>();

        for(FirstLevelDivision division : allDivisions) { mapIDToDivision.put(division.getDivisionID(), division); }

        for(Customer customer : mainController.getAllCustomers()) {
            int divisionID = customer.getDivisionID();
            customer.setDivision(mapIDToDivision.get(divisionID));
            customer.setDivisionName(customer.getDivision());
            customer.setCountry(customer.getDivision().getCountry());
        }
    }

    /**
     * Attempts to open the Add Customer Window(addcustomer.fxml) when the AddCustomer Button is pressed
     * If an error occurs the exception is printed and a popup is displayed.
     */
    @FXML
    private void AddCustomerAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/addcustomer.fxml"));
            Parent addCustomer = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(addCustomer));
            stage.show();

        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Add Customer Window");
            alert.setContentText("Add Customer Window could not be opened.");
            alert.showAndWait();
        }
    }

    /**
     * Attempts to open the Update Customer Window(updateCustomer.fxml) when the Update Customer Button is pressed
     * If an error occurs the exception is printed and a popup is displayed.
     */
    @FXML
    private void UpdateCustomerAction() {
        try {
            Customer customer = CustomersTable.getSelectionModel().getSelectedItem();
            updatecustomerController.GetSelectedCustomer(customer);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/updatecustomer.fxml"));
            Parent updateCustomer = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(updateCustomer));
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Update Customer Window");
            alert.setContentText("Please make sure a Customer is selected.");
            alert.showAndWait();
        }
    }

    /**
     * Attempts to delete the selected customer, a popup is first displayed to double check the user wants this action.
     * All appointments with this customer are first deleted, the customer is then deleted from the list of all customers, the customer is removed from the database, finally the Customers Table is updated.
     * If an error occurs, the stack trace is printed.
     */
    public void DeleteCustomerAction() {
        try {
            Customer customer = CustomersTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to Delete Customer: " + customer.getCustomerName() + "? Customer ID: " + customer.getCustomerID());
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                deleteAllAppointments(customer);
                deleteFromList(customer);
                deleteFromDatabase(customer);
                CustomersTable.setItems(mainController.getAllCustomers());
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
     * Deletes all appointments associated with this customer from the appointment list, and then the database
     * @param customer The customer that the user wants to delete from the database is passed into this method
     */
    private void deleteAllAppointments(Customer customer) {
        try {
            for(Appointment appointment : customer.getAppointmentsList()) {
                mainController.getAppointmentsList().remove(appointment);
                deleteAppointmentFromDatabase(appointment);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Attempts to delete the appointment from the APPOINTMENTS table in the database
     * @param appointment the appointment object is passed in after finding a matching customer id with the customer that will be deleted.
     * @throws SQLException if there is an error in deleting the appointment from the database
     */
    private void deleteAppointmentFromDatabase (Appointment appointment) throws SQLException {
        try {
            String deleteQuery = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setInt(1, appointment.getAppointmentID());
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Attempts to delete the customer from the CUSTOMERS table in the database
     * @param customer the customer object that is selected is passed in.
     * @throws SQLException if there is an error in deleting the customer from the database
     */
    private void deleteFromDatabase(Customer customer) throws SQLException {
        try {
            String deleteQuery = "DELETE FROM Customers WHERE Customer_ID=?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setInt(1, customer.getCustomerID());
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Attempts to delete the customer from the Customers list.
     * @param customer the customer object that is selected is passed in.
     */
    private void deleteFromList(Customer customer) {
        try {
            for(int i=0; i<mainController.getAllCustomers().size(); i++) {
                if(mainController.getAllCustomers().get(i).getCustomerID()==customer.getCustomerID()) {
                    mainController.getAllCustomers().remove(i);
                }
            }
        } catch (Exception e) { System.out.println(e); }
    }

    /**
     * Closes the Customer window.
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

    /*
    private void addDivisionName() {
        HashMap<Integer, FirstLevelDivision> divisionMap = new HashMap<>();
        HashMap<Integer, String> countryMap = new HashMap<>();

        for(FirstLevelDivision division : allDivisions) {
            divisionMap.put(division.getDivisionID(), division);
        }

        for(Country country : allCountries) {
            countryMap.put(country.getCountryID(), country.getCountryName());
        }

        setDivisionNameAndCountryID(divisionMap, countryMap);
        //setCountryName(mapcountryName);

        //setCountryForCustomer(mapDivision);
    }

    private void setDivisionNameAndCountryID(HashMap<Integer, FirstLevelDivision> divisionMap, HashMap<Integer, String> countryMap) {
        for(Customer customer : allCustomersList) {
            int divisionID = customer.getDivisionID();
            int countryID = divisionMap.get(divisionID).getCountry_ID();

            customer.setDivisionName(divisionMap.get(divisionID).getDivisionName());
            customer.setCountryID(countryID);
            customer.setCountryName(countryMap.get(countryID));
        }
    }
 */
}

