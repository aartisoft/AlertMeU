package in.alertmeu.models;


public class DealsNameDAO {
    String id = "";
    String titile = "";


    public DealsNameDAO() {

    }


    public DealsNameDAO(String id, String titile) {
        this.id = id;
        this.titile = titile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    @Override
    public String toString() {
        return titile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DealsNameDAO) {
            DealsNameDAO c = (DealsNameDAO) obj;
            if (c.getTitile().equals(titile))
                return true;
        }

        return false;
    }
}
