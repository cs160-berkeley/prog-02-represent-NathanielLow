package com.example.nathaniel.represent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nathaniel on 2/27/2016.
 * Special thanks to ishu on StackOverflow (http://stackoverflow.com/questions/16333754/how-to-customize-listview-using-baseadapter)
 */
public class  RepresentativeListAdapter extends BaseAdapter {
    private final Context context;
    private final String[] representatives;
    private final String[] websites;
    private final String[] emails;
    private final String[] latestTweets;
    private int[] portraits;


    public RepresentativeListAdapter(Context context, String[] representatives, String[] websites, String[] emails, String[] latestTweets, int[] portraits) {
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
        TextView repTweet = (TextView) repList.findViewById(R.id.latestTweet);
        picture.setImageResource(portraits[position]);
        repName.setText(representatives[position]);
        repWebsite.setText(websites[position]);
        repEmail.setText(emails[position]);
        repTweet.setText(latestTweets[position]);
        repList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, RepresentativeDetailActivity.class);
                detailIntent.putExtra("NAME", representatives[position]);
//                detailIntent.putExtra("PICTURE", portraits[position]);
                context.startActivity(detailIntent);
            }
        });
        return repList;
    }
}
