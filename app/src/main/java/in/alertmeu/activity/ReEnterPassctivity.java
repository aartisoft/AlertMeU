package in.alertmeu.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.FirebaseNotification.SharedPrefManager;
import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.MyBroadcastReceiver;
import in.alertmeu.utils.WebClient;

public class ReEnterPassctivity extends AppCompatActivity {
    EditText uPassword, uReferral;
    Button btnNext;
    String password = "", repassword = "", mobile_no = "", referral_code = "", referral_status = "0";
    String deviceId = "";
    TelephonyManager telephonyManager;
    boolean status;
    ProgressDialog mProgressDialog;
    JSONObject jsonObj, jsonObject;
    String registrationResponse = "", msg = "";
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String localTime;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_re_enter_passctivity);
        uPassword = (EditText) findViewById(R.id.uPassword);
        uReferral = (EditText) findViewById(R.id.uReferral);
        btnNext = (Button) findViewById(R.id.btnNext);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        mobile_no = intent.getStringExtra("mobile");
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    repassword = uPassword.getText().toString().trim();
                    if (repassword.equals(password)) {
                        // Toast.makeText(getApplicationContext(), password + "," + mobile_no, Toast.LENGTH_SHORT).show();
                        new userRegistration().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpmm), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    private class userRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ReEnterPassctivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(
                    res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage( res.getString(R.string.jppr));
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


                        put("mobile_no", mobile_no);
                        put("password", repassword);
                        put("mobile_device", deviceId);
                        put("referral_code", referral_code);
                        put("referral_status", referral_status);
                        put("fcm_id", token);
                        put("t_zone", localTime);
                        put("referral_status", preferences.getString("apply_U_referral_status", ""));
                        put("referral_code", preferences.getString("apply_u_referral_code", ""));
                        put("referral_user_id", preferences.getString("referral_user_id", ""));
                        put("country_code", preferences.getString("country_code", ""));
                        put("r_country_code", preferences.getString("r_country_code",""));
                        put("latitude", preferences.getString("ur_l",""));
                        put("longitude", preferences.getString("ur_lo",""));

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
                        if (status) {
                            if (!jsonObject.isNull("user_id")) {
                                try {
                                    JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                                    for (int i = 0; i < ujsonArray.length(); i++) {
                                        JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                                        prefEditor.putString("user_id", UJsonObject.getString("id"));
                                        prefEditor.putString("user_referral_code", UJsonObject.getString("referral_code"));
                                        prefEditor.putString("userMobile", mobile_no);
                                        prefEditor.putString("flag", "mobile");
                                        prefEditor.putInt("units_for_area", 15);
                                        prefEditor.putString("notifyonoff", "1");
                                        prefEditor.putString("app_login","2");
                                        prefEditor.putString("account_status", "2");
                                        prefEditor.remove("apply_u_referral_code");
                                        prefEditor.remove("apply_U_referral_status");
                                        prefEditor.remove("referral_user_id");
                                        prefEditor.remove("r_country_code");
                                        prefEditor.commit();
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }

                            prefEditor.putString("user_id", jsonObject.getString("user_id"));

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                  //  Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

             //   Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(ReEnterPassctivity.this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ReEnterPassctivity.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 60); // first time
                long frequency = 10 * 1000; // in ms

                // We want the alarm to go off 30 seconds from now.
                long firstTime = SystemClock.elapsedRealtime();
                firstTime += 1 * 1000;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 60 * 1000, pendingIntent);
                Intent intent = new Intent(ReEnterPassctivity.this, BusinessExpandableListViewActivity.class);
                startActivity(intent);
                finish();


            }
        }
    }
    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
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
}
