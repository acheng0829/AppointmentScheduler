package controller;

import helper.JDBC;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Main;
import model.Appointment;
import model.Customer;

import java.io.IOException;

/**
 * This class is the controller responsible for the Main Window(mainwindow.fxml)
 */
public class mainController {
    public static ObservableList<Customer> allCustomersList;
    public static ObservableList<Appointment> allAppointmentsList;
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomersList;
    }
    public static ObservableList<Appointment> getAppointmentsList() {
        return allAppointmentsList;
    }

    /**
     * The initialize method initializes a list of all customers as Customer objects from the database.
     * It also attempts to create a list of all appointments as Appointment objects from the database.
     */
    @FXML
    private void initialize() {
        try {
            allCustomersList = JDBC.getAllCustomers();
            allAppointmentsList = JDBC.getAllAppointments();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * @exception IOException if the customers.fxml file cannot be loaded
     */
    @FXML
    private void CustomersButtonAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/customers.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        Stage stage = new Stage();
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @exception IOException if the appointments.fxml file cannot be loaded
     */
    @FXML
    private void AppointmentsButtonAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/appointments.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = new Stage();
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @exception IOException if the reports.fxml file cannot be loaded
     */
    @FXML
    private void ReportsButtonAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/reports.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        Stage stage = new Stage();
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}
