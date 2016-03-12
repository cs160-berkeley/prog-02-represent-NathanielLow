package com.example.nathaniel.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import android.support.wearable.view.FragmentGridPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Nathaniel on 2/27/2016.
 */
public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private String zipCode;
    private String data;
    private String county;
    private String romney;
    private String obama;
    private ArrayList<SimpleRow> mPages = new ArrayList<>();

    public GridPagerAdapter(Context context, FragmentManager fm, String repData) {
        super(fm);
        String[] tokens = repData.split("[!]+");
        zipCode = tokens[0];
        data = tokens[1];
        county = tokens[2];
        romney = tokens[3];
        obama = tokens[4];
        initPages();
    }

    private void initPages() {
        SimpleRow row1 = new SimpleRow();
        JSONArray repsList = null;
        try {
            JSONObject reps = new JSONObject(data);
            repsList = reps.getJSONArray("results");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < repsList.length(); i++) {
            try {
                JSONObject rep = repsList.getJSONObject(i);
                String job;
                String party;
                if (rep.getString("chamber").equals("senate")) {
                    job = "Senator for ";
                }
                else {
                    job = "Representative for ";
                }
                if (rep.getString("party").equals("D")) {
                    party = "Democrat";
                }
                else {
                    party = "Republican";
                }
                row1.addPages(new Page(rep.getString("first_name") + " " + rep.getString("last_name"), job, party, "", rep.getString("bioguide_id"), "Swipe up to see data for the 2012 presidential election."));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SimpleRow row2 = new SimpleRow();
        row2.addPages(new Page("2012 Presidential Vote", county + " (", "Obama: " + obama, "Romney: " + romney, "blah", ""));

        mPages.add(row1);
        mPages.add(row2);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        String job;
        String rep;
        Page page = (mPages.get(row)).getPages(col);

        if (page.role.equals("2012 Presidential Vote")) {
            job = page.role;
            rep = page.name + zipCode + ")";
        }
        else {
            job = page.role;
            rep = page.name + zipCode;
        }
        String details = page.party;
        String details2 = page.party2;
        String id = page.id;
        String instructions = page.directions;
        return RepFragment.newInstance(job, rep, details, details2, id, instructions);
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mPages.get(rowNum).size();
    }

    private static class Page {
        String role;
        String name;
        String party;
        String party2;
        String id;
        String directions;

        public Page(String job, String rep, String details, String details2, String repID, String instructions) {
            role = job;
            name = rep;
            party = details;
            party2 = details2;
            id = repID;
            directions = instructions;
        }
    }

    public class SimpleRow {

        ArrayList<Page> mPagesRow = new ArrayList<>();

        public void addPages(Page page) {
            mPagesRow.add(page);
        }

        public Page getPages(int index) {
            return mPagesRow.get(index);
        }

        public int size(){
            return mPagesRow.size();
        }
    }
}
