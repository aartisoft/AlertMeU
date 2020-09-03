package in.alertmeu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.imageUtils.ImageLoader;

public class FullScreenViewActivity extends AppCompatActivity {
    ImageView imgDisplay;
    Button btnClose;
    String path = "",flag="",describe_limitations="",description="";
    TextView tid;
    TextView dis,validity, subCat, mainCat;
    TextView mLocationMarkerText, titleTxt, limitation;
    LinearLayout limithideshow, deshideshow;
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_full_screen_view);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        flag=intent.getStringExtra("flag");
        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        btnClose = (Button) findViewById(R.id.btnClose);
        dis = (TextView) findViewById(R.id.dis);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        validity = (TextView) findViewById(R.id.validity);
        limithideshow = (LinearLayout) findViewById(R.id.limithideshow);
        deshideshow = (LinearLayout) findViewById(R.id.deshideshow);
        limitation = (TextView) findViewById(R.id.limitation);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        titleTxt.setText(intent.getStringExtra("title"));
        describe_limitations = intent.getStringExtra("describe_limitations");
        mainCat.setText(res.getString(R.string.jadcat)+ intent.getStringExtra("main_cat_name"));
        subCat.setText(res.getString(R.string.jadscat)+ intent.getStringExtra("subcategory_name"));
        description = intent.getStringExtra("description");
        tid=(TextView)findViewById(R.id.tid);
        if (!description.equals("")) {
            deshideshow.setVisibility(View.VISIBLE);
            dis.setText(description);

        }
        if (!describe_limitations.equals("")) {
            limithideshow.setVisibility(View.VISIBLE);
            limitation.setText(describe_limitations);
        }
        validity.setText(parseTime(intent.getStringExtra("stime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("sdate")) + " to " + parseTime(intent.getStringExtra("etime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("edate")));

        if(flag.equals("1")) {
            tid.setVisibility(View.GONE);
            ImageLoader imageLoader = new ImageLoader(FullScreenViewActivity.this);
            imageLoader.DisplayImage(path, imgDisplay);

        }
        else
        {
            tid.setVisibility(View.VISIBLE);
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + path;
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            imgDisplay.setImageBitmap(bmp);
        }

    }
    public static String parseTime(String time, String inFormat, String outFormat) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat(outFormat).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

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
