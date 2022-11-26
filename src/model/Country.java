package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class creates a Country object for each entry in the COUNTRIES table.
 */
public class Country {
    private int countryID;
    private String countryName;

    //Each Country object keeps a list of its respective divisions
    public ObservableList<String> divisionsInCountry;

    /**
     * @param countryID
     * @param countryName
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
        this.divisionsInCountry = FXCollections.observableArrayList();
    }

    /**
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }
}