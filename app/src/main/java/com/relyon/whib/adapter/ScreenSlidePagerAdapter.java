package com.relyon.whib.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.relyon.whib.fragment.FragmentSlide1;
import com.relyon.whib.fragment.FragmentSlide2;
import com.relyon.whib.fragment.FragmentSlide3;
import com.relyon.whib.fragment.FragmentSlide4;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private int NUM_PAGES;

    public ScreenSlidePagerAdapter(FragmentManager fm, int NUM_PAGES) {
        super(fm);
        this.NUM_PAGES = NUM_PAGES;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new FragmentSlide2();
            case 2:
                return new FragmentSlide3();
            case 3:
                return new FragmentSlide4();
            default:
                return new FragmentSlide1();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}