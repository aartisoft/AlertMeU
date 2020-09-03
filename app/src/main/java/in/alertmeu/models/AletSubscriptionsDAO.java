package in.alertmeu.models;

public class AletSubscriptionsDAO {

    String id = "";
    String alert_subscriptions_name = "";

    private boolean selected;

    public AletSubscriptionsDAO() {

    }

    public AletSubscriptionsDAO(String id, String alert_subscriptions_name) {
        this.id = id;
        this.alert_subscriptions_name = alert_subscriptions_name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlert_subscriptions_name() {
        return alert_subscriptions_name;
    }

    public void setAlert_subscriptions_name(String alert_subscriptions_name) {
        this.alert_subscriptions_name = alert_subscriptions_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return alert_subscriptions_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationDAO) {
            AletSubscriptionsDAO c = (AletSubscriptionsDAO) obj;
            if (c.getAlert_subscriptions_name().equals(alert_subscriptions_name) && c.getId() == id)
                return true;
        }

        return false;
    }
}
