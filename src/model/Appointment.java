package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class creates an Appointment object for each entry in the APPOINTMENTS table.
 */
public class Appointment {
    private int appointmentID;
    private String appointmentTitle;
    private String appointmentDescription;
    private String appointmentLocation;
    private String appointmentType;
    private LocalDateTime startDateAndTime;
    private LocalDateTime endDateAndTime;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public int customerID;
    public int userID;
    public int contactID;
    public String contactName;
    public Contact contact;

    /**
     * @param appointmentID
     * @param appointmentTitle
     * @param appointmentDescription
     * @param appointmentLocation
     * @param appointmentType
     * @param startDateAndTime
     * @param endDateAndTime
     * @param customerID
     * @param userID
     * @param contactID
     */
    public Appointment(int appointmentID, String appointmentTitle, String appointmentDescription,
                        String appointmentLocation, String appointmentType, LocalDateTime startDateAndTime, LocalDateTime endDateAndTime, int customerID,
                        int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.appointmentLocation = appointmentLocation;
        this.contactID = contactID;
        this.appointmentType = appointmentType;
        this.customerID = customerID;
        this.userID = userID;
        this.startDateAndTime = startDateAndTime;
        this.endDateAndTime = endDateAndTime;
        this.startDate = startDateAndTime.toLocalDate();
        this.endDate = endDateAndTime.toLocalDate();
        this.startTime = startDateAndTime.toLocalTime();
        this.endTime = endDateAndTime.toLocalTime();
    }

    /**
     * @return appointmentID
     */
    public int getAppointmentID() { return appointmentID; }

    /**
     * @return appointmentTitle
     */
    public String getAppointmentTitle() { return appointmentTitle; }

    /**
     * @return appointmentDescription
     */
    public String getAppointmentDescription() { return appointmentDescription; }

    /**
     * @return appointmentLocation
     */
    public String getAppointmentLocation() { return appointmentLocation; }

    /**
     * @return appointmentType
     */
    public String getAppointmentType() { return appointmentType; }

    /**
     * Start date and time format is YYYY-MM-DD HH:MM
     * @return startDateAndTime
     */
    public LocalDateTime getStartDateAndTime() { return startDateAndTime; }

    /**
     * End date and time format is YYYY-MM-DD HH:MM
     * @return endDateAndTime
     */
    public LocalDateTime getEndDateAndTime() { return endDateAndTime; }

    /**
     * Start date format is YYYY-MM-DD
     * @return startDate
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * End date format is YYYY-MM-DD
     * @return endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Start time format is HH:MM
     * @return startTime
     */
    public LocalTime getStartTime() { return startTime; }

    /**
     * End time format is HH:MM
     * @return endTime
     */
    public LocalTime getEndTime() { return endTime; }

    /**
     * @return customerID
     */
    public int getCustomerID () { return customerID; }

    /**
     * @return userID
     */
    public int getUserID() { return userID; }

    /**
     * @return contactID
     */
    public int getContactID() { return contactID; }

    /**
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param appointmentTitle
     * Set Appointment Title
     */
    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle = appointmentTitle;
    }

    /**
     * @param appointmentDescription
     * Set Appointment Description
     */
    public void setAppointmentDescription(String appointmentDescription) { this.appointmentDescription = appointmentDescription; }

    /**
     * @param appointmentLocation
     * Set Appointment Location
     */
    public void setAppointmentLocation(String appointmentLocation) { this.appointmentLocation = appointmentLocation; }

    /**
     * @param appointmentType
     * Set Appointment Type
     */
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }

    /**
     * @param startDateTime
     * Set Start Date And Time
     */
    public void setStartDateAndTime(LocalDateTime startDateTime) { this.startDateAndTime = startDateTime; }

    /**
     * @param endDateTime
     * Set End Date And Time
     */
    public void setEndDateAndTime(LocalDateTime endDateTime) { this.endDateAndTime = endDateTime; }

    /**
     * @param startDate
     * Set Start Date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @param endDate
     * Set End Date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @param startTime
     * Set Start Time
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @param endTime
     * Set End Time
     */
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    /**
     * @param customerID
     * Set Customer ID
     */
    public void setCustomerID (int customerID) {
        this.customerID = customerID;
    }

    /**
     * @param userID
     * Set User ID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @param contactID
     * Set Contact ID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * @param contact
     * Set Contact Object for current Appointment
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @param contactName
     * Set Contact Name for current Appointment
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
