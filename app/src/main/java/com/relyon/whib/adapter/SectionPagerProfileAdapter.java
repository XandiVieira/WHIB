package com.relyon.whib.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.relyon.whib.R;
import com.relyon.whib.activity.tab.TabGallery;
import com.relyon.whib.activity.tab.TabHistory;
import com.relyon.whib.activity.tab.TabReports;
import com.relyon.whib.activity.tab.TabValuations;

public class SectionPagerProfileAdapter extends FragmentPagerAdapter {

    private Context context;

    public SectionPagerProfileAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabValuations();
            case 1:
                return new TabReports();
            case 2:
                return new TabGallery();
            default:
                return new TabHistory();
            /*case 3:
                return new TabFriends();
            */
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.valuations);
            case 1:
                return context.getString(R.string.reports);
            case 2:
                return context.getString(R.string.gallery);
            default:
                return context.getString(R.string.history);
            /*default:
                return context.getString(R.string.friends);*/
        }
    }
}