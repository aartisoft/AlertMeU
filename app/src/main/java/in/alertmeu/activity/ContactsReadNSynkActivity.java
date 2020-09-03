package in.alertmeu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import in.alertmeu.R;
import in.alertmeu.models.ContactModel;
import in.alertmeu.utils.ContactRead;

public class ContactsReadNSynkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_read_nsynk);
        ContactRead contactRead = new ContactRead();
        List<ContactModel> contactModelList = contactRead.getContacts(ContactsReadNSynkActivity.this);
        for (int i = 0; i < contactModelList.size(); i++) {
            Toast.makeText(getApplicationContext(), "Size is" + contactModelList.get(i).name, Toast.LENGTH_SHORT).show();
        }
    }
}
