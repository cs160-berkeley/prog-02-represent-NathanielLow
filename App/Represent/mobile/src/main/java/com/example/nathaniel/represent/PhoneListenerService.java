package com.example.nathaniel.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Nathaniel on 2/24/2016.
 */
public class PhoneListenerService extends WearableListenerService {

    private static final String REP = "/rep";
    private static final String ZIP = "/zip";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if(messageEvent.getPath().equalsIgnoreCase(REP)) {

            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent detailedIntent = new Intent(this, RepresentativeDetailActivity.class);
            detailedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailedIntent.putExtra("ID", value);
            startActivity(detailedIntent);

        }
        else if (messageEvent.getPath().equalsIgnoreCase(ZIP)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String valid = validateZip(value);
            if (!(valid.equals("invalid"))) {
                Intent listIntent = new Intent(this, ListRepresentativesActivity.class);
                listIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                listIntent.putExtra("ZIP_CODE", value);
                listIntent.putExtra("LATITUDE", "none");
                listIntent.putExtra("LONGITUDE", "none");
                listIntent.putExtra("DATA", valid);
                startActivity(listIntent);
                Intent watchRepresentativeIntent = new Intent(this, PhoneToWatchService.class);
                watchRepresentativeIntent.putExtra("DATA", value + "!" + valid);
                startService(watchRepresentativeIntent);
            }
            else {
                while (valid.equals("invalid")) {
                    value = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
                    valid = validateZip(value);
                }
                Intent listIntent = new Intent(this, ListRepresentativesActivity.class);
                listIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                listIntent.putExtra("ZIP_CODE", value);
                listIntent.putExtra("LATITUDE", "none");
                listIntent.putExtra("LONGITUDE", "none");
                listIntent.putExtra("DATA", valid);
                startActivity(listIntent);
                Intent watchRepresentativeIntent = new Intent(this, PhoneToWatchService.class);
                watchRepresentativeIntent.putExtra("DATA", value + "!" + valid);
                startService(watchRepresentativeIntent);
            }
        }
        else {
            super.onMessageReceived(messageEvent);
        }

    }

    private String validateZip(String zip) {
        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zip + "&apikey=f73c4ad3d3434cf1bafddcd187cea7dc";
        String data = "";
        try {
            data = new DownloadWebpageTask().execute(url).get();
            JSONObject validate = new JSONObject(data);
            JSONArray isValid = validate.getJSONArray("results");
            if (isValid.length() > 0) {
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "invalid";
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
