package in.alertmeu.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactModel {
    public String id;
    public String name;
    public String mobileNumber;
    public Bitmap photo;
    public Uri photoURI;

    public ContactModel() {

    }

    public ContactModel(String id, String name, String mobileNumber, Bitmap photo, Uri photoURI) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.photo = photo;
        this.photoURI = photoURI;
    }
}
