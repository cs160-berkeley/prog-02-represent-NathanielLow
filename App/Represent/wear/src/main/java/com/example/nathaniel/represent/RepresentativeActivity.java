package com.example.nathaniel.represent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.wearable.view.GridViewPager;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RepresentativeActivity extends Activity {

    private ShakeEventListener mShakeEventListener;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
        context = this;

        Bundle extras = getIntent().getExtras();
        String zip = extras.getString("ZIP_CODE");

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager(), zip));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeEventListener = new ShakeEventListener();
        mShakeEventListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                int randZip = ThreadLocalRandom.current().nextInt(10000, 100000);
                Intent randomRepPhoneIntent = new Intent(context, ShakeWatchToPhoneService.class);
                randomRepPhoneIntent.putExtra("ZIP_CODE", String.valueOf(randZip));
                startService(randomRepPhoneIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeEventListener);
        super.onPause();
    }

}
