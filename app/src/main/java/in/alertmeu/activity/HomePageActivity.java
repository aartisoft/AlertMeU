package in.alertmeu.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.alertmeu.Fragment.DealsFragment;
import in.alertmeu.Fragment.HomeFragment;
import in.alertmeu.Fragment.IncidentsFragment;
import in.alertmeu.Fragment.MessageFragment;
import in.alertmeu.R;
import in.alertmeu.adapter.ViewPagerAdapter;
import in.alertmeu.models.DealsNameDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;

public class HomePageActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private LinearLayout showDataOnMap, generateqrCodeData, scanqrcode, paypalPayment, contactsRead;
    private LocationManager locationManager;
    private ImageView upLoadData, settings, maps, lists;
    private ImageView naviBtn;

    AutoCompleteTextView searchAddress;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    //

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    //Fragments
    HomeFragment homeFragment;
    MessageFragment messageFragment;
    IncidentsFragment incidentsFragment;
    DealsFragment dealsFragment;

    int[] tabTitle = {R.drawable.tab_home_icon, R.drawable.tab_deal_icon, R.drawable.tab_alert_icon, R.drawable.tab_camera_icon};
    int[] unreadCount = {0, 2, 5, 0};

    private static Listener mListener;

    JSONObject jsonSchedule;
    String dealsResponse = "";
    public List<DealsNameDAO> dealsNameDAOList;
    DealsNameDAO dealsNameDAO;
    public ArrayAdapter<DealsNameDAO> dealsNameDAOArrayAdapter;
    TextView fabCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        searchAddress = (AutoCompleteTextView) findViewById(R.id.searchAddress);
        upLoadData = (ImageView) findViewById(R.id.upLoadData);
        if (preferences.getString("favloc", "").equals("")) {
            prefEditor.putString("favloc", "0");
            prefEditor.commit();
        }
        /*fabCounter = (TextView) findViewById(R.id.fabCounter);
        if (!preferences.getString("t_markers", "").equals("")) {
            if (Integer.parseInt(preferences.getString("t_markers", "")) > 1) {
               // fabCounter.setVisibility(View.VISIBLE);
              //  fabCounter.setText(preferences.getString("t_markers", ""));

            }
        }*/
        showDataOnMap = (LinearLayout) findViewById(R.id.showDataOnMap);
        generateqrCodeData = (LinearLayout) findViewById(R.id.generateqrCodeData);
        scanqrcode = (LinearLayout) findViewById(R.id.scanqrcode);
        paypalPayment = (LinearLayout) findViewById(R.id.paypalPayment);
        contactsRead = (LinearLayout) findViewById(R.id.contactsRead);
        naviBtn = (ImageView) findViewById(R.id.naviBtn);
        settings = (ImageView) findViewById(R.id.settings);
        maps = (ImageView) findViewById(R.id.maps);
        lists = (ImageView) findViewById(R.id.lists);
        dealsNameDAOList = new ArrayList<DealsNameDAO>();
        upLoadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (checkLocation()) //check whether location service is enable or not in your  phone
                    {
                        Intent intent = new Intent(HomePageActivity.this, UploadDataOnServerActivity.class);
                        startActivity(intent);
                    }

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });

        showDataOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (checkLocation()) //check whether location service is enable or not in your  phone
                    {
                        Intent intent = new Intent(HomePageActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });


        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(HomePageActivity.this, UserProfileSettingActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });


        lists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    lists.setVisibility(View.GONE);
                    maps.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(HomePageActivity.this, AdvertisementExpandableListActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAddress.getText())) {


                        getchannelPartnerSelect(searchAddress.getText().toString());

                    } else {
                        mListener.messageReceived("");

                    }
                }
                return false;
            }
        });

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        try {
            setupTabIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, true);
                //tabLayout.getTabAt(position).select();
                // Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                if (position == 3) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        if (checkLocation()) //check whether location service is enable or not in your  phone
                        {
                            Intent intent = new Intent(getApplicationContext(), UploadDataOnServerActivity.class);
                            startActivity(intent);
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        // messageFragment = new MessageFragment();
        // incidentsFragment = new IncidentsFragment();
        //  dealsFragment = new DealsFragment();
        adapter.addFragment(homeFragment, "Home");
        // adapter.addFragment(messageFragment, "Message");
        //  adapter.addFragment(incidentsFragment, "Incidents");
        //  adapter.addFragment(dealsFragment, "Deals");
        viewPager.setAdapter(adapter);
        // viewPager.setCurrentItem(0);
    }

    private View prepareTabView(int pos) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ImageView tv_title = (ImageView) view.findViewById(R.id.tv_title);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_count);
        tv_title.setBackgroundResource(tabTitle[pos]);
        if (unreadCount[pos] > 0) {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText("" + unreadCount[pos]);
        } else
            tv_count.setVisibility(View.GONE);


        return view;
    }

    private void setupTabIcons() {

        for (int i = 0; i < tabTitle.length; i++) {
            /*TabLayout.Tab tabitem = tabLayout.newTab();
            tabitem.setCustomView(prepareTabView(i));
            tabLayout.addTab(tabitem);*/

            tabLayout.getTabAt(i).setCustomView(prepareTabView(i));
        }


    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
    }

    public void getchannelPartnerSelect(final String channelPartnerSelect) {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("Prefixtext", channelPartnerSelect);

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
                //SEND RESPONSE
                dealsResponse = serviceAccess.SendHttpPost(Config.URL_GETALLDEALSBYPREFIX, jsonSchedule);
                Log.i("resp", "loginResponse" + dealsResponse);


                try {
                    JSONArray callArrayList = new JSONArray(dealsResponse);
                    dealsNameDAOList.clear();
                    // user_id="";
                    for (int i = 0; i < callArrayList.length(); i++) {
                        dealsNameDAO = new DealsNameDAO();
                        JSONObject cityJsonObject = callArrayList.getJSONObject(i);
                        dealsNameDAOList.add(new DealsNameDAO(cityJsonObject.getString("id"), cityJsonObject.getString("title")));

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        dealsNameDAOArrayAdapter = new ArrayAdapter<DealsNameDAO>(getApplicationContext(), R.layout.item, dealsNameDAOList);
                        searchAddress.setAdapter(dealsNameDAOArrayAdapter);
                        if (dealsNameDAOList.size() < 40)
                            searchAddress.setThreshold(1);
                        else searchAddress.setThreshold(2);
                        dealsNameDAOArrayAdapter.notifyDataSetChanged();
                        searchAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                                // String s = parent.getItemAtPosition(i).toString();
                                DealsNameDAO student = (DealsNameDAO) parent.getAdapter().getItem(i);
                                mListener.messageReceived(student.getTitile());
                                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            }
                        });
                        dealsNameDAOArrayAdapter.notifyDataSetChanged();

                    }
                });


            }
        });

        objectThread.start();

    }

    //
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
