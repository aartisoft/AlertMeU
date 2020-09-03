package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import in.alertmeu.FirebaseNotification.SharedPrefManager;
import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.WebClient;

public class RegisterActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    int pos;
    private EditText nameEdtTxt, lastNameEdtTxt, phEdtTxt, emailEdtTxt, passEdtTxt;
    private Button loginButton, registerButton;
    String deviceId = "";
    TelephonyManager telephonyManager;
    String gender = "", first_name = "", last_name = "", email_id = "", mobile_no = "", user_password = "", registrationResponse = "", msg = "";
    boolean status;
    ProgressDialog mProgressDialog;
    JSONObject jsonObj, jsonObject;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEdtTxt = (EditText) findViewById(R.id.nameEdtTxt);
        lastNameEdtTxt = (EditText) findViewById(R.id.lastNameEdtTxt);
        phEdtTxt = (EditText) findViewById(R.id.phEdtTxt);
        emailEdtTxt = (EditText) findViewById(R.id.emailEdtTxt);
        passEdtTxt = (EditText) findViewById(R.id.passEdtTxt);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phEdtTxt);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());

        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    mobile_no = ccp.getFullNumberWithPlus();
                    Toast.makeText(getApplicationContext(), "Your mobile number is valid.", Toast.LENGTH_SHORT).show();
                } else {
                    mobile_no = "";
                    //Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                    } else {

                        Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                first_name = nameEdtTxt.getText().toString();
                last_name = lastNameEdtTxt.getText().toString();
                // mobile_no = phEdtTxt.getText().toString().trim();
                email_id = emailEdtTxt.getText().toString();
                user_password = passEdtTxt.getText().toString().trim();
                if (validate(gender, first_name, last_name, mobile_no,user_password)) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        new userRegistration().execute();

                    } else {

                        Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

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
    }



    public boolean validate(String gender, String first_name, String last_name, String mobile_no, String user_password) {
        boolean isValidate = false;
        if (gender.equals("")) {
            Toast.makeText(getApplicationContext(), "Please select gender.", Toast.LENGTH_LONG).show();
        } else if (first_name.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter  first name", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (last_name.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter  last name", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (mobile_no.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter  valid Mobile No.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } /*else if (email_id.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Email Id.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (!validateEmail(email_id)) {
            if (!email_id.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter valid Email Id.", Toast.LENGTH_LONG).show();
                isValidate = false;
            } else {
                isValidate = true;
            }
        }*/ else if (user_password.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_LONG).show();
            isValidate = false;

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

    private class userRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Registration...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            jsonObj = new JSONObject() {
                {
                    try {

                        put("first_name", first_name);
                        put("last_name", last_name);
                        put("mobile_no", mobile_no);
                        put("email_id", email_id);
                        put("gender", gender);
                        put("password", user_password);
                        put("mobile_device", deviceId);
                        put("fcm_id", token);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERBYA, jsonObj);
            Log.i("resp", "registrationResponse" + registrationResponse);


            if (registrationResponse.compareTo("") != 0) {
                if (isJSONValid(registrationResponse)) {


                    try {

                        jsonObject = new JSONObject(registrationResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                finish();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }
        }
    }

    protected boolean isJSONValid(String registrationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(registrationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(registrationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(setIntent);
    }
}
