package com.example.nathaniel.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ListRepresentativesActivity extends AppCompatActivity {

    private TextView zipText;
    private ListView senRepList;
    private ListView houseRepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_representatives);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(myToolbar);

        zipText = (TextView) findViewById(R.id.zipCode);
        Bundle extras = getIntent().getExtras();
        String zipCode = extras.getString("ZIP_CODE");
        String text = getResources().getText(R.string.representative_top_text).toString();
        zipText.setText(text + " " + zipCode + ". Tap a representative for more information.");

        senRepList = (ListView) findViewById(R.id.senListView);
        String[] representatives = {"Dianne Feinstein", "Barbara Boxer"};
        String[] websites = {"feinstein.com", "boxer.com"};
        String[] emails = {"dianne@feinstein.com", "barbara@boxer.com"};
        String[] latestTweets = {"@feinstein", "@boxer"};
        int[] portraits = {R.drawable.feinstein, R.drawable.boxer};
        final RepresentativeListAdapter senAdapter = new RepresentativeListAdapter(this, representatives, websites, emails, latestTweets, portraits);
        senRepList.setAdapter(senAdapter);

        houseRepList = (ListView) findViewById(R.id.houseListView);
        String[] houseReps = {"Barbara Lee"};
        String[] houseWebs = {"lee.com"};
        String[] houseEmails = {"barbara@lee.com"};
        String[] houseTweets= {"@lee"};
        int[] housePics = {R.drawable.lee};
        final RepresentativeListAdapter houseAdapter = new RepresentativeListAdapter(this, houseReps, houseWebs, houseEmails, houseTweets, housePics);
        houseRepList.setAdapter(houseAdapter);
    }

}
