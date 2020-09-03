package in.alertmeu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.AppUtils;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;

import in.alertmeu.utils.Utility;
import in.alertmeu.utils.WebClient;


public class ViewImageDescriptionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private JSONObject jsonLeadObj, jsonSchedule, jsonSchedule1;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "";
    String updateStatusResponse = "", imagePathResponse = "";

    String path = "", imgDes = "", like_status = "", image_flag = "0", dis_like_status = "", mobile_no = "", imagePath = "", starrating = "", userStar = "", add = "", id = "", qrCode = "", describe_limitations = "", description = "", emailid = "";
    ImageView image, like, calling, navigation, share, dislike, liked, disliked, takebarCode, email, emailh, callingh;
    TextView dis, totalLikes, address, clickTxt, validity, subCat, mainCat;
    //bar code
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    String fname = "";
    RatingBar ratingStar;
    //map

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context mContext;
    TextView mLocationMarkerText, titleTxt, limitation;
    Double lat, lng;
    LinearLayout limithideshow, deshideshow;
    // private LatLng mCenterLatLong;
    String localTime;
    Intent intent;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        intent = getIntent();
        mobile_no = intent.getStringExtra("mobile_no");
        emailid = intent.getStringExtra("email");
        if (!mobile_no.equals("") && emailid.equals("") || emailid.equals(" ")) {
            setContentView(R.layout.activity_view_image_description);
        } else if (!emailid.equals("") && mobile_no.equals("") || mobile_no.equals(" ")) {
            setContentView(R.layout.activity_view_image_descriptione);
        } else if (!emailid.equals("") && !mobile_no.equals("")) {
            setContentView(R.layout.activity_view_image_descriptionep);
        }
        mContext = this;
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        image = (ImageView) findViewById(R.id.image);
        like = (ImageView) findViewById(R.id.like);
        calling = (ImageView) findViewById(R.id.calling);
        callingh = (ImageView) findViewById(R.id.callingh);
        email = (ImageView) findViewById(R.id.email);
        emailh = (ImageView) findViewById(R.id.emailh);
        navigation = (ImageView) findViewById(R.id.navigation);
        share = (ImageView) findViewById(R.id.share);
        dislike = (ImageView) findViewById(R.id.dislike);
        liked = (ImageView) findViewById(R.id.liked);
        disliked = (ImageView) findViewById(R.id.disliked);
        takebarCode = (ImageView) findViewById(R.id.takebarCode);
        dis = (TextView) findViewById(R.id.dis);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        totalLikes = (TextView) findViewById(R.id.totalLikes);
        clickTxt = (TextView) findViewById(R.id.clickTxt);
        address = (TextView) findViewById(R.id.address);
        validity = (TextView) findViewById(R.id.validity);
        ratingStar = (RatingBar) findViewById(R.id.ratingStar);
        limithideshow = (LinearLayout) findViewById(R.id.limithideshow);
        deshideshow = (LinearLayout) findViewById(R.id.deshideshow);
        limitation = (TextView) findViewById(R.id.limitation);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        image.setVisibility(View.VISIBLE);

        id = intent.getStringExtra("id");
        titleTxt.setText(intent.getStringExtra("title"));
        imgDes = intent.getStringExtra("title");
        address.setText(intent.getStringExtra("business"));

        qrCode = intent.getStringExtra("qrCode");
        lat = Double.parseDouble(intent.getStringExtra("lat"));
        lng = Double.parseDouble(intent.getStringExtra("long"));
        imagePath = intent.getStringExtra("imagePath");
        describe_limitations = intent.getStringExtra("describe_limitations");
        mainCat.setText(res.getString(R.string.jadcat) + intent.getStringExtra("main_cat_name"));
        subCat.setText(res.getString(R.string.jadscat) + intent.getStringExtra("subcategory_name"));
        description = intent.getStringExtra("description");
        insertUserClick();
        getImagePath();
        if (intent.getStringExtra("likecnt").equals("0")) {
            totalLikes.setVisibility(View.GONE);
        } else if (intent.getStringExtra("likecnt").equals("1")) {
            totalLikes.setVisibility(View.VISIBLE);
            totalLikes.setText(intent.getStringExtra("likecnt") + " Like");
        } else {
            totalLikes.setVisibility(View.VISIBLE);
            totalLikes.setText(intent.getStringExtra("likecnt") + " Likes");
        }
        if (!description.equals("")) {
            deshideshow.setVisibility(View.VISIBLE);
            dis.setText(description);

        }
        if (!describe_limitations.equals("")) {
            limithideshow.setVisibility(View.VISIBLE);
            limitation.setText(describe_limitations);
        }
        if (mobile_no.trim().equals("")) {
            calling.setVisibility(View.GONE);
            callingh.setVisibility(View.VISIBLE);
        } else {
            callingh.setVisibility(View.GONE);
            calling.setVisibility(View.VISIBLE);

        }
        if (emailid.trim().equals("")) {
            email.setVisibility(View.GONE);
            emailh.setVisibility(View.VISIBLE);
        } else {
            emailh.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);

        }
       /* if (intent.getStringExtra("likecnt").equals("0")) {
            liked.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);


        } else if (Integer.parseInt(intent.getStringExtra("likecnt")) > 0) {

            like.setVisibility(View.GONE);
            liked.setVisibility(View.VISIBLE);
        }
        if (intent.getStringExtra("dislikecnt").equals("0")) {

            dislike.setVisibility(View.VISIBLE);
            disliked.setVisibility(View.GONE);

        } else if (Integer.parseInt(intent.getStringExtra("dislikecnt")) > 0) {
            dislike.setVisibility(View.GONE);
            disliked.setVisibility(View.VISIBLE);

        }*/

        validity.setText(parseTime(intent.getStringExtra("stime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("sdate")) + " to " + parseTime(intent.getStringExtra("etime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("edate")));
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.DisplayImage(imagePath, image);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }


        calling.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkAndRequestPermissions()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile_no));
                    if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailid));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AlertMeU");
                // emailIntent.putExtra(Intent.EXTRA_TEXT, body);
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

                startActivity(Intent.createChooser(emailIntent, "AlertMeU"));
            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + add + ")"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                    String mapsPackageName = "com.google.android.apps.maps";

                    i.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity");
                    i.setPackage(mapsPackageName);

                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }


            }
        });
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Uri bmpUri = getLocalBitmapUri(image);
                    if (bmpUri != null) {
                        // Construct a ShareIntent with link to image
                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on Alert MeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/apps/internaltest/4699689855537704233).\n\n" + imgDes + " \n" + description + " \n" + validity.getText().toString());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpg");
                        startActivity(Intent.createChooser(shareIntent, "Share with"));

                    } else {
                        // ...sharing failed, handle error
                    }
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }


            }
        });
        like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    like_status = "1";
                    if (like_status.equals("1")) {
                        disliked.setVisibility(View.GONE);
                        like.setVisibility(View.GONE);
                        dislike.setVisibility(View.VISIBLE);
                        liked.setVisibility(View.VISIBLE);
                    }
                    new initStatusUpdate().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }


            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    like_status = "0";
                    if (like_status.equals("0")) {
                        liked.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);
                        like.setVisibility(View.VISIBLE);
                        disliked.setVisibility(View.VISIBLE);

                    }
                    new initStatusUpdate().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }


            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jprqr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
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

                } else {

                    Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
                    intent1.putExtra("path", imagePath);
                    intent1.putExtra("flag", "1");
                    intent1.putExtra("title", intent.getStringExtra("title"));
                    intent1.putExtra("description", description);
                    intent1.putExtra("describe_limitations", describe_limitations);
                    intent1.putExtra("sdate", intent.getStringExtra("sdate"));
                    intent1.putExtra("stime", intent.getStringExtra("stime"));
                    intent1.putExtra("edate", intent.getStringExtra("edate"));
                    intent1.putExtra("etime", intent.getStringExtra("etime"));
                    intent1.putExtra("main_cat_name", intent.getStringExtra("main_cat_name"));
                    intent1.putExtra("subcategory_name", intent.getStringExtra("subcategory_name"));
                    startActivity(intent1);
                }

            }
        });
        if (preferences.getString("account_status", "").equals("1")) {
            takebarCode.setVisibility(View.GONE);
            clickTxt.setVisibility(View.GONE);

        } else {
            takebarCode.setVisibility(View.VISIBLE);
            clickTxt.setVisibility(View.VISIBLE);
        }
        takebarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showLoadingDialogQr(ViewImageDescriptionActivity.this);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            try {

                                bitmap = TextToImageEncode(preferences.getString("user_id", "") + "#" + id + "#" + qrCode);

                                // imageView.setImageBitmap(bitmap);
                                String root = Environment.getExternalStorageDirectory().toString();
                                File myDir = new File(root + "/AlertMeU");
                                myDir.mkdirs();
                                // Random generator = new Random();
                                // int n = 10000;
                                //  n = generator.nextInt(n);
                                fname = "Img-" + Integer.parseInt(preferences.getString("user_id", "") + id) + ".jpg";

                                File file = new File(myDir, fname);
                                if (file.exists()) file.delete();
                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    out.flush();
                                    out.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fname;
                            Bitmap bmp = BitmapFactory.decodeFile(filePath);

                            Utility.dismissLoadingDialog();
                            Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
                            intent1.putExtra("path", fname);
                            intent1.putExtra("flag", "2");
                            intent1.putExtra("title", intent.getStringExtra("title"));
                            intent1.putExtra("description", description);
                            intent1.putExtra("describe_limitations", describe_limitations);
                            intent1.putExtra("sdate", intent.getStringExtra("sdate"));
                            intent1.putExtra("stime", intent.getStringExtra("stime"));
                            intent1.putExtra("edate", intent.getStringExtra("edate"));
                            intent1.putExtra("etime", intent.getStringExtra("etime"));
                            intent1.putExtra("main_cat_name", intent.getStringExtra("main_cat_name"));
                            intent1.putExtra("subcategory_name", intent.getStringExtra("subcategory_name"));
                            startActivity(intent1);
                        }
                    }
                }, 1000);

            }
        });
        ratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    //Toast.makeText(getApplicationContext(),"rating"+rating,Toast.LENGTH_SHORT).show();
                    starrating = "" + rating;
                    new initRatingUpdate().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;


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
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

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
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


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

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            //LatLng latLong;

            MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(10f).tilt(70).build();
            mMap.addMarker(marker);
            // latLong = new LatLng(Double.parseDouble(preferences.getString("lat", "")), Double.parseDouble(preferences.getString("long", "")));
            //  CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(10f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", location.getLatitude()) + "," + "Long : " + String.format("%.06f", location.getLongitude()));
            // startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private class initStatusUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ViewImageDescriptionActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Updating Status...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("advertisment_id", id);
                        put("like_dislike_status", like_status);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERLIKES, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(updateStatusResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

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
                            Toast.makeText(ViewImageDescriptionActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewImageDescriptionActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();
                //  mProgressDialog.dismiss();


            } else {

                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                //    mProgressDialog.dismiss();
                ;
            }
            //    mProgressDialog.dismiss();

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

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("advertisment_id", id);
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_GETIMAGELOCPATH, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");
                            path = jsonObject.getString("path");
                            dis_like_status = jsonObject.getString("likestatus");
                            //userStar = jsonObject.getString("ratingstar");
                           /* if (!userStar.equals("null") && !userStar.equals("")) {
                                ratingStar.setRating(Float.parseFloat(userStar));
                            }*/

                            String a[] = path.split("#");

                            if (a[1].equals("0")) {
                                totalLikes.setVisibility(View.GONE);
                            } else if (a[1].equals("1")) {
                                totalLikes.setVisibility(View.VISIBLE);
                                totalLikes.setText(a[1] + " Like");
                            } else {
                                totalLikes.setVisibility(View.VISIBLE);
                                totalLikes.setText(a[1] + " Likes");
                            }
                            if (dis_like_status.equals("0")) {
                                liked.setVisibility(View.GONE);
                                dislike.setVisibility(View.GONE);
                                like.setVisibility(View.VISIBLE);
                                disliked.setVisibility(View.VISIBLE);

                            } else if (dis_like_status.equals("1")) {

                                disliked.setVisibility(View.GONE);
                                like.setVisibility(View.GONE);
                                liked.setVisibility(View.VISIBLE);
                                dislike.setVisibility(View.VISIBLE);
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

    public void insertUserClick() {

        jsonSchedule1 = new JSONObject() {
            {
                try {
                    put("advertisment_id", id);
                    put("user_id", preferences.getString("user_id", ""));
                    put("t_zone", localTime);
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_INSERTUSERVIEWCOUNTADS, jsonSchedule1);
                Log.i("resp", "imagePathResponse" + imagePathResponse);


            }
        });

        objectThread.start();

    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class initRatingUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ViewImageDescriptionActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.juds));
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
                        put("advertisment_id", preferences.getString("id", ""));
                        put("rating_star", starrating);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERRATINGSTAR, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(updateStatusResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

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
                            Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (status) {
                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();


            } else {

                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                ;
            }
            mProgressDialog.dismiss();

        }
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

    //
    private boolean checkAndRequestPermissions() {


        int permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions


                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mobile_no));
                        if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:in.alertmeu")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
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
}
