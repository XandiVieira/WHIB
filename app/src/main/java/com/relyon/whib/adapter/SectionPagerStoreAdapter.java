package com.relyon.whib.adapter;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.relyon.whib.R;
import com.relyon.whib.activity.tab.TabStore;
import com.relyon.whib.activity.tab.TabWhibExtra;

public class SectionPagerStoreAdapter extends FragmentPagerAdapter {

    private Context context;

    public SectionPagerStoreAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TabStore();
        }
        return new TabWhibExtra();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.stickers);
            case 1:
                return context.getString(R.string.whib_extra);
        }
        return null;
    }
}