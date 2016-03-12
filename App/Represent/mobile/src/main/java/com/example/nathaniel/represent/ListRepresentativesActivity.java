package com.example.nathaniel.represent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListRepresentativesActivity extends AppCompatActivity {

    private TextView zipText;
    private ListView senRepList;
    private ListView houseRepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_representatives);

        zipText = (TextView) findViewById(R.id.zipCode);
        Bundle extras = getIntent().getExtras();
        String zipCode = extras.getString("ZIP_CODE");
        String latitude = extras.getString("LATITUDE");
        String longitude = extras.getString("LONGITUDE");
        String data = extras.getString("DATA");
        String text = getResources().getText(R.string.representative_top_text).toString();
        zipText.setText(text + " " + zipCode + ". Tap a representative for more information.");

        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] representatives = new String[2];
        String[] websites = new String[2];
        String[] emails = new String[2];
        String[] latestTweets = new String[2];
        String[] portraits = new String[2];
        String[] houseReps = new String[jsonArray.length() - 2];
        String[] houseWebs = new String[jsonArray.length() - 2];
        String[] houseEmails = new String[jsonArray.length() - 2];
        String[] houseTweets = new String[jsonArray.length() - 2];
        String[] housePics = new String[jsonArray.length() - 2];

        int senIndex = 0;
        int houseIndex = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject repData = jsonArray.getJSONObject(i);
                if (repData.getString("chamber").equals("senate")) {
                    String name = repData.getString("first_name") + " " + repData.getString("last_name") + "  (" + repData.getString("party") + " - " + repData.getString("state") + ")";
                    representatives[senIndex] = name;
                    websites[senIndex] = repData.getString("website");
                    emails[senIndex] = repData.getString("oc_email");
                    latestTweets[senIndex] = repData.getString("twitter_id");
                    portraits[senIndex] = repData.getString("bioguide_id");
                    senIndex += 1;
                } else if (repData.getString("chamber").equals("house")) {
                    String name = repData.getString("first_name") + " " + repData.getString("last_name") + "  (" + repData.getString("party") + " - " + repData.getString("state") + ")";
                    houseReps[houseIndex] = name;
                    houseWebs[houseIndex] = repData.getString("website");
                    houseEmails[houseIndex] = repData.getString("oc_email");
                    houseTweets[houseIndex] = repData.getString("twitter_id");
                    housePics[houseIndex] = repData.getString("bioguide_id");
                    houseIndex += 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            senRepList = (ListView) findViewById(R.id.senListView);
            final RepresentativeListAdapter senAdapter = new RepresentativeListAdapter(this, representatives, websites, emails, latestTweets, portraits);
            senRepList.setAdapter(senAdapter);

            houseRepList = (ListView) findViewById(R.id.houseListView);
            final RepresentativeListAdapter houseAdapter = new RepresentativeListAdapter(this, houseReps, houseWebs, houseEmails, houseTweets, housePics);
            houseRepList.setAdapter(houseAdapter);
        }
    }
}
