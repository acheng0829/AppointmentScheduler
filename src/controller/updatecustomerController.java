package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import static helper.JDBC.connection;


public class updatecustomerController {
    @FXML private TextField CustomerID;
    @FXML private TextField CustomerName;
    @FXML private TextField Address;
    @FXML private TextField PostalCode;
    @FXML private TextField PhoneNumber;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox FirstLevelDivision;
    @FXML private ComboBox Country;

    private static Customer selectedCustomer;

    public static HashMap<String, FirstLevelDivision> mapStringToDivision;

    @FXML
    private void initialize() {
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            mapStringToDivision = new HashMap<>();
            mapStringToDivision = stringToDivision(mapStringToDivision);

            CustomerID.setText(Integer.toString(selectedCustomer.getCustomerID()));
            CustomerName.setText(selectedCustomer.getCustomerName());
            Address.setText(selectedCustomer.getAddress());
            PostalCode.setText(selectedCustomer.getPostalCode());
            PhoneNumber.setText(selectedCustomer.getPhoneNumber());
            FirstLevelDivision.setValue(selectedCustomer.getDivision().getDivisionName());
            Country.setValue(selectedCustomer.getCountry().getCountryName());

            //Lambda
            //customersController.getAllDivisions().forEach(firstLevelDivision -> divisionNames.add(firstLevelDivision.getDivisionName()));
            customersController.getAllCountries().forEach(Country -> countryNames.add(Country.getCountryName()));
            Country.setItems(countryNames);
            setInitialDivisions();
    }

    private HashMap<String, FirstLevelDivision> stringToDivision(HashMap<String, FirstLevelDivision> map) {
        try {
            for(FirstLevelDivision division : customersController.getAllDivisions()) {
                map.put(division.getDivisionName(), division);
            }
        } catch (Exception e) { System.out.println(e); }
        return map;
    }

    private void setInitialDivisions() {
        try {
            for(Country country : customersController.getAllCountries()) {
                if(country.getCountryName().equals(selectedCustomer.getCountry().getCountryName())) {
                    FirstLevelDivision.setItems(country.divisionsInCountry);
                }
            }
        } catch (Exception e) { System.out.println(e); }
    }

    public void CountryComboBox() {
        try {
            String countryName = (String) Country.getSelectionModel().getSelectedItem();
            for(Country country : customersController.getAllCountries()) {
                if(country.getCountryName().equals(countryName)) {
                    FirstLevelDivision.setItems(country.divisionsInCountry);
                    FirstLevelDivision.setValue(country.divisionsInCountry.get(0));
                }
            }
        } catch (Exception e) { System.out.println(e); }
    }

    public static void GetSelectedCustomer(Customer customer) {
        selectedCustomer = customer;
    }

    @FXML
    private void Update() throws SQLException {
        try {
            selectedCustomer.setCustomerName(this.CustomerName.getText());
            selectedCustomer.setAddress(this.Address.getText());
            selectedCustomer.setPostalCode(this.PostalCode.getText());
            selectedCustomer.setPhoneNumber(this.PhoneNumber.getText());

            String divisionName = (String) this.FirstLevelDivision.getValue();
            FirstLevelDivision division = mapStringToDivision.get(divisionName);
            selectedCustomer.setDivision(division);
            selectedCustomer.setCountry(division.getCountry());
            selectedCustomer.setDivisionID(division.getDivisionID());

            updateCustomerInDataBase(selectedCustomer);
            updateCustomerList();
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) { System.out.println(e); }
    }

    private void updateCustomerList() {
        try {
            for(int i=0; i<mainController.getAllCustomers().size(); i++) {
                if(mainController.getAllCustomers().get(i).getCustomerID()==selectedCustomer.getCustomerID()) {
                    mainController.getAllCustomers().set(i, selectedCustomer);
                }
            }
        } catch (Exception e) { System.out.println(e); }
    }

    public void updateCustomerInDataBase(Customer customer) {
        try {
            String updateSQLQuery = "UPDATE CUSTOMERS SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                    "Division_ID = ? WHERE Customer_ID = ?";

            PreparedStatement ps = connection.prepareStatement(updateSQLQuery);
            ps.setInt(1, customer.getCustomerID());
            ps.setString(2, customer.getCustomerName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPostalCode());
            ps.setString(5, customer.getPhoneNumber());
            ps.setInt(6, customer.getDivisionID());
            ps.setInt(7, customer.getCustomerID());
            ps.execute();

        } catch (Exception e) { System.out.println(e); }
    }

    @FXML
    private void Cancel() {
        try {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void DivisionComboBox()  {
    }
}
