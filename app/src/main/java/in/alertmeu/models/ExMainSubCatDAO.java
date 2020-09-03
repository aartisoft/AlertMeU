package in.alertmeu.models;

import java.util.ArrayList;
import java.util.List;


public class ExMainSubCatDAO {

    public String catMainSubName;
    public List<ExAdvertisementDAO> advertisements = new ArrayList<ExAdvertisementDAO>();

    public ExMainSubCatDAO(String catMainSubName, List<ExAdvertisementDAO> advertisements) {
        this.catMainSubName = catMainSubName;
        this.advertisements = advertisements;
    }

}
