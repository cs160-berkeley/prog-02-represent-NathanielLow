package com.example.nathaniel.represent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.internal.IMapFragmentDelegate;

public class RepresentativeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("NAME");
        int portrait = R.drawable.gold_marker;

        ImageView picture = (ImageView) findViewById(R.id.portrait);
//        picture.setImageResource(portrait);
        TextView person = (TextView) findViewById(R.id.name);
        TextView party = (TextView) findViewById(R.id.party);
        TextView termEnd = (TextView) findViewById(R.id.termEnd);
        TextView committees = (TextView) findViewById(R.id.committees);
        TextView bills = (TextView) findViewById(R.id.bills);

        person.setText(name);
        // Determination of person should acutally happen with API in PhoneListenerService
        if (name.equals("Dianne Feinstein")) {
            portrait = R.drawable.feinstein;
        }
        else if (name.equals("Barbara Boxer")) {
            portrait = R.drawable.boxer;
        }
        else if (name.equals("Barbara Lee")) {
            portrait = R.drawable.lee;
        }
        picture.setImageResource(portrait);
        party.setText(R.string.party);
        termEnd.setText(R.string.term_end);
        committees.setText(R.string.committees);
        bills.setText(R.string.bills);
    }
}
