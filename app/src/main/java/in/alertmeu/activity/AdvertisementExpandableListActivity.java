package in.alertmeu.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import in.alertmeu.R;
import in.alertmeu.adapter.CatMianSubAdapter;

import android.widget.ExpandableListAdapter;

import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementDAO;
import in.alertmeu.models.ExAdvertisementDAO;
import in.alertmeu.models.ExMainSubCatDAO;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.WebClient;

public class AdvertisementExpandableListActivity extends AppCompatActivity {
    ExpandableListView mAdvertisementsListView;
    List<ExMainSubCatDAO> mainSubCatDAOArrayList;
    ExpandableListAdapter mCatMianSubAdapter;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonLeadObj, jsonLeadObj1, jsonSchedule;
    JSONArray jsonArray;
    ProgressDialog mProgressDialog;
    String myPlaceListResponse = "", advertisementListResponse = "", imagePathResponse = "";
    boolean status;
    List<MainCatModeDAO> data;
    List<MainCatModeDAO> data1;
    MainCatModeDAO mainCatModeDAO;
    List<AdvertisementDAO> advertisementDAOList;

    ExMainSubCatDAO exMainSubCatDAO;
    private ImageView settings, maps, refreshData;
    private ImageView naviBtn;
    String localTime;
    int days = 0;

