package com.uofthack.pebbleye;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class GeocodingActivity extends Activity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("146076d9-adeb-4ef2-a90f-c6197b0264e3");
    String address = "NOTHING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LocationListener ll = new mylocationlistener();
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        postData();
    }

    public void postData() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://peaceful-escarpment-8704.herokuapp.com/contact-nos");
        httppost.addHeader("accept", "application/json");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences sharedPrefSettings = this.getSharedPreferences(getString(R.string.preference_file_key_settings), Context.MODE_PRIVATE);

            Map<String, ?> keys = sharedPref.getAll();
            ArrayList<String> list = new ArrayList<String>();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                list.add(entry.getKey() + "%" + entry.getValue().toString());
            }
            Collections.sort(list);

            String sendMap = "";
            for (String element : list) {
                sendMap += "$" + element.split("%")[1] + "%" + element.split("%")[2];
            }
            Log.v("Map", sendMap);

            String message = sharedPrefSettings.getString("message", "") + " I am at " + address + ".";

            Log.v("Message", message);

            nameValuePairs.add(new BasicNameValuePair("map", sendMap));
            nameValuePairs.add(new BasicNameValuePair("message", message));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            Log.v("Response", response.getStatusLine().getStatusCode() + "");
        } catch (Exception e) {
        }
    }

    private class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION CHANGED", location.getLatitude() + "");
                Log.d("LOCATION CHANGED", location.getLongitude() + "");
                Geocoder g = new Geocoder(GeocodingActivity.this);
                try {
                    address = g.getFromLocation(location.getLatitude(), location.getLongitude(), 1).toString();
                } catch (Exception e) {
                }
                Log.d("LOCATION CHANGED", address);
            }
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
