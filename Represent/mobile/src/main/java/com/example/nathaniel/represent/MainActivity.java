package com.example.nathaniel.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mZipCode;
    private Intent representativeListIntent;
    private Intent watchRepresentativeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mZipCodeButton = (Button) findViewById(R.id.zipCodeButton);
        Button mCurrentLocationButton = (Button) findViewById(R.id.currentLocationButton);
        mZipCode = (EditText) findViewById(R.id.enterZipCode);
        representativeListIntent = new Intent(this, ListRepresentativesActivity.class);
        watchRepresentativeIntent = new Intent(this, PhoneToWatchService.class);

        mZipCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCode = mZipCode.getText().toString();
                if (!(zipCode.equals(""))) {
                    if (Integer.parseInt(zipCode) > 9999 && Integer.parseInt(zipCode) < 100000 ) {
                        representativeListIntent.putExtra("ZIP_CODE", zipCode);
                        startActivity(representativeListIntent);
                        watchRepresentativeIntent.putExtra("ZIP_CODE", zipCode);
                        startService(watchRepresentativeIntent);
                    }
                }
            }
        });

        mCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use API to get current zip code
                String zipCode = "94720";
                representativeListIntent.putExtra("ZIP_CODE", zipCode);
                startActivity(representativeListIntent);
                watchRepresentativeIntent.putExtra("ZIP_CODE", zipCode);
                startService(watchRepresentativeIntent);
            }
        });
    }
}
