package in.alertmeu.models;



public class MyPlaceModeDAO {
    private String id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String full_address;
    private String time_stamp;

    public MyPlaceModeDAO() {

    }

    public MyPlaceModeDAO(String id, String user_id, String latitude, String longitude, String full_address, String time_stamp) {
        this.id = id;
        this.user_id = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.full_address = full_address;
        this.time_stamp = time_stamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}