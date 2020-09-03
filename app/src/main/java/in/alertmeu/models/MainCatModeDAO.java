package in.alertmeu.models;


import java.util.ArrayList;

public class MainCatModeDAO {
    private String id;
    private String category_name;
    private String category_name_hindi;
    String checked_status;
    String isselected="";
    private ArrayList<SubCatModeDAO> list = new ArrayList<SubCatModeDAO>();
    private boolean isSelected=false;


    public MainCatModeDAO() {

    }

    public MainCatModeDAO(String id, String category_name, String checked_status, String isselected, ArrayList<SubCatModeDAO> list, boolean isSelected) {
        this.id = id;
        this.category_name = category_name;
        this.checked_status = checked_status;
        this.isselected = isselected;
        this.list = list;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getChecked_status() {
        return checked_status;
    }

    public void setChecked_status(String checked_status) {
        this.checked_status = checked_status;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    public ArrayList<SubCatModeDAO> getList() {
        return list;
    }

    public void setList(ArrayList<SubCatModeDAO> list) {
        this.list = list;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategory_name_hindi() {
        return category_name_hindi;
    }

    public void setCategory_name_hindi(String category_name_hindi) {
        this.category_name_hindi = category_name_hindi;
    }
}