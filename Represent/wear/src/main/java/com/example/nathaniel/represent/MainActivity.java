package com.example.nathaniel.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by Nathaniel on 3/1/2016.
 */
public class MainActivity extends Activity {

    private ShakeEventListener mShakeEventListener;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

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
