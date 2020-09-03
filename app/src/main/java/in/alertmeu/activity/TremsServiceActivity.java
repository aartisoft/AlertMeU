package in.alertmeu.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

import in.alertmeu.R;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.models.ImageModel;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.WebClient;

public class TremsServiceActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonSchedule;
    String tcResponse = "";
    boolean status;
    String msg = "", ppt = "", tct = "";
    TextView txtTremsC;
    Button pp, tc;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_trems_service);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        txtTremsC = (TextView) findViewById(R.id.txtTremsC);
        pp = (Button) findViewById(R.id.pp);
        tc = (Button) findViewById(R.id.tc);

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            getTeramsCodition();
        } else {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tc.setBackgroundColor(Color.parseColor("#D9D9D9"));
                pp.setBackgroundColor(Color.parseColor("#08AE9E"));
                txtTremsC.setText(ppt);
            }
        });
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pp.setBackgroundColor(Color.parseColor("#D9D9D9"));
                tc.setBackgroundColor(Color.parseColor("#08AE9E"));
                txtTremsC.setText(tct);
            }
        });
    }

    public void getTeramsCodition() {

        jsonSchedule = new JSONObject() {
            {
                try {

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
                tcResponse = serviceAccess.SendHttpPost(Config.URL_GETTREAMSCONDITION, jsonSchedule);
                Log.i("resp", "tcResponse" + tcResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(tcResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");
                            if (status) {
                                if (!jsonObject.isNull("dataList")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        if (preferences.getString("ulang", "").equals("en")) {
                                            if (object.getString("id").equals("1")) {
                                                txtTremsC.setText(object.getString("details"));
                                                ppt = object.getString("details");
                                            }
                                            if (object.getString("id").equals("2")) {
                                                tct = object.getString("details");
                                            }
                                        } else if (preferences.getString("ulang", "").equals("hi")) {
                                            if (object.getString("id").equals("1")) {
                                                txtTremsC.setText(object.getString("details_hindi"));
                                                ppt = object.getString("details_hindi");
                                            }
                                            if (object.getString("id").equals("2")) {
                                                tct = object.getString("details_hindi");
                                            }
                                        }


                                    }
                                }

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
}
