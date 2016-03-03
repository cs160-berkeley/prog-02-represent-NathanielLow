package com.example.nathaniel.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.List;

/**
 * Created by Nathaniel on 2/27/2016.
 * Special thanks to http://www.sprima.com/blog/?p=144 (https://github.com/tapasb/ImagineAir)
 */
public class GridPagerAdapter extends FragmentGridPagerAdapter {
    private final Context mContext;
    private String zipCode;
//    private List mRows;

    public GridPagerAdapter(Context context, FragmentManager fm, String zip) {
        super(fm);
        mContext = context;
        zipCode = zip;
    }

//    static final int[] BG_IMAGES = new int[] {
//            R.drawable.smallflag2
//    };

    private static class Page {
        String role;
        String name;
        String party;
        int portrait;
//        int cardGravity = Gravity.CENTER;
//        boolean expansionEnabled = true;
//        float expansionFactor = 1.0f;
//        int expansionDirection = CardFragment.EXPAND_DOWN;

        public Page(String job, String rep, String details, int picture) {
            this.role = job;
            this.name = rep;
            this.party = details;
            this.portrait = picture;
        }

    }

    private final Page[][] PAGES = {
            {
                    new Page("Senator for ", "Dianne Feinstein", "Democrat", R.drawable.feinstein),
                    new Page("Senator for ", "Barbara Boxer", "Democrat", R.drawable.boxer),
                    new Page("Representative for ", "Barbara Lee", "Democrat", R.drawable.lee)
            },
            {
                    new Page("2012 Presidential Vote", "Alameda, CA (", "Obama: XX%, Romney: XX%", R.drawable.smallflag2),
            }
    };

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        String job = "";
        String rep = "";
        if (page.role.equals("2012 Presidential Vote")) {
            job = page.role;
            rep = page.name + zipCode + ")";
        }
        else {
            job = page.role + zipCode;
            rep = page.name;
        }
        String details = page.party;
        int picture = page.portrait;
        return RepFragment.newInstance(job, rep, details, picture);
//        fragment.setCardGravity(page.cardGravity);
//        fragment.setExpansionEnabled(page.expansionEnabled);
//        fragment.setExpansionDirection(page.expansionDirection);
//        fragment.setExpansionFactor(page.expansionFactor);

    }

//    @Override
//    public Drawable getBackgroundForRow(int row) {
//        return mContext.getResources().getDrawable((BG_IMAGES[row % BG_IMAGES.length]), null);
//    }

//    public Drawable getBackgroundForPage(int row, int column) {
//        if(row == 2 && column == 1) {
//            // Place image at specified position
//            return mContext.getResources().getDrawable(R.drawable.smallflag2, null);
//        }
//        else {
//            // Default to background image for row
//            return GridPagerAdapter.BACKGROUND_NONE;
//        }
//    }

    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }
}