    LinearLayout noadshid;
    LinearLayout btnshopPrec, sphs;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_advertisement_expandable_list);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        settings = (ImageView) findViewById(R.id.settings);
        maps = (ImageView) findViewById(R.id.maps);
        naviBtn = (ImageView) findViewById(R.id.naviBtn);
        refreshData = (ImageView) findViewById(R.id.refreshData);
        data = new ArrayList<>();
        data1 = new ArrayList<>();
        advertisementDAOList = new ArrayList<>();
        noadshid = (LinearLayout) findViewById(R.id.noadshid);
        sphs = (LinearLayout) findViewById(R.id.sphs);
        btnshopPrec = (LinearLayout) findViewById(R.id.btnshopPrec);
        mainSubCatDAOArrayList = new ArrayList<ExMainSubCatDAO>();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        mAdvertisementsListView = (ExpandableListView) findViewById(R.id.list_advertisement);

        // preparing list data
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            prefEditor.putInt("n_count", 0);
            prefEditor.commit();
            setBadge();
            new getMainCategoryList().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(AdvertisementExpandableListActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(AdvertisementExpandableListActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(AdvertisementExpandableListActivity.this, UserProfileSettingActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnshopPrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(AdvertisementExpandableListActivity.this).isOnline()) {
                    Intent intent = new Intent(AdvertisementExpandableListActivity.this, BusinessExpandableListViewActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(AdvertisementExpandableListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (AppStatus.getInstance(AdvertisementExpandableListActivity.this).isOnline()) {
            getImagePath();
        } else {

            Toast.makeText(AdvertisementExpandableListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        if (preferences.getString("today", "").equals("true")) {
            days += 1;
        }
        if (preferences.getString("tomorrow", "").equals("true")) {
            days += 2;
        }
        if (preferences.getString("oneweek", "").equals("true")) {
            days = 7;
        }
        if (preferences.getString("twoweeks", "").equals("true")) {
            days = 14;
        }
        refreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    new getMainCategoryList().execute();
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
     * Preparing the list data
     */


    private class getMainCategoryList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AdvertisementExpandableListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (days == 0) {
                days = 1;
            }
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("latitude", preferences.getString("favlat", ""));
                        put("longitude", preferences.getString("favlong", ""));
                        put("distance", "" + preferences.getInt("units_for_area", 0));
                        put("t_zone", localTime);
                        put("currentdays", days);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINCATEGORYBYUSER, jsonLeadObj);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLNEWADVERTISEMENT, jsonLeadObj1);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                data.clear();
                                advertisementDAOList.clear();
                                mainSubCatDAOArrayList.clear();
                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseNewMainCatList(myPlaceListResponse);
                                advertisementDAOList = jsonHelper.parseAdvertisementList(advertisementListResponse);
                                jsonArray = new JSONArray(myPlaceListResponse);

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
                            Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            //  data.retainAll(advertisementDAOList);
            // create ArrayList list1
            ArrayList<String> list1 = new ArrayList<String>();
            list1.clear();
            data1.clear();
            for (int i = 0; i < advertisementDAOList.size(); i++) {
                list1.add(advertisementDAOList.get(i).getBusiness_main_category_id());
            }
            for (MainCatModeDAO temp : data) {

                // Check if theis element is
                // present in list2 or not
                if (list1.contains(temp.getId())) {

                    // Since present, add it to list3
                    Log.d("value", temp.getCategory_name());
                    mainCatModeDAO = new MainCatModeDAO();
                    mainCatModeDAO.setId(temp.getId());
                    if (preferences.getString("ulang", "").equals("en")) {
                        mainCatModeDAO.setCategory_name(temp.getCategory_name());
                    } else if (preferences.getString("ulang", "").equals("hi")) {
                        mainCatModeDAO.setCategory_name(temp.getCategory_name_hindi());
                    }
                    data1.add(mainCatModeDAO);
                }
            }
            if (data1.size() > 0) {
                noadshid.setVisibility(View.GONE);
                   /* for (int j = 0; j < advertisementDAOList.size(); j++) {
                        if (!data.contains(advertisementDAOList.get(j).getMain_cat_name())) {
                           data.remove(1);
                        }
                    }
*/

                for (int i = 0; i < data1.size(); i++) {
                    List<ExAdvertisementDAO> exAdvertisementDAOList = new ArrayList<ExAdvertisementDAO>();
                    for (int j = 0; j < advertisementDAOList.size(); j++) {
                        if (data1.get(i).getId().equals(advertisementDAOList.get(j).getBusiness_main_category_id())) {
                            exAdvertisementDAOList.add(new ExAdvertisementDAO(advertisementDAOList.get(j).getTitle(), advertisementDAOList.get(j).getDescription(), advertisementDAOList.get(j).getDescribe_limitations(), advertisementDAOList.get(j).getOriginal_image_path(), advertisementDAOList.get(j).getModify_image_path(), advertisementDAOList.get(j).getMain_cat_name(), advertisementDAOList.get(j).getSubcategory_name(), advertisementDAOList.get(j).getId(), advertisementDAOList.get(j).getLatitude(), advertisementDAOList.get(j).getLongitude(), advertisementDAOList.get(j).getRq_code(), advertisementDAOList.get(j).getBusiness_name(), advertisementDAOList.get(j).getAddress(), advertisementDAOList.get(j).getS_time(), advertisementDAOList.get(j).getE_time(), advertisementDAOList.get(j).getS_date(), advertisementDAOList.get(j).getE_date(), advertisementDAOList.get(j).getMobile_no(), advertisementDAOList.get(j).getLikecnt(), advertisementDAOList.get(j).getDislikecnt(), advertisementDAOList.get(j).getBusiness_number(), advertisementDAOList.get(j).getBusiness_email()));

                        }
                    }
                    exMainSubCatDAO = new ExMainSubCatDAO(data1.get(i).getCategory_name(), exAdvertisementDAOList);
                    mainSubCatDAOArrayList.add(exMainSubCatDAO);

                }
                Log.d("Logs", "" + mainSubCatDAOArrayList.size());
                mCatMianSubAdapter = new CatMianSubAdapter(AdvertisementExpandableListActivity.this, mainSubCatDAOArrayList);
                mAdvertisementsListView.setAdapter(mCatMianSubAdapter);
                //expand all the Groups
                expandAll();

            } else {
                //noadshid.setVisibility(View.VISIBLE);
            }

        }
    }

    //method to expand all groups
    private void expandAll() {
        int count = mCatMianSubAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mAdvertisementsListView.expandGroup(i);
        }
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
    //count show on app icon

    public void setBadge() {
        String launcherClassName = getLauncherClassName(getApplicationContext());
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", 0);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        sendBroadcast(intent);
    }

    public String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("user_id", preferences.getString("user_id", ""));
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_CUCBID, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                sphs.setVisibility(View.GONE);
                                noadshid.setVisibility(View.VISIBLE);
                            } else {
                                noadshid.setVisibility(View.GONE);
                                sphs.setVisibility(View.VISIBLE);
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
    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(AdvertisementExpandableListActivity.this, HomePageActivity.class);
        startActivity(setIntent);
        finish();
    }
}
