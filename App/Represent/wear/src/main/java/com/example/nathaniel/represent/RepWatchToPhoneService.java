package com.example.nathaniel.represent;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Nathaniel on 2/24/2016.
 */
public class RepWatchToPhoneService extends Service { //implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        //initialize the googleAPIClient for message passing
//        mWatchApiClient = new GoogleApiClient.Builder(this).addApi( Wearable.API ).addConnectionCallbacks(this).build();
//        //and actually connect it
//        mWatchApiClient.connect();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
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
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
//    public void onConnected(final Bundle bundle) {
//        Log.d("T", "in onconnected");
//        Wearable.NodeApi.getConnectedNodes(mWatchApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//                    @Override
//                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
//                        String name = bundle.getString("REP_NAME");
//                        nodes = getConnectedNodesResult.getNodes();
//                        Log.d("T", "found nodes");
//                        //when we find a connected node, we populate the list declared above
//                        //finally, we can send a message
//                        sendMessage("/rep", name);
//                        Log.d("T", "sent");
//                    }
//                });
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        final String rep = extras.getString("REP_NAME");
        final String zip = extras.getString("ZIP_CODE");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mWatchApiClient.connect();
                sendMessage("/rep", rep);
            }
        }).start();
        return START_STICKY;
    }

//    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
//    public void onConnectionSuspended(int i) {}

    private void sendMessage(final String path, final String text ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mWatchApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

}
