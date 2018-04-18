package cyruslee487.donteatalone.DiscountRoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Discount {

    @PrimaryKey(autoGenerate = true)
    private int eid;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "rest_name")
    private String rest_name;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "end_date")
    private String endDate;

    @ColumnInfo(name = "end_time")
    private String endTime;

    @ColumnInfo(name = "num_of_people")
    private int numOfPeople;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "key")
    private String key;

    @ColumnInfo(name = "email")
    private String email;

    public Discount(){}

    public Discount(String address, String rest_name,
                    String startDate, String startTime, String endDate, String endTime,
                    int numOfPeople, String description, String token, String key, String email) {
        this.address = address;
        this.rest_name = rest_name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.numOfPeople = numOfPeople;
        this.description = description;
        this.token = token;
        this.key = key;
        this.email = email;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
