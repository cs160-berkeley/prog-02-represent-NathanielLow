package com.example.nathaniel.represent;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Nathaniel on 2/24/2016.
 */
public class PhoneToWatchService extends Service {
    private GoogleApiClient mApiClient;
    private Geocoder geocoder;
    private String zipCode;
    private String county;
    private String romney;
    private String obama;
    private String voteData;
    private double[] lat_long = new double[2];

    @Override
    public void onCreate() {
        super.onCreate();
        mApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
            }
            @Override
            public void onConnectionSuspended(int cause) {
            }
        }).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        final String zip = extras.getString("DATA");
        zipCode = zip.split("[!]+")[0];
        getLatLong();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(lat_long[0]) + "," + String.valueOf(lat_long[1]) + "&key=AIzaSyCXEm-THmJgcLnBA69yuU6UB5M5ererVf8";
        String countyData = "";
        try {
            countyData = new DownloadWebpageTask().execute(url).get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonCounty = new JSONObject(countyData);
            System.out.println(jsonCounty);
            JSONArray data = jsonCounty.getJSONArray("results");
            System.out.println(data);
            JSONArray addressComp = data.getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < addressComp.length(); i++) {
                JSONObject place = addressComp.getJSONObject(i);
                String type = place.getJSONArray("types").get(0).toString();
                if (type.equals("administrative_area_level_2")) {
                    county = place.getString("short_name");
                }
                if (type.equals("administrative_area_level_1")) {
                    county += ", " + place.getString("short_name");
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            InputStream stream = getAssets().open("formatted_2012_vote_data.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            voteData = new String(buffer, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject votes = new JSONObject(voteData);
            JSONObject countyVotes = votes.getJSONObject(county);
            romney = countyVotes.getString("romney");
            obama = countyVotes.getString("obama");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                sendMessage("/zipCode", zip + "!" + county + "!" + romney + "%!" + obama + "%");
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

    private void getLatLong() {
        geocoder = new Geocoder((getApplicationContext()));
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                lat_long[0] = address.getLatitude();
                lat_long[1] = address.getLongitude();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    return sb.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {

        }
    }
}
