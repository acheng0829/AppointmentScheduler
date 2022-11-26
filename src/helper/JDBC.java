package helper;

import controller.customersController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This is the JDBC class in charge of connecting to a database
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    //private static final String jdbcUrl = "jdbc:mysql://localhost/client_schedule?connectionTimeZone = SERVER";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; //  sqlUser
    private static String password = "Passw0rd!"; // Passw0rd!
    public static Connection connection;  // Connection Interface

    /**
     * Attempts to open a connection with a database.
     * @return true if a connection was established and false otherwise.
     */
    public static Boolean openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
            return true;
        }
        catch(Exception e) { System.out.println("Error:" + e.getMessage()); }
        return false;
    }

    /**
     * Closes an opened connection with a database.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)  { System.out.println("Error:" + e.getMessage()); }
    }

    /**
     * @return int if there is a username and password match in the database, the user ID is returned
     * if there is no match, a negative number is returned and the stack trace is printed
     */
    public static int getUserID(String username, String password)
    {
        try {
            String sqlQuery = "SELECT User_ID, User_Name, Password from USERS";
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if ((rs.getString("User_Name").equals(username)) && (rs.getString("Password").equals(password))) {
                    return rs.getInt("User_ID");
                }
            }
        }
        catch(SQLException e)
                { e.printStackTrace(); }
                return -1;
            }


    /**
     * Creates an Appointment object for each appointment in the APPOINTMENTS table.
     * @return the list of appointments able to be read from the database.
     */
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointmentsObservableList = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * from APPOINTMENTS";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int appointmentID = rs.getInt("Appointment_ID");
                String appointmentTitle = rs.getString("Title");
                String appointmentDescription = rs.getString("Description");
                String appointmentLocation = rs.getString("Location");
                String appointmentType = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();

                //start and end are currently in UTC, convert start and end to user's zone
                ZonedDateTime startUTC = start.atZone(ZoneId.of("UTC"));
                ZonedDateTime endUTC = end.atZone(ZoneId.of("UTC"));

                ZoneId zone = ZoneId.systemDefault(); //get user's zone
                ZonedDateTime startUserZone = startUTC.withZoneSameInstant(zone);
                ZonedDateTime endUserZone = endUTC.withZoneSameInstant(zone);

                LocalDateTime localStart = LocalDateTime.ofInstant(startUserZone.toInstant(), zone);
                LocalDateTime localEnd = LocalDateTime.ofInstant(endUserZone.toInstant(), zone);

                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                Appointment appointment = new Appointment(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, localStart, localEnd, customerID, userID, contactID);
                appointmentsObservableList.add(appointment);
            }
        } catch (SQLException s) { System.out.println(s); }
        return appointmentsObservableList;
    }

    /**
     * Creates a Customer object for each customer in the CUSTOMERS table.
     * @return the list of customers able to be read from the database.
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customersObservableList = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * from CUSTOMERS";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");

                Customer customer = new Customer(customerID, customerName, address, postalCode, phone, divisionID);
                customersObservableList.add(customer);
            }
        } catch (SQLException s) { System.out.println(s); }
        return customersObservableList;
    }

    /**
     * Creates a Country object for each country in the COUNTRIES table.
     * @return the list of countries able to be read from the database.
     */
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countriesList = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Country_ID, Country from COUNTRIES";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int countryID = rs.getInt("Country_ID");
                String countryName = rs.getString("Country");
                Country country = new Country(countryID, countryName);
                countriesList.add(country);
            }
        } catch (SQLException s) { System.out.println(s); }
        return countriesList;
    }

    /**
     * Creates a FirstLevelDivision object for each division in the FIRST_LEVEL_DIVISIONS table.
     * @return the list of first level divisions able to be read from the database.
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions() throws SQLException {
        ObservableList<FirstLevelDivision> divisionsList = FXCollections.observableArrayList();
        String sql = "SELECT Division_ID, Division, Country_ID from FIRST_LEVEL_DIVISIONS";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");
            FirstLevelDivision division = new FirstLevelDivision(divisionID, divisionName, countryID);

            addDivisionNameToCountry(countryID, divisionName);
            divisionsList.add(division);
        }
        return divisionsList;
    }

    /**
     * Gets Country associated with this division's countryID, then adds the division name to the Country's list of divisions
     */
    private static void addDivisionNameToCountry(int countryID, String divisionName) {
        Country country = customersController.getAllCountries().get(countryID-1);
        country.divisionsInCountry.add(divisionName);
    }

    /**
     * Creates a Contact object for each contact in the CONTACTS table.
     * @return the list of contacts able to be read from the database.
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contactsList = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Contact_ID, Contact_Name from CONTACTS";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int contactID = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                Contact contact = new Contact(contactID, contactName);
                contactsList.add(contact);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return contactsList;
    }

    /*
    public static void query(String query, String column) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            String value = rs.getString(column);
            System.out.println(value);
        }
    }
     */
}
