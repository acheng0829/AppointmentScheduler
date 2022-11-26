package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;

import static helper.JDBC.connection;

/**
 * This controller is responsible for the Add Customer Window(addcustomer.fxml)
 */
public class addcustomerController {

    @FXML private TextField CustomerID;
    @FXML private TextField CustomerName;
    @FXML private TextField Address;
    @FXML private TextField PostalCode;
    @FXML private TextField PhoneNumber;

    @FXML private Button addButton;
    @FXML private Button cancelButton;

    @FXML private ComboBox FirstLevelDivision;
    @FXML private ComboBox Country;

    public static HashMap<String, FirstLevelDivision> mapStringToDivision;

    /**
     * Lambda Expression included to go through each Country object and add each Countries Name to local ObservableList 'countryNames.'  This list is used to display the list of Countries in the ComboBox.
     * Initializes the Country and Divisions Combobox, the Customer ID is auto-generated and disabled for modification.
     */
    @FXML
    private void initialize() {
        try {
            CustomerID.setText("Auto Gen-Disabled");
            ObservableList<String> countryNames = FXCollections.observableArrayList();

            //Lambda Expression
            customersController.getAllCountries().forEach(Country -> countryNames.add(Country.getCountryName()));
            Country initialCountry = customersController.getAllCountries().get(0);

            Country.setItems(countryNames);
            Country.setValue(initialCountry.getCountryName());
            setDivisions(initialCountry.getCountryName());
            FirstLevelDivision.setValue(initialCountry.divisionsInCountry.get(0));

            mapStringToDivision = new HashMap<>();
            mapStringToDivision = stringToDivision(mapStringToDivision);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * When this Combobox is used, the Country name selected will change the FirstLevelDivision Combobox to list out all the Divisions within the selected Country.
     */
    public void CountryComboBox() {
        try {
            String countryName = (String) Country.getSelectionModel().getSelectedItem();
            for(Country country : customersController.getAllCountries()) {
                if(country.getCountryName().equals(countryName)) {
                    FirstLevelDivision.setItems(country.divisionsInCountry);
                    FirstLevelDivision.setValue(country.divisionsInCountry.get(0));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Find the Country object associated with this name, fills the FirstLevelDivision Combobox with divisions associated with this Country.
     * @param countryName the name of the Country selected from the Country Combobox is passed in.
     */
    private void setDivisions(String countryName) {
        try {
            for(Country country : customersController.getAllCountries()) {
                if(country.getCountryName().equals(countryName)) {
                    FirstLevelDivision.setItems(country.divisionsInCountry);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Find the Country object associated with this name, fills the FirstLevelDivision ComboBox with divisions associated with this Country.
     */
    @FXML
    private void Add() {
        try {
            int customerID = generateID();
            String customerName = this.CustomerName.getText();
            String address = this.Address.getText();
            String postalCode = this.PostalCode.getText();
            String phoneNumber = this.PhoneNumber.getText();
            String divisionName = (String) FirstLevelDivision.getSelectionModel().getSelectedItem();
            int divisionID = mapStringToDivision.get(divisionName).getDivisionID();

            Customer customer = new Customer(customerID, customerName, address, postalCode, phoneNumber, divisionID);
            //add division
            FirstLevelDivision division = mapStringToDivision.get(divisionName);
            customer.setDivision(division);
            customer.setDivisionName(division);
            customer.setCountry(division.getCountry());

            mainController.getAllCustomers().add(customer); //add to Customer List
            addCustomer(customer); //add to Database
            closeWindow(addButton);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * @param customer The Customer object that was just created is passed in.
     * Attempts to add data for this new customer into the database.
     */
    private void addCustomer(Customer customer) {
        try {
            String query = "INSERT INTO CUSTOMERS (Customer_ID, Customer_Name, Address, Postal_Code, Phone, " +
                    "Division_ID) VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, customer.getCustomerID());
            ps.setString(2, customer.getCustomerName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPostalCode());
            ps.setString(5, customer.getPhoneNumber());
            ps.setInt(6, customer.getDivisionID());
            ps.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Pressing the Cancel button calls the close window method.
     */
    @FXML
    private void Cancel() { closeWindow(cancelButton); }


    /**
     * Starting from 1, for each iteration, keep adding 1 until an ID in the Customer list has not been taken is found.
     * @return the lowest positive integer that has not been taken as the new Customer ID.
     */
    private int generateID() {
        try {
            HashSet<Integer> setOfTakenIDs = new HashSet<>();
            for(Customer customer : mainController.getAllCustomers()) {
                setOfTakenIDs.add(customer.getCustomerID());
            }

            int ID = 1;
            while (true) {
                if(!setOfTakenIDs.contains(ID)) { return ID; }
                else { ID+=1; }
            }
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    /**
     * @param map Hashmap that maps division name to division object.
     * @return The updated division name to division object hashmap.
     * For every FirstLevelDivision, the division name is mapped to the division object.
     */
    private HashMap<String, FirstLevelDivision> stringToDivision(HashMap<String, FirstLevelDivision> map) {
        try {
            for(FirstLevelDivision division : customersController.getAllDivisions()) {
                map.put(division.getDivisionName(), division);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return map;
    }

    /**
     * Method responsible to close the window when a button that should end the window is passed in.
     * @param button a button that should initiates the exiting of this window is passed in.
     */
    private void closeWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    /**
     * Individual FirstLevelDivisions can be highlighted and selected.
     */
    public void DivisionComboBox() {
    }
}
