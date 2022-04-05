package com.example.smarthomedashboard.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.example.smarthomedashboard.fragment.ChartFragment;
import com.example.smarthomedashboard.fragment.HomeFragment;
import com.example.smarthomedashboard.fragment.SettingFragment;
import com.example.smarthomedashboard.fragment.CameraFragment;
import com.example.smarthomedashboard.fragment.VoiceFragment;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ChartFragment();
            case 2:
                return new SettingFragment();
            case 3:
                return new CameraFragment();
            case 4:
                return new VoiceFragment();
            default:
                return new HomeFragment();
        }
    }


    @Override
    public int getCount() {
        return 5;
    }
}
