package in.alertmeu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.LocationDAO;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<LocationDAO>> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String googlePlacesJson;
    GoogleMap googleMap;
    private static Listener mListener;

    @Override
    protected List<LocationDAO> doInBackground(Object... inputObj) {
        Context context = (Context) inputObj[2];
        List<LocationDAO> data = null;
        preferences = context.getSharedPreferences("Prefrence", context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        JsonHelper jsonHelper = new JsonHelper();


        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = (String) inputObj[1];
            if (isJSONValid(googlePlacesJson)) {
                data = jsonHelper.parseLocationList(googlePlacesJson);
            }

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(List<LocationDAO> list) {
        // googleMap.clear();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            List<LocationDAO> listData = list;
            double lat = Double.parseDouble(listData.get(i).getLatitude());
            double lng = Double.parseDouble(listData.get(i).getLongitude());
            // String placeName = googlePlace.get("place_name");
            // String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(listData.get(i).getId());
            markerOptions.snippet(listData.get(i).getPath() + "#" + listData.get(i).getRq_code() + "#" + listData.get(i).getTitle() + "#" + listData.get(i).getDescription() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getS_time() + "#" + listData.get(i).getE_time() + "#" + listData.get(i).getS_date() + "#" + listData.get(i).getE_date() + "#" + listData.get(i).getLikecnt() + "#" + listData.get(i).getDislikecnt() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getDescribe_limitations() + "#" + listData.get(i).getBusiness_main_category() + "#" + listData.get(i).getBusiness_subcategory() + "#" + listData.get(i).getBusiness_email());
            googleMap.addMarker(markerOptions);

        }
        if (list.size() > 0) {
            mListener.messageReceived("" + 1);
        } else {
            mListener.messageReceived("" + 0);
        }
        prefEditor.putString("t_markers", "" + list.size());
        prefEditor.commit();
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
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

