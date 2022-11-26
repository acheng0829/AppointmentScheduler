package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a Customer object for each entry in the CUSTOMERS table.
 */
public class Customer {
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;

    private int divisionID;
    private String divisionName;
    private FirstLevelDivision division;
    private Country country;
    private List<Appointment> appointments;

    /**
     * @param customerID
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNumber
     * @param divisionID
     */
    public Customer(int customerID, String customerName, String address, String postalCode, String phoneNumber, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;
        appointments = new ArrayList<>();
    }

    /**
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @return customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Returns the customers appointments in a list
     * @return appointments
     */
    public List<Appointment> getAppointmentsList() {
        return appointments;
    }

    /**
     * @return country
     */
    public Country getCountry() {
        return this.country;
    }

    /**
     * @return division
     */
    public FirstLevelDivision getDivision() {
        return this.division;
    }

    /**
     * @return divisionName
     */
    public String getDivisionName() { return this.divisionName; }

    /**
     * Set customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Set address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Set postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Set phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Set divisionID
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * Set country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Set division
     */
    public void setDivision(FirstLevelDivision division) {
        this.division = division;
    }

    /**
     * Set divisionName
     */
    public void setDivisionName(FirstLevelDivision division) {
        this.divisionName = division.getDivisionName();
    }

    /**
     * Add appointment to list of appointments
     */
    public void addToAppointmentsList(Appointment appointment) {
        this.appointments.add(appointment);
    }
}
