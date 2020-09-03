package in.alertmeu.models;

public class AlertTypeDAO {
    String id = "";
    String alert_name = "";

    private boolean selected;

    public AlertTypeDAO() {

    }

    public AlertTypeDAO(String id, String alert_name) {
        this.id = id;
        this.alert_name = alert_name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlert_name() {
        return alert_name;
    }

    public void setAlert_name(String alert_name) {
        this.alert_name = alert_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    @Override
    public String toString() {
        return alert_name;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationDAO) {
            AlertTypeDAO c = (AlertTypeDAO) obj;
            if (c.getAlert_name().equals(alert_name) && c.getId() == id) return true;
        }

        return false;
    }
}
