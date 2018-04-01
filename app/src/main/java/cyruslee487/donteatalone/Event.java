package cyruslee487.donteatalone;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
            private int eid;

    @ColumnInfo(name = "user_name")
            private String username;

    @ColumnInfo(name = "restaurant_name")
            private String restaurant_name;

    @ColumnInfo(name = "location")
            private String location;

    @ColumnInfo(name = "date")
            private String date;

    @ColumnInfo(name = "time")
            private String time;


    public Event(){
    }

    public Event(String username, String restaurant_name, String location, String date, String time){
        this.username = username;

        this.restaurant_name = restaurant_name;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Event{" +
                "restaurant_name='" + restaurant_name + '\'' +
                ", username='" + username + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
