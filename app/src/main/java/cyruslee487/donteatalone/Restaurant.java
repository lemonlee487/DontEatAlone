package cyruslee487.donteatalone;

/**
 * Created by cyrus on 2018-03-22.
 */

public class Restaurant {

    private String name;
    private String address;
    private String imageUrl;

    public Restaurant(String name, String imageUrl){
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
