package com.relyon.whib.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.relyon.whib.activity.tab.TabPreferences;
import com.relyon.whib.activity.tab.TabWhibExtra;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TabPreferences();
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
                return "CONFIGURAÇÕES";
            case 1:
                return "WHIB EXTRA";
        }
        return null;
    }
}