package com.example.smarthomedashboard.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.smarthomedashboard.fragment.BedRoomFragment;
import com.example.smarthomedashboard.fragment.DiningRoomFragment;
import com.example.smarthomedashboard.fragment.LivingRoomFragment;
import com.example.smarthomedashboard.fragment.camera.Camera1Fragment;
import com.example.smarthomedashboard.fragment.camera.Camera2Fragment;
import com.example.smarthomedashboard.fragment.camera.Camera3Fragment;
import com.example.smarthomedashboard.fragment.camera.Camera4Fragment;

public class CameraViewPagerAdapter extends FragmentStatePagerAdapter {

    public CameraViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Camera1Fragment();
            case 1:
                return new Camera2Fragment();
            case 2:
                return new Camera3Fragment();
            case 3:
                return new Camera4Fragment();
            default:
                return new Camera1Fragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Cam 1";
                break;
            case 1:
                title = "Cam 2";
                break;
            case 2:
                title = "Cam 3";
                break;
            case 3:
                title = "Cam 4";
                break;
        }
        return title;
    }
}
