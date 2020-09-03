package in.alertmeu.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

import in.alertmeu.R;

public class HelpCenterActivity extends AppCompatActivity {
    LinearLayout logout;
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    LinearLayout changenumber, faq, contactus, termsprivacy, changepassword;
    TextView appversion;
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_help_center);

        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        changenumber = (LinearLayout) findViewById(R.id.changenumber);
        faq = (LinearLayout) findViewById(R.id.faq);
        contactus = (LinearLayout) findViewById(R.id.contactus);
        termsprivacy = (LinearLayout) findViewById(R.id.termsprivacy);
        changepassword = (LinearLayout) findViewById(R.id.changepassword);
        appversion = (TextView) findViewById(R.id.appversion);

        logout = (LinearLayout) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HelpCenterActivity.this, LogoutActivity.class);
                startActivity(intent);

            }
        });
        if(preferences.getString("account_status","").equals("2"))
        {
            changenumber.setVisibility(View.VISIBLE);
        }
        changenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("userEmail", "").equals("") || !preferences.getString("userMobile", "").equals("")) {
                    if (preferences.getString("app_login", "").equals("2")) {
                        Intent intent = new Intent(HelpCenterActivity.this, InstructionChangeNumberActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (preferences.getString("app_login", "").equals("3"))
                            Toast.makeText(HelpCenterActivity.this, res.getString(R.string.julg), Toast.LENGTH_SHORT).show();
                        if (preferences.getString("app_login", "").equals("4"))
                            Toast.makeText(HelpCenterActivity.this, res.getString(R.string.julf), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HelpCenterActivity.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    Intent intent = new Intent(HelpCenterActivity.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(res.getString(R.string.helpCan)
                                    , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();

                }
            }
        });

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("userEmail", "").equals("") || !preferences.getString("userMobile", "").equals("")) {
                    if (preferences.getString("app_login", "").equals("2")) {
                        Intent intent = new Intent(HelpCenterActivity.this, ChangeMyPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (preferences.getString("app_login", "").equals("3"))
                            Toast.makeText(HelpCenterActivity.this, res.getString(R.string.julg), Toast.LENGTH_SHORT).show();
                        if (preferences.getString("app_login", "").equals("4"))
                            Toast.makeText(HelpCenterActivity.this, res.getString(R.string.julf), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HelpCenterActivity.this);
                    builder.setMessage(R.string.jpr)
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(HelpCenterActivity.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(res.getString(R.string.helpCan), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();

                }
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, FQAActivity.class);
                startActivity(intent);
            }
        });
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("userEmail", "").equals("") || !preferences.getString("userMobile", "").equals("")) {
                    Intent intent = new Intent(HelpCenterActivity.this, ContactUsActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HelpCenterActivity.this);
                    builder.setMessage(R.string.jpr)
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(HelpCenterActivity.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(res.getString(R.string.helpCan), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();

                }

            }
        });
        termsprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, TremsServiceActivity.class);
                startActivity(intent);
            }
        });
        getVersionInfo();
    }


    //get the current version number and name
    private void getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversion.setText(getString(R.string.helpv) + ": " + versionName + " " + getString(R.string.helpb) + " " + versionCode);
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
        Intent setIntent = new Intent(HelpCenterActivity.this, UserProfileSettingActivity.class);
        startActivity(setIntent);
        finish();
    }
}
