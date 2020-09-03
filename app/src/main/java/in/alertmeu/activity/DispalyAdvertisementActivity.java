package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


import in.alertmeu.R;
import in.alertmeu.adapter.AdvertisementListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementDAO;
import in.alertmeu.models.AlertTypeDAO;
import in.alertmeu.models.AletSubscriptionsDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;

public class DispalyAdvertisementActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private FloatingActionButton fab;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj, jsonLeadObj1;
    JSONArray jsonArray;
    String advertisementListResponse = "";
    List<AdvertisementDAO> data;
    AdvertisementListAdpter advertisementListAdpter;
    RecyclerView recyclerView;

    String alertResponse = "", alertSubResponse = "", bc_id = "", sub_id = "";
    ArrayList<AlertTypeDAO> alertTypeArrayList;
    ArrayList<AletSubscriptionsDAO> aletSubscriptionsDAOArrayList;
    EditText descriptionEdtTxt;
    String description = "";
    ImageView maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_main_layout);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();

        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.advertisementList);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        maps = (ImageView) findViewById(R.id.maps);

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new initMainCategorySpinner().execute();
        } else {

            Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(DispalyAdvertisementActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            prefEditor.putInt("n_count", 0);
            prefEditor.commit();
            setBadge();
        } else {

            Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }

    }

    private class initMainCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(DispalyAdvertisementActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

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
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            alertResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINCATEGORYBYUSER, jsonLeadObj);
            Log.i("resp", "alertResponse" + alertResponse);

            if (alertResponse.compareTo("") != 0) {
                if (isJSONValid(alertResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                alertTypeArrayList = new ArrayList<>();
                                JSONArray LeadSourceJsonObj = new JSONArray(alertResponse);
                                for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                    JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                    alertTypeArrayList.add(new AlertTypeDAO(json_data.getString("id"), json_data.getString("category_name")));

                                }

                                jsonArray = new JSONArray(alertResponse);

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
                            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (alertResponse.compareTo("") != 0) {

                Spinner spinnerCustom = (Spinner) findViewById(R.id.spinnerMainCategory);
                ArrayAdapter<AlertTypeDAO> adapter = new ArrayAdapter<AlertTypeDAO>(DispalyAdvertisementActivity.this, android.R.layout.simple_spinner_dropdown_item, alertTypeArrayList);
                spinnerCustom.setAdapter(adapter);
                spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        AlertTypeDAO alertTypeDAO = (AlertTypeDAO) parent.getSelectedItem();
                        //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                        bc_id = alertTypeDAO.getId();
                        new initSubCategorySpinner().execute();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });

            } else {
                // Close the progressdialog

            }
        }
    }

    //
    private class initSubCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(DispalyAdvertisementActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("bc_id", bc_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            alertSubResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSUBCATEGORYUSERBYID, jsonLeadObj1);
            Log.i("resp", "leadListResponse" + alertSubResponse);

            if (alertSubResponse.compareTo("") != 0) {
                if (isJSONValid(alertSubResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                aletSubscriptionsDAOArrayList = new ArrayList<>();
                                JSONArray LeadSourceJsonObj = new JSONArray(alertSubResponse);
                                for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                    JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                    aletSubscriptionsDAOArrayList.add(new AletSubscriptionsDAO(json_data.getString("id"), json_data.getString("subcategory_name")));
                                }

                                jsonArray = new JSONArray(alertSubResponse);

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
                            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (alertSubResponse.compareTo("") != 0) {
                Spinner spinnerCustom = (Spinner) findViewById(R.id.spinnerSubCategory);
                ArrayAdapter<AletSubscriptionsDAO> adapter = new ArrayAdapter<AletSubscriptionsDAO>(DispalyAdvertisementActivity.this, android.R.layout.simple_spinner_dropdown_item, aletSubscriptionsDAOArrayList);

                spinnerCustom.setAdapter(adapter);
                spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        AletSubscriptionsDAO aletSubscriptionsDAO = (AletSubscriptionsDAO) parent.getSelectedItem();
                        // Toast.makeText(getApplicationContext(), "Source ID:  " + aletSubscriptionsDAO.getId() + ",  Source Name : " + aletSubscriptionsDAO.getAlert_subscriptions_name(), Toast.LENGTH_SHORT).show();
                        sub_id = aletSubscriptionsDAO.getId();
                        new getAdvertisement().execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });

            } else {

            }
        }
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(DispalyAdvertisementActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("business_main_category_id", bc_id);
                        put("business_subcategory_id", sub_id);
                        put("latitude", preferences.getString("favlat", ""));
                        put("longitude", preferences.getString("favlong", ""));
                        put("distance","" + preferences.getInt("units_for_area", 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLADVERTISEMENT, jsonLeadObj);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            if (advertisementListResponse.compareTo("") != 0) {
                if (isJSONValid(advertisementListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {


                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseAdvertisementList(advertisementListResponse);
                                jsonArray = new JSONArray(advertisementListResponse);

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
                            //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (data.size() > 0) {
                advertisementListAdpter = new AdvertisementListAdpter(DispalyAdvertisementActivity.this, data);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(DispalyAdvertisementActivity.this));
                // Toast.makeText(getApplicationContext(), "" + data.size(), Toast.LENGTH_SHORT).show();

            } else {
                data.clear();
                advertisementListAdpter = new AdvertisementListAdpter(DispalyAdvertisementActivity.this, data);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(DispalyAdvertisementActivity.this));
            }
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(DispalyAdvertisementActivity.this, HomePageActivity.class);
        startActivity(setIntent);
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
}
