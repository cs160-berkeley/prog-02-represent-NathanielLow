package com.example.nathaniel.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;


/**
 * Created by Nathaniel on 2/24/2016.
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String ZIP_CODE = "/zipCode";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());

        if(messageEvent.getPath().equalsIgnoreCase(ZIP_CODE)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, RepresentativeActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ZIP_CODE", value);
            Log.d("T", "about to start watch MainActivity with CAT_NAME: " + value);
            startActivity(intent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }

    }
}
