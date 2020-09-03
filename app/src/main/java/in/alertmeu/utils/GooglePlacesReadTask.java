package in.alertmeu.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    JSONObject jsonLeadObj;
    String latitude;
    String longitude;
    String title;
    String distance;
    String days;
    String user_id;
    Context context;
    String localTime;
    @Override
    protected String doInBackground(Object... inputObj) {
        try {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                    Locale.getDefault());
            Date currentLocalTime = calendar.getTime();

            DateFormat date = new SimpleDateFormat("ZZZZZ",Locale.getDefault());
            localTime = date.format(currentLocalTime);
            googleMap = (GoogleMap) inputObj[0];
            latitude = (String) inputObj[1];
            longitude = (String) inputObj[2];
            title = (String) inputObj[3];
            distance = (String) inputObj[4];
            days = (String) inputObj[5];
            context = (Context) inputObj[6];
            user_id = (String) inputObj[7];
            if (days.equals("0")) {
                days = "1";
            }
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("latitude", latitude);
                        put("longitude", longitude);
                        put("title", title);
                        put("distance", distance);
                        put("currentdays", days);
                        put("user_id", user_id);
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            Log.d("resp", "" + jsonLeadObj);
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLLOCATIONDATA, jsonLeadObj);

        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[3];
        toPass[0] = googleMap;
        toPass[1] = result;
        toPass[2] = context;
        placesDisplayTask.execute(toPass);
    }
}
