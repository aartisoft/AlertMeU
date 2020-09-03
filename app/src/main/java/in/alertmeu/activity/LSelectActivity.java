package in.alertmeu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.VersionChecker;

public class LSelectActivity extends AppCompatActivity {
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TextView english, Hindi;
    Button next;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    String Lag = "";
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_l_select);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        english = (TextView) findViewById(R.id.english);
        Hindi = (TextView) findViewById(R.id.Hindi);
        next = (Button) findViewById(R.id.next);
        if (getLangCode().equals("en")) {
            Hindi.setTextColor(Color.parseColor("#E6E6DC"));
            english.setTextColor(Color.parseColor("#558FE6"));
            prefEditor.putString("ulang", "en");
            prefEditor.commit();
        }
        if (getLangCode().equals("hi")) {
            english.setTextColor(Color.parseColor("#E6E6DC"));
            Hindi.setTextColor(Color.parseColor("#558FE6"));
            prefEditor.putString("ulang", "hi");
            prefEditor.commit();
        }
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hindi.setTextColor(Color.parseColor("#E6E6DC"));
                english.setTextColor(Color.parseColor("#558FE6"));
                Lag = "en";

            }
        });
        Hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                english.setTextColor(Color.parseColor("#E6E6DC"));
                Hindi.setTextColor(Color.parseColor("#558FE6"));
                Lag = "hi";

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Lag.equals("")) {
                    saveLanguage(Lag);
                    Intent i = new Intent(LSelectActivity.this, SplashActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LSelectActivity.this, res.getString(R.string.xpsul), Toast.LENGTH_LONG).show();
                }
            }
        });

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
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void saveLanguage(String lang) {
        prefEditor.putString("ulang", lang);
        prefEditor.commit();
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANG, lang);
        editor.apply();
        recreate();
    }
}