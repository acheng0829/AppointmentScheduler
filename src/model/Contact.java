package model;

/**
 * This class creates a Contact object for each entry in the CONTACTS table.
 */
public class Contact {
    public int contactID;
    public String contactName;

    /**
     * @param contactID
     * @param contactName
     */
    public Contact(int contactID, String contactName) {
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * @return contactID
     */
    public int getContactId() {
        return contactID;
    }

    /**
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }
}