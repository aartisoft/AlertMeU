package in.alertmeu.utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.MediaPlayer;

import android.support.v4.content.WakefulBroadcastReceiver;

import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;


import org.json.JSONArray;

import org.json.JSONObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MyBroadcastReceiver extends WakefulBroadcastReceiver  {
    MediaPlayer mp;
    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    SharedPreferences preference;
    SharedPreferences.Editor prefEditor;

    JSONObject jsonReqObj;
    JSONArray jsonArray;

    String status = "";
    String localTime;
    String time24;
    @Override
    public void onReceive(Context context, Intent intent) {
        preference = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preference.edit();
        gps = new GPSTracker(context);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        Calendar now = Calendar.getInstance();
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        if (!preference.getString("user_id", "").equals("")) {
            getLocation(context, intent);
        }
        int a = now.get(Calendar.AM_PM);
        /*if (a == Calendar.AM) {
            System.out.println("AM" + now.get(Calendar.HOUR));
            String time = "AM" + now.get(Calendar.HOUR);
            if (time.endsWith("AM9") || time.endsWith("AM9") || time.endsWith("AM10") || time.endsWith("AM11")) {
                System.out.println("AM............" + now.get(Calendar.HOUR));
              //  Toast.makeText(context, "helo"+longitude, Toast.LENGTH_SHORT).show();
                getLocation(context, intent);

            } else {
                System.out.println("AM............");
            }

        } else if (a == Calendar.PM) {

            System.out.println("PM" + now.get(Calendar.HOUR));
            String time = "PM" + now.get(Calendar.HOUR);
            if (time.endsWith("PM0") || time.endsWith("PM1") || time.endsWith("PM2") || time.endsWith("PM3") || time.endsWith("PM4") || time.endsWith("PM5") || time.endsWith("PM6") || time.endsWith("PM7") || time.endsWith("PM7") || time.endsWith("PM8")) {
                System.out.println("PM........" + now.get(Calendar.HOUR));
               // Toast.makeText(context, "helo"+longitude, Toast.LENGTH_SHORT).show();
                 getLocation(context, intent);
                // getNewLeadraw(context, intent);
            } else {
                System.out.println("PM............");
            }
        }*/

        //getNewLeadraw(context,intent);

    }



   private void getLocation(Context context, Intent intent) {

        // create class object
        gps = new GPSTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            final String dateToStr = format.format(today);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            final String dateToStr1 = format1.format(today);
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");
            final String dateToStr2 = format2.format(today);
            String now = new SimpleDateFormat("hh:mm aa").format(new java.util.Date().getTime());
            System.out.println("time in 12 hour format : " + now);
            SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");


            try {
                time24 = outFormat.format(inFormat.parse(now));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            jsonReqObj = new JSONObject() {
                        {
                            try {

                                put("user_id", preference.getString("user_id", ""));
                                put("latitude", latitude);
                                put("longitude", longitude);
                                put("create_at", dateToStr);
                                put("entry_date", dateToStr1);
                                put("time", time24);
                                put("t_zone", localTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("json exception", "json exception" + e);
                            }
                        }
                    };

                    Thread objectThread1 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            WebClient serviceAccess = new WebClient();
                            Log.i("json", "json" + jsonReqObj);
                            String TrackResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERLOCATION, jsonReqObj);
                            Log.i("TrackResponse", TrackResponse);

                        }
                    });

                    objectThread1.start();

                }
            }




    }

   /* private void update() {
        LocationDAO.deleteAll(LocationDAO.class);
        System.out.println("deleted------>");
    }

    private void getNewLeadraw(final Context context, final Intent intent) {
        final JSONObject jsonReqObj = new JSONObject() {
            {
                try {

                    put(Constant.LEAD_USER_ID, preference.getString("u_id", ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };

        Thread objectThread1 = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonReqObj);
                String newLeadrawResponse = serviceAccess.SendHttpPost(Config.URL_GET_NEWLEAD_RAW, jsonReqObj);
                Log.i("newLeadrawResponse---->", newLeadrawResponse);
                if (true) {
                   *//* ComponentName comp = new ComponentName(context.getPackageName(), NotificationService.class.getName());
                    startWakefulService(context, (intent.setComponent(comp)));
                    setResultCode(Activity.RESULT_OK);*//*
                } else {


                }

            }
        });

        objectThread1.start();

    }*/


