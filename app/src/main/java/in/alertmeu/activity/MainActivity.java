package in.alertmeu.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hbb20.CountryCodePicker;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import in.alertmeu.R;
import in.alertmeu.adapter.SlidingImage_Adapter;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.ImageModel;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.GPSTracker;
import in.alertmeu.utils.WebClient;

public class MainActivity extends AppCompatActivity {
    Button login, newAccount, learnMore;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    private JSONObject jsonSchedule;
    boolean status;
    String msg = "";
    String imagePathResponse = "", requestResponse = "";
    List<ImageModel> data;
    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    static final Integer ACCESS_FINE_LOCATION = 0x14;
    static final Integer ACCESS_COARSE_LOCATION = 0x15;
    Button reTry;
    RelativeLayout pagerSlide;
    LinearLayout botHideShow, thf;
    String app_status = "";
    TextView infoDisplay, th, aftersubmit;
    CountryCodePicker ccp;
    Button sendRequest;
    EditText emailEdtTxt, mobileEdtTxt;
    String emailId = "", mobileno = "";
    private JSONObject jsonLeadObj;
    ProgressDialog mProgressDialog;
    String localTime;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        login = (Button) findViewById(R.id.login);
        newAccount = (Button) findViewById(R.id.newAccount);
        learnMore = (Button) findViewById(R.id.learnMore);
        reTry = (Button) findViewById(R.id.reTry);
        pagerSlide = (RelativeLayout) findViewById(R.id.pagerSlide);
        botHideShow = (LinearLayout) findViewById(R.id.botHideShow);
        thf = (LinearLayout) findViewById(R.id.thf);
        infoDisplay = (TextView) findViewById(R.id.infoDisplay);
        aftersubmit = (TextView) findViewById(R.id.aftersubmit);
        emailEdtTxt = (EditText) findViewById(R.id.emailEdtTxt);
        mobileEdtTxt = (EditText) findViewById(R.id.mobileEdtTxt);
        sendRequest = (Button) findViewById(R.id.sendRequest);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        th = (TextView) findViewById(R.id.th);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());
        prefEditor.putString("country_code", ccp.getSelectedCountryCodeWithPlus());
        prefEditor.commit();
// create class object
        for (int i = 0; i <= 30; i++) {
            gps = new GPSTracker(MainActivity.this);
        }

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.i("TrackResponse", "" + latitude);
            Log.i("TrackResponse", "" + longitude);
            if (latitude != 0.0 && longitude != 0.0) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    reTry.setVisibility(View.GONE);
                    pagerSlide.setVisibility(View.VISIBLE);
                    prefEditor.putString("ur_l", "" + latitude);
                    prefEditor.putString("ur_lo", "" + longitude);
                    prefEditor.commit();
                    getImagePath();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            } else {
                pagerSlide.setVisibility(View.GONE);
                reTry.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), res.getString(R.string.jpta), Toast.LENGTH_LONG).show();
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //

            pagerSlide.setVisibility(View.GONE);
            reTry.setVisibility(View.VISIBLE);
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Do something for lollipop and above versions
                displayLocationSettingsRequest(MainActivity.this);
            } else {
                // do something for phones running an SDK before lollipop
                gps.showSettingsAlert();

            }
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    emailId = emailEdtTxt.getText().toString();
                    mobileno = mobileEdtTxt.getText().toString();

                    if (mobileno.equals("") && emailId.equals("")) {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jemn), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!emailId.equals("")) {
                            if (validate(emailId)) {
                                new insertRequest().execute();
                            }
                        } else {
                            new insertRequest().execute();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent intent = new Intent(MainActivity.this, RegisterNGetStartActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(MainActivity.this, LMActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        reTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <= 30; i++) {
                    gps = new GPSTracker(MainActivity.this);
                }

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.i("TrackResponse", "" + latitude);
                    Log.i("TrackResponse", "" + longitude);
                    if (latitude != 0.0 && longitude != 0.0) {
                        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                            reTry.setVisibility(View.GONE);
                            pagerSlide.setVisibility(View.VISIBLE);
                            prefEditor.putString("ur_l", "" + latitude);
                            prefEditor.putString("ur_lo", "" + longitude);
                            prefEditor.commit();
                            getImagePath();
                        } else {
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pagerSlide.setVisibility(View.GONE);
                        reTry.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpta), Toast.LENGTH_LONG).show();
                        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
                    }

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    //


                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                        // Do something for lollipop and above versions
                        displayLocationSettingsRequest(MainActivity.this);
                    } else {
                        // do something for phones running an SDK before lollipop
                        gps.showSettingsAlert();

                    }
                }
            }
        });
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

    private void init() {
        th.setVisibility(View.GONE);
        mPager = (ViewPager) findViewById(R.id.pager);
        // mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, imageModelArrayList));
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, data));
        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(data.size() * density);

        // NUM_PAGES = imageModelArrayList.size();
        NUM_PAGES = data.size();
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 10000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

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

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("status", 1);
                    put("latitude", latitude);
                    put("longitude", longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };


        Thread objectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonSchedule);
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSLIDEIMAGES, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");
                            app_status = jsonObject.getString("app_status");
                            if (status) {
                                if (Integer.parseInt(app_status) > 0) {
                                    infoDisplay.setVisibility(View.GONE);
                                    thf.setVisibility(View.GONE);
                                    botHideShow.setVisibility(View.VISIBLE);
                                    pagerSlide.setVisibility(View.VISIBLE);
                                    data = new ArrayList<>();
                                    JsonHelper jsonHelper = new JsonHelper();
                                    data = jsonHelper.parseImagePathList(imagePathResponse);
                                    init();
                                } else {
                                    botHideShow.setVisibility(View.GONE);
                                    pagerSlide.setVisibility(View.GONE);
                                    infoDisplay.setVisibility(View.VISIBLE);
                                    thf.setVisibility(View.VISIBLE);

                                }

                            } else {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        objectThread.start();

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public void displayLocationSettingsRequest(final Context context) {
// final int REQUEST_CHECK_SETTINGS = 0x1;
        final String TAG = "MainActivity";
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
// Show the dialog by calling startResolutionForResult(), and check the result
// in onActivityResult().
                            status.startResolutionForResult((MainActivity) context, REQUEST_CHECK_SETTINGS);

// getActivity().startActivityForResult((MainActivity)getActivity(),REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private class insertRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsq));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("email", emailId);
                        put("mobile", mobileno);
                        put("latitude", latitude);
                        put("longitude", longitude);
                        put("t_zone", localTime);
                        put("app_type", "1");


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            requestResponse = serviceAccess.SendHttpPost(Config.URL_INSERTUSERRQ, jsonLeadObj);
            Log.i("resp", "requestResponse" + requestResponse);

            if (requestResponse.compareTo("") != 0) {
                if (isJSONValid(requestResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(requestResponse);
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
                            Toast.makeText(MainActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
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
                thf.setVisibility(View.GONE);
                infoDisplay.setVisibility(View.GONE);
                aftersubmit.setVisibility(View.VISIBLE);
                // Toast.makeText(MainActivity.this,  msg, Toast.LENGTH_LONG).show();


            } else {
                // Toast.makeText(MainActivity.this,  msg, Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}