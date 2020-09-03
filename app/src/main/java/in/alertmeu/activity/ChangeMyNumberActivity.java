package in.alertmeu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.WebClient;

public class ChangeMyNumberActivity extends AppCompatActivity {
    EditText new_mobile;

    CountryCodePicker ccp, ccp1;
    String old_number = "", new_number = "", verifyMobileResponse = "";
    JSONObject jsonObj;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TextView old_mobile, warning;
    boolean status;
    Button btnNext;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_change_my_number);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        old_mobile = (TextView) findViewById(R.id.old_mobile);
        new_mobile = (EditText) findViewById(R.id.new_mobile);
        warning = (TextView) findViewById(R.id.warning);
        btnNext = (Button) findViewById(R.id.btnNext);
        old_mobile.setText(preferences.getString("userMobile", ""));
        ccp1 = (CountryCodePicker) findViewById(R.id.ccp1);
        ccp1.registerCarrierNumberEditText(new_mobile);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp1.isValidFullNumber();
        ccp1.setCountryPreference(ccp1.getDefaultCountryNameCode());
        ccp1.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    new_number = ccp1.getFullNumberWithPlus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpevn), Toast.LENGTH_SHORT).show();
                } else {
                    new_number = "";
                    //Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    if (!new_number.equals("")) {
                        Intent intent = new Intent(ChangeMyNumberActivity.this, OTPforChangeNumberActivity.class);
                        intent.putExtra("new_number", new_number);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpeevn), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void verifyMobileNumber() {


        jsonObj = new JSONObject() {
            {
                try {
                    put("mobile_no", old_number);
                    put("user_id", preferences.getString("user_id", ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread objectThread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                verifyMobileResponse = serviceAccess.SendHttpPost(Config.URL_GETAVAILABLECHANGEMOBILENUMBER, jsonObj);
                Log.i("loginResponse", "verifyMobileResponse" + verifyMobileResponse);
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() { // This thread runs in the UI
                            @Override
                            public void run() {
                                if (verifyMobileResponse.compareTo("") == 0) {

                                } else {

                                    try {
                                        JSONObject jObject = new JSONObject(verifyMobileResponse);
                                        status = jObject.getBoolean("status");
                                        if (status) {

                                            // Toast.makeText(getApplicationContext(), "Already Exist", Toast.LENGTH_LONG).show();

                                            warning.setVisibility(View.GONE);
                                            btnNext.setVisibility(View.VISIBLE);

                                        } else {
                                            warning.setVisibility(View.VISIBLE);
                                            btnNext.setVisibility(View.GONE);

                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                };

                new Thread(runnable).start();
            }
        });
        objectThread.start();
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
        Intent setIntent = new Intent(ChangeMyNumberActivity.this, InstructionChangeNumberActivity.class);
        startActivity(setIntent);
        finish();
    }
}
