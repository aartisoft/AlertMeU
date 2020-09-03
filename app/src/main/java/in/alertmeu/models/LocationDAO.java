package in.alertmeu.models;

public class LocationDAO {
    String id="";
    String user_id="";
    String description = "";
    String describe_limitations = "";
    String latitude="";
    String longitude="";
    String create_at="";
    String flag_map="";
    String path="";
    String rq_code="";
    String title="";
    String mobile_no="";
    String 	address="";
    String likecnt="";
    String 	dislikecnt="";
    String business_name="";
    String s_date="";
    String e_date="";
    String 	s_time="";
    String e_time="";
    String numbers = "";
    String 	business_main_category="";
    String business_subcategory = "";
    String 	business_number="";
    String business_email = "";
    private boolean isSelected;

    public LocationDAO() {

    }

    public LocationDAO(String id, String user_id, String description, String describe_limitations, String latitude, String longitude, String create_at, String flag_map, String path, String rq_code, String title, String mobile_no, String address, String likecnt, String dislikecnt, String business_name, String s_date, String e_date, String s_time, String e_time, String numbers, boolean isSelected) {
        this.id = id;
        this.user_id = user_id;
        this.description = description;
        this.describe_limitations = describe_limitations;
        this.latitude = latitude;
        this.longitude = longitude;
        this.create_at = create_at;
        this.flag_map = flag_map;
        this.path = path;
        this.rq_code = rq_code;
        this.title = title;
        this.mobile_no = mobile_no;
        this.address = address;
        this.likecnt = likecnt;
        this.dislikecnt = dislikecnt;
        this.business_name = business_name;
        this.s_date = s_date;
        this.e_date = e_date;
        this.s_time = s_time;
        this.e_time = e_time;
        this.numbers = numbers;
        this.isSelected = isSelected;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescribe_limitations() {
        return describe_limitations;
    }

    public void setDescribe_limitations(String describe_limitations) {
        this.describe_limitations = describe_limitations;
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

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getFlag_map() {
        return flag_map;
    }

    public void setFlag_map(String flag_map) {
        this.flag_map = flag_map;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRq_code() {
        return rq_code;
    }

    public void setRq_code(String rq_code) {
        this.rq_code = rq_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLikecnt() {
        return likecnt;
    }

    public void setLikecnt(String likecnt) {
        this.likecnt = likecnt;
    }

    public String getDislikecnt() {
        return dislikecnt;
    }

    public void setDislikecnt(String dislikecnt) {
        this.dislikecnt = dislikecnt;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getS_date() {
        return s_date;
    }

    public void setS_date(String s_date) {
        this.s_date = s_date;
    }

    public String getE_date() {
        return e_date;
    }

    public void setE_date(String e_date) {
        this.e_date = e_date;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getBusiness_main_category() {
        return business_main_category;
    }

    public void setBusiness_main_category(String business_main_category) {
        this.business_main_category = business_main_category;
    }

    public String getBusiness_subcategory() {
        return business_subcategory;
    }

    public void setBusiness_subcategory(String business_subcategory) {
        this.business_subcategory = business_subcategory;
    }

    public String getBusiness_number() {
        return business_number;
    }

    public void setBusiness_number(String business_number) {
        this.business_number = business_number;
    }

    public String getBusiness_email() {
        return business_email;
    }

    public void setBusiness_email(String business_email) {
        this.business_email = business_email;
    }
}
