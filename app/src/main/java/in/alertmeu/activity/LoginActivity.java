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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import in.alertmeu.FirebaseNotification.SharedPrefManager;
import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.MyBroadcastReceiver;
import in.alertmeu.utils.WebClient;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    public static LoginActivity fa;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private EditText u_emailid, u_pass, u_mobile;
    private TextView title, register, loginEmailButton, loginMobileButton, hide1;
    private Button emailNext, mobileNext;
    String userId = "", userMobile = "", userPassword = "", loginResponse = "", msg = "", registrationResponse = "", referral_status = "0";
    boolean status;
    ProgressDialog mProgressDialog;
    JSONObject jsonObj, jsonObject;
    //
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    String deviceId = "";
    TelephonyManager telephonyManager;
    String first_name = "", last_name = "", email_id = "";
    LinearLayout layoutHide, hide2;
    CountryCodePicker ccp;
    String personPhotoUrl = "";
    String localTime;
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_login);

        fa = this;
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        u_emailid = (EditText) findViewById(R.id.u_emailid);
        u_mobile = (EditText) findViewById(R.id.u_mobile);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        title = (TextView) findViewById(R.id.title);
        loginMobileButton = (TextView) findViewById(R.id.loginMobileButton);
        loginEmailButton = (TextView) findViewById(R.id.loginEmailButton);
        register = (TextView) findViewById(R.id.register);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        hide1 = (TextView) findViewById(R.id.hide1);
        hide2 = (LinearLayout) findViewById(R.id.hide2);
        emailNext = (Button) findViewById(R.id.emailNext);
        mobileNext = (Button) findViewById(R.id.mobileNext);
        layoutHide = (LinearLayout) findViewById(R.id.layoutHide);
        btnSignIn.setOnClickListener(this);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(u_mobile);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    userMobile = ccp.getFullNumberWithPlus();
                    //  Toast.makeText(getApplicationContext(), "Your mobile number is valid.", Toast.LENGTH_SHORT).show();
                } else {
                    userMobile = "";
                    //  Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginMobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(res.getString(R.string.xemn));
                u_emailid.setVisibility(View.GONE);
                emailNext.setVisibility(View.GONE);
                layoutHide.setVisibility(View.VISIBLE);
                mobileNext.setVisibility(View.VISIBLE);
                hide1.setVisibility(View.VISIBLE);
                hide2.setVisibility(View.VISIBLE);
                loginMobileButton.setVisibility(View.GONE);
                loginEmailButton.setVisibility(View.VISIBLE);

            }
        });
        loginEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(res.getString(R.string.l_email));
                layoutHide.setVisibility(View.GONE);
                mobileNext.setVisibility(View.GONE);
                hide1.setVisibility(View.GONE);
                hide2.setVisibility(View.GONE);
                u_emailid.setVisibility(View.VISIBLE);
                emailNext.setVisibility(View.VISIBLE);
                loginMobileButton.setVisibility(View.VISIBLE);
                loginEmailButton.setVisibility(View.GONE);
            }
        });

        mobileNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Mobile" + u_mobile.getText().toString(), Toast.LENGTH_SHORT).show();
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    //userMobile = u_mobile.getText().toString().trim();
                    if (validateMobile(userMobile)) {
                        prefEditor.remove("user_name");
                        prefEditor.commit();
                        new userCheckMobile().execute();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        emailNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Email" + u_emailid.getText().toString(), Toast.LENGTH_SHORT).show();
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    userId = u_emailid.getText().toString().trim();
                    if (validate(userId)) {
                        prefEditor.remove("user_name");
                        prefEditor.commit();
                        new userCheckEmail().execute();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent intent = new Intent(LoginActivity.this, RegisterNGetStartActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        setGooglePlusButtonText(btnSignIn,res.getString(R.string.xsi));
    }

    public boolean validateMobile(String userMobile) {
        boolean isValidate = false;
        if (userMobile.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpenm), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else {
            isValidate = true;
        }
        return isValidate;
    }

    public boolean validate(String email) {
        boolean isValidate = false;
        if (email.trim().equals("")) {
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

    private class userCheckEmail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jlogin));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj = new JSONObject() {
                {
                    try {
                        put("email_users", userId);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            loginResponse = serviceAccess.SendHttpPost(Config.URL_CHECKUSEREMAILLOGIN, jsonObj);
            Log.i("resp", "loginResponse" + loginResponse);


            if (loginResponse.compareTo("") != 0) {
                if (isJSONValid(loginResponse)) {


                    try {

                        jsonObject = new JSONObject(loginResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            if (!jsonObject.isNull("user_id")) {
                                JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                                for (int i = 0; i < ujsonArray.length(); i++) {
                                    JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                                    if (!UJsonObject.getString("first_name").equals("")) {
                                        prefEditor.putString("user_name", UJsonObject.getString("first_name") + " " + UJsonObject.getString("last_name"));
                                        prefEditor.putString("gender", UJsonObject.getString("gender"));
                                        prefEditor.putString("userEmail", UJsonObject.getString("email_id"));

                                    }
                                    prefEditor.putString("userEmail", userId);
                                    prefEditor.putString("first_name", UJsonObject.getString("first_name"));
                                    prefEditor.putString("flag", "email");
                                    prefEditor.putInt("units_for_area", 15);
                                    prefEditor.putString("notifyonoff", "1");
                                    prefEditor.commit();

                                }
                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    // Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, PostLoginActivity.class);
                startActivity(intent);

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }
        }
    }

    private class userCheckMobile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jlogin));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj = new JSONObject() {
                {
                    try {
                        put("mobile_users", userMobile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            loginResponse = serviceAccess.SendHttpPost(Config.URL_CHECKUSERMOBILELOGIN, jsonObj);
            Log.i("resp", "loginResponse" + loginResponse);


            if (loginResponse.compareTo("") != 0) {
                if (isJSONValid(loginResponse)) {


                    try {

                        jsonObject = new JSONObject(loginResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            if (!jsonObject.isNull("user_id")) {
                                JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                                for (int i = 0; i < ujsonArray.length(); i++) {
                                    JSONObject UJsonObject = ujsonArray.getJSONObject(i);

                                    if (!UJsonObject.getString("first_name").equals("")) {
                                        prefEditor.putString("user_name", UJsonObject.getString("first_name") + " " + UJsonObject.getString("last_name"));
                                        prefEditor.putString("gender", UJsonObject.getString("gender"));
                                        prefEditor.putString("userEmail", UJsonObject.getString("email_id"));
                                    }
                                    prefEditor.putString("account_status", UJsonObject.getString("account_status"));
                                    prefEditor.putString("userMobile", userMobile);
                                    prefEditor.putString("flag", "mobile");
                                    prefEditor.putInt("units_for_area", 15);
                                    prefEditor.putString("notifyonoff", "1");
                                    prefEditor.commit();
                                }
                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    // Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, PostLoginActivity.class);
                startActivity(intent);

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
               // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        // showProgressDialog();
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            personPhotoUrl = "" + acct.getPhotoUrl();
            String email = acct.getEmail();


            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: ");
            if (!personName.equals("")) {
                String columns[] = personName.split(" ");
                first_name = columns[0];
                last_name = columns[1];

            }
            email_id = acct.getEmail();
            prefEditor.putString("user_name", personName);
            prefEditor.putString("userEmail", email);
            prefEditor.putString("pic_name", personPhotoUrl);
            prefEditor.commit();

            new userRegistration().execute();

           /* txtName.setText(personName);
            txtEmail.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);
*/
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            // showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    // hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /*private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }*/

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);

        } else {
            btnSignIn.setVisibility(View.VISIBLE);

        }
    }

    private class userRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Registration...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            jsonObj = new JSONObject() {
                {
                    try {

                        put("first_name", first_name);
                        put("last_name", last_name);
                        put("email_id", email_id);
                        put("mobile_device", deviceId);
                        put("fcm_id", token);
                        put("profilePath", personPhotoUrl);
                        put("t_zone", localTime);
                        put("referral_status", referral_status);
                        put("country_code", preferences.getString("country_code", ""));
                        put("latitude", preferences.getString("ur_l",""));
                        put("longitude", preferences.getString("ur_lo",""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERBYGOOGLE, jsonObj);
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
                    //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                if (!jsonObject.isNull("user_id")) {
                    try {
                        JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                        for (int i = 0; i < ujsonArray.length(); i++) {
                            JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                            prefEditor.putString("user_id", UJsonObject.getString("id"));
                            prefEditor.putString("user_referral_code", UJsonObject.getString("referral_code"));
                            prefEditor.putString("user_name", UJsonObject.getString("first_name") + " " + UJsonObject.getString("last_name"));
                            prefEditor.putString("user_mobile", UJsonObject.getString("mobile_no"));
                            prefEditor.putString("first_name", UJsonObject.getString("first_name"));
                            prefEditor.putString("last_name", UJsonObject.getString("last_name"));
                            prefEditor.putString("userEmail", UJsonObject.getString("email_id"));
                            prefEditor.putString("user_email", UJsonObject.getString("user_email"));
                            prefEditor.putString("user_mobile", UJsonObject.getString("user_mobile"));
                            prefEditor.putString("pic_name", UJsonObject.getString("profilePath"));
                            prefEditor.putInt("units_for_area", 15);
                            prefEditor.putString("notifyonoff", "1");
                            prefEditor.putString("app_login", "3");
                            prefEditor.putString("account_status", "3");
                            prefEditor.commit();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                Intent myIntent = new Intent(LoginActivity.this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginActivity.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 60); // first time
                long frequency = 10 * 1000; // in ms

                // We want the alarm to go off 30 seconds from now.
                long firstTime = SystemClock.elapsedRealtime();
                firstTime += 1 * 1000;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 60 * 1000, pendingIntent);
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();

                // Close the progressdialog
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
              //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                // mProgressDialog.dismiss();

            }
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
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
    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(setIntent);
    }
}
