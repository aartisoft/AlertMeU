package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;
import in.alertmeu.view.AddEEntryView;
import in.alertmeu.view.AddPEntryView;

public class UpdateUserProfileActivity extends AppCompatActivity {
    String userName = "";
    EditText first_name, last_name;
    TextView edtEmailOb, edtMobileOb;
    Button update;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private JSONObject jsonLeadObj, jsonSchedule;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "";
    String updateStatusResponse = "", imagePathResponse = "";
    String firstName = "", lastName = "", emailId = "", gender = "", mobileno = "";
    RadioGroup radioGroup;
    private RadioButton radioButton;
    int pos;
    private RadioButton male, female;
    Button Edit, add, addM, EditM;
    Resources res ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        res = getResources();
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();

        Intent intent = getIntent();
        userName = intent.getStringExtra("name");
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        update = (Button) findViewById(R.id.update);
        edtEmailOb = (TextView) findViewById(R.id.edtEmailOb);
        edtMobileOb = (TextView) findViewById(R.id.edtMobileOb);
        male = (RadioButton) findViewById(R.id.male);
        add = (Button) findViewById(R.id.add);
        Edit = (Button) findViewById(R.id.Edit);
        addM = (Button) findViewById(R.id.addM);
        EditM = (Button) findViewById(R.id.EditM);
        female = (RadioButton) findViewById(R.id.female);
        if (!userName.equals("")) {
            // String name[] = userName.split(" ");
            first_name.setText(preferences.getString("first_name", ""));
            last_name.setText(preferences.getString("last_name", ""));
        }
        gender = preferences.getString("gender", "");
        if(!preferences.getString("user_mobile", "").equals(""))
        {
            addM.setVisibility(View.GONE);
            EditM.setVisibility(View.VISIBLE);
            edtMobileOb.setText(preferences.getString("user_mobile", ""));
        }
        if(!preferences.getString("user_mobile", "").equals(""))
        {
            add.setVisibility(View.GONE);
            Edit.setVisibility(View.VISIBLE);
            edtEmailOb.setText(preferences.getString("user_email", ""));
        }


        if (gender.equals("Male")) {
            male.setChecked(true);
        }
        if (gender.equals("Female")) {
            female.setChecked(true);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                // Method 1 For Getting Index of RadioButton
                pos = radioGroup.indexOfChild(findViewById(checkedId));
                switch (pos) {
                    case 1:

                        gender = "Female";
                        break;
                    case 2:
                        gender = "Male";
                        break;

                    default:
                        //The default selection is RadioButton 1
                        gender = "Female";
                        break;
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    firstName = first_name.getText().toString().trim();
                    lastName = last_name.getText().toString().trim();
                    if (validate(firstName, lastName)) {
                        new profileUpdate().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AddEEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (!messageText.trim().equals("")) {
                    add.setVisibility(View.GONE);
                    Edit.setVisibility(View.VISIBLE);
                    edtEmailOb.setText(messageText);
                    prefEditor.putString("user_email", messageText);
                    prefEditor.commit();
                }
            }
        });

        AddPEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (!messageText.trim().equals("")) {
                    addM.setVisibility(View.GONE);
                    EditM.setVisibility(View.VISIBLE);
                    edtMobileOb.setText(messageText);
                    prefEditor.putString("user_mobile", messageText);
                    prefEditor.commit();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEEntryView addEEntryView = new AddEEntryView();
                addEEntryView.show(getSupportFragmentManager(), "addEEntryView");
            }
        });
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEEntryView addEEntryView = new AddEEntryView();
                addEEntryView.show(getSupportFragmentManager(), "addEEntryView");
            }
        });
        addM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPEntryView addPEntryView = new AddPEntryView();
                addPEntryView.show(getSupportFragmentManager(), "addPEntryView");
            }
        });
        EditM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPEntryView addPEntryView = new AddPEntryView();
                addPEntryView.show(getSupportFragmentManager(), "addPEntryView");
            }
        });
    }

    private class profileUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(UpdateUserProfileActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.juppro));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("first_name", firstName);
                        put("last_name", lastName);
                        put("gender", gender);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEUSERPROFILE, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(updateStatusResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdateUserProfileActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateUserProfileActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (status) {
                prefEditor.putString("user_name", firstName + " " + lastName);
                prefEditor.putString("first_name", firstName);
                prefEditor.putString("last_name", lastName);
                prefEditor.putString("gender", gender);
                prefEditor.commit();
                Intent intent = new Intent(UpdateUserProfileActivity.this, UserProfileSettingActivity.class);
                startActivity(intent);
                finish();
            } else {


                ;
            }


        }
    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(String first_name, String last_name) {
        boolean isValidate = false;
        if (first_name.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpef), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (last_name.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpeln), Toast.LENGTH_LONG).show();
            isValidate = false;

        }/*
        if (gender.equals("")) {
            Toast.makeText(getApplicationContext(), "Please select gender.", Toast.LENGTH_LONG).show();
        }
         else if (mobile_no.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter  valid Mobile No.", Toast.LENGTH_LONG).show();
            isValidate = false;

        }
        else if (email_id.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Email Id.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (!validateEmail(email_id)) {
            if (!email_id.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter valid Email Id.", Toast.LENGTH_LONG).show();
                isValidate = false;
            } else {
                isValidate = true;
            }
        } else if (user_password.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } */ else {
            isValidate = true;
        }
        return isValidate;
    }

    public boolean validateEmail(String first_name, String last_name, String email) {
        boolean isValidate = false;
        if (first_name.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpef), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (last_name.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpeln), Toast.LENGTH_LONG).show();
            isValidate = false;

        }/*
        if (gender.equals("")) {
            Toast.makeText(getApplicationContext(), "Please select gender.", Toast.LENGTH_LONG).show();
        }
         else if (mobile_no.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter  valid Mobile No.", Toast.LENGTH_LONG).show();
            isValidate = false;

        }
       else if (user_password.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } */ else if (email.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jemi), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (!validateEmail(email)) {
            if (!email.equals("")) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.jvemi), Toast.LENGTH_LONG).show();
                isValidate = false;
            } else {
                isValidate = true;
            }
        } else {
            isValidate = true;
        }
        return isValidate;
    }

    /**
     * email validation
     */
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    public boolean validateEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(UpdateUserProfileActivity.this, UserProfileSettingActivity.class);
        startActivity(setIntent);
    }
}
