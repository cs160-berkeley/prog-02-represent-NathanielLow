package com.example.nathaniel.represent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RepresentativeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative_detail);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("ID");

        String repUrl = "http://congress.api.sunlightfoundation.com/legislators?bioguide_id=" + name + "&apikey=f73c4ad3d3434cf1bafddcd187cea7dc";
        String committeeUrl = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + name + "&apikey=f73c4ad3d3434cf1bafddcd187cea7dc";
        String billUrl = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + name + "&apikey=f73c4ad3d3434cf1bafddcd187cea7dc";
        String picUrl = "https://theunitedstates.io/images/congress/original/" + name + ".jpg";

        ImageView picture = (ImageView) findViewById(R.id.portrait);
        TextView person = (TextView) findViewById(R.id.name);
        TextView party = (TextView) findViewById(R.id.party);
        TextView termEnd = (TextView) findViewById(R.id.termEnd);
        ListView committees = (ListView) findViewById(R.id.committees);
        ListView bills = (ListView) findViewById(R.id.bills);

        new DownloadImageTask(picture).execute(picUrl);
        new DownloadWebpageTask(this, person, party, termEnd, committees, bills).execute(repUrl, committeeUrl, billUrl);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String[]> {

        private Context context;
        private TextView person;
        private TextView party;
        private TextView termEnd;
        private ListView committees;
        private ListView bills;

        public DownloadWebpageTask(Context con, TextView name, TextView faction, TextView end, ListView com, ListView bil) {
            context = con;
            person = name;
            party = faction;
            termEnd = end;
            committees = com;
            bills = bil;
        }

        @Override
        protected String[] doInBackground(String... urls) {
            String[] result = new String[urls.length];
            try {
                for (int i = 0; i < urls.length; i++) {
                    URL url = new URL(urls[i]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        result[i] = sb.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] response) {
            if (response == null) {

            }
            JSONArray jsonArray = null;
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject repData = jsonArray.getJSONObject(i);
                    person.setText(repData.getString("first_name") + " " + repData.getString("last_name") + "  (" + repData.getString("party") + " - " + repData.getString("state") + ")");
                    if (repData.getString("party").equals("D")) {
                        party.setText("Democrat");
                    }
                    else {
                        party.setText("Republican");
                    }
                    termEnd.setText("Term ends: " + repData.getString("term_end"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(response[1]);
                jsonArray = jsonObject.getJSONArray("results");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            String[] comms = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject com = jsonArray.getJSONObject(i);
                    comms[i] = com.getString("name");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final DocumentListAdapter committeesAdapter = new DocumentListAdapter(context, comms);
            committees.setAdapter(committeesAdapter);

            try {
                JSONObject jsonObject = new JSONObject(response[2]);
                jsonArray = jsonObject.getJSONArray("results");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            String[] repBills = new String[5];
            String[] tentRepBills = new String[jsonArray.length()];
            String[] dates = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject bill = jsonArray.getJSONObject(i);
                    tentRepBills[i] = bill.getString("short_title");
                    dates[i] = bill.getString("introduced_on");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            int i = 0;
            int j = 0;
            while (i < 5 && j < tentRepBills.length) {
                if (!(tentRepBills[j].equals("null"))) {
                    repBills[i] = tentRepBills[j] + " (" + dates[j] + ")";
                    i += 1;
                    j += 1;
                }
                else {
                    j += 1;
                }
            }
            final DocumentListAdapter billAdapter = new DocumentListAdapter(context, repBills);
            bills.setAdapter(billAdapter);
        }
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
