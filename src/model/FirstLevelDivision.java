package model;

/**
 * This class creates a FirstLevelDivision object for each entry in the FIRST-LEVEL DIVISIONS table.
 */
public class FirstLevelDivision {
    private int divisionID;
    private String divisionName;
    public int country_ID;
    private Country country;

    /**
     * @param divisionID
     * @param divisionName
     * @param country_ID
     */
    public FirstLevelDivision(int divisionID, String divisionName, int country_ID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country_ID = country_ID;
    }

    /**
     * @return divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @return divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * @return country_ID
     */
    public int getCountry_ID() {
        return country_ID;
    }

    /**
     * @return country
     */
    public Country getCountry() {
        return this.country;
    }

    /**
     * Set country that this division belongs to
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Set the name of this division
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }
}