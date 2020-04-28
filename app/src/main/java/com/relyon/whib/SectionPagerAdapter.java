package com.relyon.whib;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabPreferences tabPreferences = new TabPreferences();
                return tabPreferences;
            case 1:
                TabWhibExtra tabWhibExtra = new TabWhibExtra();
                return tabWhibExtra;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
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
