package in.alertmeu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Constant;

public class CreatePassActivity extends AppCompatActivity {
    EditText uPassword;
    Button btnNext;
    String password = "", mobile = "";
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_create_pass);

        uPassword = (EditText) findViewById(R.id.uPassword);
        btnNext = (Button) findViewById(R.id.btnNext);

        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    password = uPassword.getText().toString().trim();
                    if (password.length() < 6 && !isValidPassword(password)) {

                        Toast.makeText(getApplicationContext(), res.getString(R.string.jnv), Toast.LENGTH_SHORT).show();
                    } else {

                      //  Toast.makeText(getApplicationContext(), "Valid", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreatePassActivity.this, ReEnterPassctivity.class);
                        intent.putExtra("password", password);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                    }


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

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
