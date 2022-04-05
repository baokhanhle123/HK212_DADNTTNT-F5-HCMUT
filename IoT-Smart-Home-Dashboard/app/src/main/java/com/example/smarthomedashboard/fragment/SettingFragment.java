package com.example.smarthomedashboard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.R;
import com.google.android.material.slider.Slider;

public class SettingFragment extends Fragment {
    float tempLimit = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Slider slider = view.findViewById(R.id.slider);
        Button apply = view.findViewById(R.id.applyBtn);
        TextView limit = view.findViewById(R.id.tempLimit);

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tempLimit = value;
//                Log.d("aaa", "onValueChange: " + tempLimit);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit.setText(Integer.toString((int) tempLimit) + "°C");
                ((MainActivity) getActivity()).updateLimit((int) tempLimit);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView limit = getView().findViewById(R.id.tempLimit);
        limit.setText(Integer.toString((int) tempLimit) + "°C");
    }
}
