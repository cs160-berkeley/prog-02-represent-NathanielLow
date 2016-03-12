package com.example.nathaniel.represent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * created by Nathaniel on 3/9/16
 * Special thanks to http://stackoverflow.com/questions/3641304/get-latitude-and-longitude-using-zipcode
 * https://github.com/obaro/SimpleWebAPI/blob/master/app/src/main/java/com/sample/foo/simplewebapi/MainActivity.java
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private EditText mZipCode;
    private Intent representativeListIntent;
    private Intent watchRepresentativeIntent;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;
    private String zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mZipCodeButton = (Button) findViewById(R.id.zipCodeButton);
        Button mCurrentLocationButton = (Button) findViewById(R.id.currentLocationButton);
        mZipCode = (EditText) findViewById(R.id.enterZipCode);
        representativeListIntent = new Intent(this, ListRepresentativesActivity.class);
        watchRepresentativeIntent = new Intent(this, PhoneToWatchService.class);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mZipCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipCode = mZipCode.getText().toString();
                String json = zipCode + "!";
                if (!(zipCode.equals(""))) {
                    String zip = validateZip(zipCode);
                    if (!(zip.equals("invalid"))) {
                        representativeListIntent.putExtra("ZIP_CODE", zipCode);
                        representativeListIntent.putExtra("LATITUDE", "none");
                        representativeListIntent.putExtra("LONGITUDE", "none");
                        representativeListIntent.putExtra("DATA", zip);
                        startActivity(representativeListIntent);
                        json += zip;
                        watchRepresentativeIntent.putExtra("DATA", json);
                        startService(watchRepresentativeIntent);
                    }
                }
            }
        });

        mCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mLatitude = 0;
                    mLongitude = 0;
                } else {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        mLatitude = mLastLocation.getLatitude();
                        mLongitude = mLastLocation.getLongitude();
                    }
                }
                String zip = "";
                try {
                    zip = new DownloadWebpageTask().execute("http://congress.api.sunlightfoundation.com/legislators/locate?latitude=" + String.valueOf(mLatitude) + "&longitude=" + String.valueOf(mLongitude) + "&apikey=f73c4ad3d3434cf1bafddcd187cea7dc").get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                zipCode = getZipCode(mLatitude, mLongitude);
                String data = zipCode + "!" + zip;
                representativeListIntent.putExtra("ZIP_CODE", zipCode);
                representativeListIntent.putExtra("LATITUDE", String.valueOf(mLatitude));
                representativeListIntent.putExtra("LONGITUDE", String.valueOf(mLongitude));
                representativeListIntent.putExtra("DATA", zip);
                startActivity(representativeListIntent);
                watchRepresentativeIntent.putExtra("DATA", data);
                startService(watchRepresentativeIntent);
            }
        });
    }

    public String getZipCode(double lat, double lon) {
        final Geocoder gcd = new Geocoder(getApplicationContext());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (Address address : addresses) {
            if (address.getLocality() != null && address.getPostalCode() != null) {
                return address.getPostalCode();
            }
        }
        return "No zip code";
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(this, "Invalid zip code. Please try again", Toast.LENGTH_LONG);
        toast.show();
        return "invalid";
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionSuspended(int connection) {

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
