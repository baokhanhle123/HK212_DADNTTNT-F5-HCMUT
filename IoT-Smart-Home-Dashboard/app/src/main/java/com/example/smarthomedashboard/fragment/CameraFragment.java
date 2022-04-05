package com.example.smarthomedashboard.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.smarthomedashboard.R;
import com.example.smarthomedashboard.adapter.CameraViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class CameraFragment extends Fragment {

    // Declare
    private TabLayout cameraTabLayout;
    private ViewPager cameraViewPager;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Get view
        cameraTabLayout = view.findViewById(R.id.camera_tab_layout);
        cameraViewPager = view.findViewById(R.id.camera_view_pager);

        //Call
        setUpCameraAdapter();

        cameraTabLayout.setupWithViewPager(cameraViewPager);

        return view;
    }

    private void setUpCameraAdapter(){
        CameraViewPagerAdapter cameraViewPagerAdapter = new CameraViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        cameraViewPager.setAdapter(cameraViewPagerAdapter);
    }
}