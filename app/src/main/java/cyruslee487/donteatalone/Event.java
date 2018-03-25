package cyruslee487.donteatalone;

/**
 * Created by cyrus on 2018-03-23.
 */

public class Event {

    String restaurant_name, username;
    String location;
    String date, time;

    public Event(){
    }

    public Event(String username, String restaurant_name, String location, String date, String time){
        this.username = username;

        this.restaurant_name = restaurant_name;
        this.location = location;
        this.date = date;
        this.time = time;
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
