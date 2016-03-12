package com.example.nathaniel.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Nathaniel on 2/27/2016.
 */
public class  RepresentativeListAdapter extends BaseAdapter {
    private final Context context;
    private final String[] representatives;
    private final String[] websites;
    private final String[] emails;
    private final String[] latestTweets;
    private String[] portraits;

    private static final String TWITTER_KEY = "VleLyaxpr3a09ltjxWyH1Qh6k";
    private static final String TWITTER_SECRET = "7iY6uVY7052PmNe0oj4TFPgLjrTaWb67zGbzcnvs31WXb5leAj";

    public RepresentativeListAdapter(Context context, String[] representatives, String[] websites, String[] emails, String[] latestTweets, String[] portraits) {
        this.context = context;
        this.representatives = representatives;
        this.websites = websites;
        this.emails = emails;
        this.latestTweets = latestTweets;
        this.portraits = portraits;
    }

    @Override
    public int getCount() {
        return representatives.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View repList = inflater.inflate(R.layout.list_entries, parent, false);

        ImageView picture = (ImageView) repList.findViewById(R.id.icon);
        TextView repName = (TextView) repList.findViewById(R.id.name);
        TextView repWebsite = (TextView) repList.findViewById(R.id.website);
        TextView repEmail = (TextView) repList.findViewById(R.id.email);
        final TextView repTweet = (TextView) repList.findViewById(R.id.latestTweet);

        String url = "https://theunitedstates.io/images/congress/225x275/" + portraits[position] + ".jpg";
        DownloadImageTask image = new DownloadImageTask(picture);
        image.execute(url);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> result) {
                AppSession session = result.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                StatusesService statusesService = twitterApiClient.getStatusesService();
                statusesService.userTimeline(null, latestTweets[position], 1, null, null, false, true, false, false, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        for (Tweet Tweet : result.data) {
                            repTweet.setText(Tweet.text);
                        }
                    }

                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });

        repName.setText(representatives[position]);
        repWebsite.setText(websites[position]);
        repEmail.setText(emails[position]);
        repTweet.setText(latestTweets[position]);

        repList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, RepresentativeDetailActivity.class);
                detailIntent.putExtra("ID", portraits[position]);
                context.startActivity(detailIntent);
            }
        });
        return repList;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView portrait;

        public DownloadImageTask(ImageView i) {
            portrait = i;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    return BitmapFactory.decodeStream(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap response) {
            if (response != null) {
                portrait.setImageBitmap(response);
            }
        }
    }
}
