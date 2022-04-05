package com.example.smarthomedashboard.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.smarthomedashboard.fragment.BedRoomFragment;
import com.example.smarthomedashboard.fragment.DiningRoomFragment;
import com.example.smarthomedashboard.fragment.LivingRoomFragment;

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {
    public HomeViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LivingRoomFragment();
            case 1:
                return new BedRoomFragment();
            case 2:
                return new DiningRoomFragment();
            default:
                return new LivingRoomFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Living room";
                break;
            case 1:
                title = "Bedroom";
                break;
            case 2:
                title = "Dining room";
                break;
        }
        return title;
    }
}
