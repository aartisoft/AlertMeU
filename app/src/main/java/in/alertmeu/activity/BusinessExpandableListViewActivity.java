package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.adapter.ExpandableListAdapter;
import in.alertmeu.adapter.MainCatListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;

public class BusinessExpandableListViewActivity extends AppCompatActivity {
    private LinkedHashMap<String, MainCatModeDAO> subjects = new LinkedHashMap<String, MainCatModeDAO>();
    private ArrayList<MainCatModeDAO> deptList = new ArrayList<MainCatModeDAO>();

    private ExpandableListAdapter listAdapter;
    private ExpandableListView simpleExpandableListView;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonLeadObj, jsonSchedule;
    JSONArray jsonArray;
    String myPlaceListResponse = "", subCategoryListResponse = "", imagePathResponse = "";
    List<MainCatModeDAO> data;
    List<SubCatModeDAO> subCatModeDAOList;
    ProgressDialog mProgressDialog;
    LinearLayout btnNext, shmsg;
    boolean status;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_business_expandable_list_view);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        shmsg = (LinearLayout) findViewById(R.id.shmsg);
        // preparing list data
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new getMainCategoryList().execute();
            getImagePath();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        //get reference of the ExpandableListView
        simpleExpandableListView = (ExpandableListView) findViewById(R.id.simpleExpandableListView);


        // setOnChildClickListener listener for child row click
        simpleExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //get the group header
                MainCatModeDAO headerInfo = deptList.get(groupPosition);
                //get the child info
                SubCatModeDAO detailInfo = headerInfo.getList().get(childPosition);
                //display it or do something with it
                //  Toast.makeText(getBaseContext(), " Clicked on :: " + headerInfo.getId() + "/" + detailInfo.getId(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
        // setOnGroupClickListener listener for group heading click
        simpleExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //get the group header
                MainCatModeDAO headerInfo = deptList.get(groupPosition);
                //display it or do something with it
                // Toast.makeText(getBaseContext(), " Header is :: " + headerInfo.getCategory_name(), Toast.LENGTH_LONG).show();

                return false;
            }
        });
        ExpandableListAdapter.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    getImagePath();
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(BusinessExpandableListViewActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            simpleExpandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            simpleExpandableListView.collapseGroup(i);
        }
    }


    /*
     * Preparing the list data
     */


    private class getMainCategoryList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BusinessExpandableListViewActivity.this);
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

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLUSERMAINCATEGORY, jsonLeadObj);
            subCategoryListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLUSERSMAINSUBCATEGORY, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                data = new ArrayList<>();
                                subCatModeDAOList = new ArrayList<>();
                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseMainCatList(myPlaceListResponse);
                                subCatModeDAOList = jsonHelper.parseSubCatList(subCategoryListResponse);
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
            if (data.size() > 0) {

                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < subCatModeDAOList.size(); j++) {
                        if (data.get(i).getId().equals(subCatModeDAOList.get(j).getBc_id())) {
                            addProduct(subCatModeDAOList.get(j).getId(), subCatModeDAOList.get(j).getBc_id(), subCatModeDAOList.get(j).getChecked_status(), data.get(i).getCategory_name(), subCatModeDAOList.get(j).getSubcategory_name(), data.get(i).getChecked_status(), data.get(i).getCategory_name_hindi(), subCatModeDAOList.get(j).getSubcategory_name_hindi());
                        }

                    }

                }
                // create the adapter by passing your ArrayList data
                listAdapter = new ExpandableListAdapter(BusinessExpandableListViewActivity.this, deptList);
                // attach the adapter to the expandable list view
                simpleExpandableListView.setAdapter(listAdapter);

                //expand all the Groups
                // expandAll();
            }


        }
    }

    //here we maintain our products in various departments
    private int addProduct(String id, String b_id, String checked_status, String department, String product, String mchecked_status, String cathi, String csathi) {

        int groupPosition = 0;

        //check the hash map if the group already exists
        MainCatModeDAO headerInfo = subjects.get(department);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new MainCatModeDAO();
            headerInfo.setCategory_name(department);
            headerInfo.setCategory_name_hindi(cathi);
            headerInfo.setChecked_status(mchecked_status);
            subjects.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<SubCatModeDAO> productList = headerInfo.getList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        SubCatModeDAO detailInfo = new SubCatModeDAO();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setSubcategory_name(product);
        detailInfo.setSubcategory_name_hindi(csathi);
        detailInfo.setChecked_status(checked_status);
        detailInfo.setId(id);
        detailInfo.setBc_id(b_id);
        productList.add(detailInfo);
        headerInfo.setList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
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
                                shmsg.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                            } else {
                                btnNext.setVisibility(View.GONE);
                                shmsg.setVisibility(View.VISIBLE);
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
}