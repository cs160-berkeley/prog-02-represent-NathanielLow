package com.example.nathaniel.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Nathaniel on 2/24/2016.
 */
public class PhoneToWatchService extends Service {
    private GoogleApiClient mApiClient;

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
        final String zip = extras.getString("ZIP_CODE");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                sendMessage("/zipCode", zip);
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
}
