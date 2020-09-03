package in.alertmeu.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.javiersantos.appupdater.AppUpdater;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


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
import in.alertmeu.activity.BusinessExpandableListViewActivity;
import in.alertmeu.activity.HomePageActivity;
import in.alertmeu.activity.RegisterNGetStartActivity;
import in.alertmeu.activity.UserProfileSettingActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.adapter.ExpandableListAdapter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.AppUtils;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.FetchAddressIntentService;
import in.alertmeu.utils.GooglePlacesReadTask;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.PlacesDisplayTask;
import in.alertmeu.utils.WebClient;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG = "MAP LOCATION";
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    GoogleMap googleMap;
    double latitude = 0.0;
    double longitude = 0.0;
    double save_latitude = 0.0;
    double save_longitude = 0.0;
    private LatLng mCenterLatLong;
    TextView mLocationMarkerText;
    EditText nameOfThePlace;
    String placeName = "";
    ImageView sendData, showhide, areaUnit, refreshMap;
    LinearLayout head2, btnNext, locationMarker, ths, thsna;
    private JSONObject jsonLeadObj, jsonSchedule;
    ProgressDialog mProgressDialog;
    String placeAddResponse = "";
    boolean status;
    String msg = "", imagePathResponse = "";
    String title = "";
    int flag = 0;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    int days = 0;
    String provider;
    private LocationManager locationManager;
    String localTime;
    Resources res;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        res = getResources();
        AppUpdater appUpdater = new AppUpdater(getActivity());
        appUpdater.start();
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString("t_markers", "" + 0);
        prefEditor.commit();
        mLocationMarkerText = (TextView) v.findViewById(R.id.locationMarkertext);
        nameOfThePlace = (EditText) v.findViewById(R.id.nameOfThePlace);
        sendData = (ImageView) v.findViewById(R.id.sendData);
        showhide = (ImageView) v.findViewById(R.id.showhide);
        areaUnit = (ImageView) v.findViewById(R.id.areaUnit);
        refreshMap = (ImageView) v.findViewById(R.id.refreshMap);
        btnNext = (LinearLayout) v.findViewById(R.id.btnNext);
        head2 = (LinearLayout) v.findViewById(R.id.head2);
        ths = (LinearLayout) v.findViewById(R.id.ths);
        thsna = (LinearLayout) v.findViewById(R.id.thsna);
        locationMarker = (LinearLayout) v.findViewById(R.id.locationMarker);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        fragment.getMapAsync(this);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            getImagePath();
        } else {

            Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        try {
            PlacesDisplayTask.bindListener(new Listener() {
                @Override
                public void messageReceived(String messageText) {
                    if (status == true)
                        if (!messageText.equals("0")) {
                            thsna.setVisibility(View.GONE);
                            ths.setVisibility(View.VISIBLE);
                        } else {
                            ths.setVisibility(View.GONE);
                            thsna.setVisibility(View.VISIBLE);
                        }
                    /*if (AppStatus.getInstance(getActivity()).isOnline()) {
                        // Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();



                    } else {

                        Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

        } catch (Exception e) {
        }
        mResultReceiver = new AddressResultReceiver(new Handler());
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(getActivity())) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    placeName = nameOfThePlace.getText().toString().trim();
                    if (!placeName.equals("")) {
                        // Toast.makeText(getActivity(), "lat n long" + save_latitude + "," + save_longitude, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(res.getString(R.string.jdwant) + placeName + res.getString(R.string.jflwant))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new submitData().execute();
                                        dialog.cancel();


                                    }
                                })
                                .setNegativeButton(res.getString(R.string.jno), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        //  alert.setTitle("Adding Favorite place");
                        alert.show();


                    } else {
                        Toast.makeText(getActivity(), res.getString(R.string.jfpe)
                                , Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                            , Toast.LENGTH_SHORT).show();
                }
            }

        });

        HomePageActivity.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                // Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();
                title = messageText;
                updateMapData();

            }
        });
        refreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), res.getString(R.string.jrepre), Toast.LENGTH_LONG).show();
                getImagePath();
                updateMapData();


            }
        });
        showhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(res.getString(R.string.jfpe));
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                placeName = nameOfThePlace.getText().toString().trim();
                input.setText(placeName);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                // alertDialog.setIcon(R.drawable.msg_img);

                alertDialog.setPositiveButton(res.getString(R.string.xsub)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                placeName = input.getText().toString();
                                if (AppStatus.getInstance(getActivity()).isOnline()) {
                                    if (!placeName.equals("")) {
                                        new submitData().execute();
                                    } else {
                                        Toast.makeText(getActivity(), res.getString(R.string.jfpe), Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                                            , Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                alertDialog.setNegativeButton(res.getString(R.string.helpCan)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });

        areaUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(res.getString(R.string.jfd));
                final EditText input = new EditText(getActivity());
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                input.setText("" + preferences.getInt("units_for_area", 0));
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                // alertDialog.setIcon(R.drawable.msg_img);

                alertDialog.setPositiveButton(res.getString(R.string.jconfirm)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String entity = input.getText().toString();
                                prefEditor.putInt("units_for_area", Integer.parseInt(entity));
                                int km = Integer.parseInt(entity);
                                if (km < 1) {
                                    km = 1;
                                } else if (km > 999) {
                                    km = 999;
                                }
                                //int zoom = km;
                                prefEditor.commit();

                                prefEditor.commit();
                                Toast.makeText(getActivity(), res.getString(R.string.jsad) + entity + res.getString(R.string.jkm), Toast.LENGTH_LONG).show();
                                if (AppStatus.getInstance(getActivity()).isOnline()) {
                                    updateMapData();
                                    int zoom = (int) (Math.log(25000 / km) / Math.log(2));
                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
                                } else {

                                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                alertDialog.setNegativeButton(res.getString(R.string.helpCan)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

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
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    Intent intent = new Intent(getActivity(), BusinessExpandableListViewActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Toast.makeText(getActivity(),"total days"+days,Toast.LENGTH_SHORT).show();
        return v;
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        Log.d(TAG, "OnMapReady");
        googleMap = Map;

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                // googleMap.clear();

                try {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setCostAllowed(true);
                    criteria.setPowerRequirement(Criteria.POWER_LOW);
                    criteria.setAltitudeRequired(false);
                    criteria.setBearingRequired(false);
                    //API level 9 and up
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    provider = locationManager.getBestProvider(criteria, true);


                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                    save_latitude = mCenterLatLong.latitude;
                    save_longitude = mCenterLatLong.longitude;
                    // mLocationMarkerText.setText("Lat : " + String.format("%.06f", mCenterLatLong.latitude) + "," + "Long : " + String.format("%.06f", mCenterLatLong.longitude));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng latLng;
        if (preferences.getString("favloc", "").equals("0")) {
            latLng = new LatLng(latitude, longitude);
            prefEditor.putString("favlat", "" + latitude);
            prefEditor.putString("favlong", "" + longitude);
            prefEditor.commit();
        } else {
            latitude = Double.parseDouble(preferences.getString("favlat", ""));
            longitude = Double.parseDouble(preferences.getString("favlong", ""));
            latLng = new LatLng(latitude, longitude);
            updateMapData();
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        int km = preferences.getInt("units_for_area", 0);
        if (km < 1) {
            km = 1;
        } else if (km > 999) {
            km = 999;
        }
        int zoom = (int) (Math.log(25000 / km) / Math.log(2));
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng position = marker.getPosition(); //
                String a[] = marker.getSnippet().split("#");
               /* prefEditor.putString("id", marker.getTitle());
                prefEditor.putString("imagePath",a[0]);
                prefEditor.putString("qrCode", a[1]);
                prefEditor.putString("lat", "" + position.latitude);
                prefEditor.putString("long", "" + position.longitude);
                prefEditor.commit();*/
                //Toast.makeText(getActivity(), "Lat " + position.latitude + " " + "Long " + position.longitude,Toast.LENGTH_LONG).show();
                // LocationDetailsView locationDetailsView = new LocationDetailsView();
                //  locationDetailsView.show(getActivity().getSupportFragmentManager(), "locationDetailsView");
                Intent intent = new Intent(getActivity(), ViewImageDescriptionActivity.class);
                intent.putExtra("id", marker.getTitle());
                intent.putExtra("qrCode", a[1]);
                intent.putExtra("lat", "" + "" + position.latitude);
                intent.putExtra("long", "" + position.longitude);
                intent.putExtra("imagePath", a[0]);
                intent.putExtra("title", a[2]);
                intent.putExtra("description", a[3]);
                intent.putExtra("business", a[4] + "\n" + a[5]);
                intent.putExtra("stime", "" + a[6]);
                intent.putExtra("etime", "" + a[7]);
                intent.putExtra("sdate", a[8]);
                intent.putExtra("edate", a[9]);
                intent.putExtra("likecnt", a[10]);
                intent.putExtra("dislikecnt", a[11]);
                intent.putExtra("mobile_no", a[12]);
                intent.putExtra("describe_limitations", a[13]);
                intent.putExtra("main_cat_name", a[14]);
                intent.putExtra("subcategory_name", a[15]);
                intent.putExtra("email", a[16]);
                startActivity(intent);
                return true;
            }
        });
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //Toast.makeText(getActivity(), "zoom out", Toast.LENGTH_SHORT).show();

            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Toast.makeText(getApplicationContext(), "zoom in", Toast.LENGTH_SHORT).show();
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                //  googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                save_latitude = latLng.latitude;
                save_longitude = latLng.longitude;
                // Placing a marker on the touched position
                // googleMap.addMarker(markerOptions);
            }
        });

        updateMapData();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            @SuppressLint("RestrictedApi")
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {

            if (location != null)
                changeMap(location);

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {

            googleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            if (preferences.getString("favloc", "").equals("0")) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                prefEditor.putString("favlat", "" + latitude);
                prefEditor.putString("favlong", "" + longitude);
                prefEditor.commit();
            } else {
                latitude = Double.parseDouble(preferences.getString("favlat", ""));
                longitude = Double.parseDouble(preferences.getString("favlong", ""));
                latLong = new LatLng(latitude, longitude);
                updateMapData();
            }
            save_latitude = location.getLatitude();
            save_longitude = location.getLongitude();
            /*  CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(10f).tilt(70).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            int km = preferences.getInt("units_for_area", 0);
            if (km < 1) {
                km = 1;
            } else if (km > 999) {
                km = 999;
            }
            int zoom = (int) (Math.log(25000 / km) / Math.log(2));
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", latitude) + "," + "Long : " + String.format("%.06f", latitude));
            startIntentService(location);
            updateMapData();

        } else {
            Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateMapData() {
        googleMap.clear();
        if (latitude != 0.0) {
            // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            Object[] toPass = new Object[8];
            toPass[0] = googleMap;
            toPass[1] = "" + latitude;
            toPass[2] = "" + longitude;
            toPass[3] = title;
            toPass[4] = "" + preferences.getInt("units_for_area", 0);
            toPass[5] = "" + days;
            toPass[6] = getActivity();
            toPass[7] = preferences.getString("user_id", "");
            //latitude = location.getLatitude();
            // longitude = location.getLongitude();
            googlePlacesReadTask.execute(toPass);

        }
    }

    private class submitData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsd));
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
                        put("full_address", placeName);
                        put("latitude", save_latitude);
                        put("longitude", save_longitude);
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            placeAddResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERPLACES, jsonLeadObj);
            Log.i("resp", "placeAddResponse" + placeAddResponse);


            if (placeAddResponse.compareTo("") != 0) {
                if (isJSONValid(placeAddResponse)) {


                    try {

                        JSONObject jsonObject = new JSONObject(placeAddResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {


                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();


                }
            } else {

                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getActivity(), res.getString(R.string.jpadds), Toast.LENGTH_SHORT).show();

                nameOfThePlace.setText("");
                // Close the progressdialog
                mProgressDialog.dismiss();

            } else {
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        Context ctx = (Context) HomeFragment.this.getActivity();
        ctx.startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    private void displayAddressOutput() {

        if (mAreaOutput != null) {
            mLocationMarkerText.setVisibility(View.VISIBLE);
            mLocationMarkerText.setText(mAreaOutput);
            nameOfThePlace.setText(mAreaOutput);
        } else {
            nameOfThePlace.setText("");
            mLocationMarkerText.setVisibility(View.GONE);
        }


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
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(imagePathResponse);
                                status = jsonObject.getBoolean("status");
                                msg = jsonObject.getString("message");
                                if (status) {
                                    btnNext.setVisibility(View.GONE);
                                    head2.setVisibility(View.VISIBLE);
                                    locationMarker.setVisibility(View.VISIBLE);
                                    //  ths.setVisibility(View.VISIBLE);
                                } else {
                                    head2.setVisibility(View.GONE);
                                    locationMarker.setVisibility(View.GONE);
                                    ths.setVisibility(View.GONE);
                                    thsna.setVisibility(View.GONE);
                                    btnNext.setVisibility(View.VISIBLE);
                                }


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {
                }

            }
        });

        objectThread.start();

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
