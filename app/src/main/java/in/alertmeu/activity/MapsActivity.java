package in.alertmeu.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import in.alertmeu.R;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;
import in.alertmeu.view.LocationDetailsView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private GoogleMap mMap;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj;
    JSONArray jsonArray;
    String locationListResponse = "";
    List<LocationDAO> data;
    private ImageView rfrsh;
    int i;

    private static final String TAG = "MapsActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;

    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        data = new ArrayList<>();
        rfrsh = (ImageView) findViewById(R.id.refresh);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //refreshing details of user on map
        rfrsh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
                    new getLocationList().execute();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }

            }
        });

        LocationDetailsView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
                    new getLocationList().execute();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        for (i = 0; i < data.size(); i++) {
           /* if (data.get(i).getFlag_map().equals("1")) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).title(data.get(i).getId()).snippet(data.get(i).getCreate_at());
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).zoom(10).build();
                Marker marker1 = mMap.addMarker(marker);
               // marker1.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).title(data.get(i).getId()).snippet(data.get(i).getCreate_at());
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).zoom(10).build();
                Marker marker1 = mMap.addMarker(marker);
              //  marker1.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }*/

            if (data.get(i).getFlag_map().equals("1")) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).title(data.get(i).getId());
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).zoom(10).build();
                Marker marker1 = mMap.addMarker(marker);
                //marker1.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            } else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).title(data.get(i).getId());
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(data.get(i).getLatitude()), Double.parseDouble(data.get(i).getLongitude()))).zoom(10).build();
                Marker marker1 = mMap.addMarker(marker);
                // marker1.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                // mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));


            }


        }
       /* mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                prefEditor.putString("id", marker.getTitle());
                prefEditor.commit();

                // LocationDetailsView locationDetailsView = new LocationDetailsView();
                // locationDetailsView.show(getSupportFragmentManager(), "locationDetailsView");

            }
        });*/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                prefEditor.putString("id", marker.getTitle());
                prefEditor.commit();
                LocationDetailsView locationDetailsView = new LocationDetailsView();
                locationDetailsView.show(getSupportFragmentManager(), "locationDetailsView");
                return true;
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                // Toast.makeText(getApplicationContext(), "zoom out", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Toast.makeText(getApplicationContext(), "zoom in", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //
    private class getLocationList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(MapsActivity.this);
            // Set progressdialog title
            //mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            // mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("latitude", latitude);
                        put("longitude", longitude);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            locationListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLLOCATIONDATA, jsonLeadObj);
            Log.i("resp", "locationListResponse" + locationListResponse);

            if (locationListResponse.compareTo("") != 0) {
                if (isJSONValid(locationListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                data.clear();
                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseLocationList(locationListResponse);
                                jsonArray = new JSONArray(locationListResponse);

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
                            //  Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
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
            if (data.size() > 0) {
                // Close the progressdialog
                // mProgressDialog.dismiss();
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
            } else {
                // Close the progressdialog
                // mProgressDialog.dismiss();

            }
        }
    }

    protected boolean isJSONValid(String locationListResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(locationListResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(locationListResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

            //mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();

            if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                new getLocationList().execute();
            } else {

                Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        // mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
        // latitude = location.getLatitude();
        //  longitude = location.getLongitude();
        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            //  new getLocationList().execute();
        } else {

            // Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }


}
