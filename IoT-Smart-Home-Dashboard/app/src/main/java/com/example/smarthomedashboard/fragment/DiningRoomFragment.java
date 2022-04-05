package com.example.smarthomedashboard.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.R;
import com.example.smarthomedashboard.mqtt.MQTTHelper;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class DiningRoomFragment extends Fragment implements View.OnClickListener {

    // Declare
    CardView dining_room_light, dining_room_air_conditioner;
    ConstraintLayout roomSetting, lightSetting, airConditionerSetting;
    ImageButton btn_light_back, btn_air_conditioner_back;
    TextView tempView, humidView, gasView;
    ProgressBar tempProgress, humidProgress, gasProgress;
    JSONArray light;
    JSONArray airConditioner;
    MQTTHelper mqttHelper;
    String clientID = Integer.toString((int)Math.random() * 1000);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dining_room, container, false);


        // Match view
        dining_room_light = view.findViewById(R.id.dining_room_light);
        dining_room_air_conditioner = view.findViewById(R.id.dining_room_air_conditioner);

        roomSetting = view.findViewById(R.id.dining_room_setting_container);
        lightSetting = view.findViewById(R.id.dining_room_light_container);
        airConditionerSetting = view.findViewById(R.id.dining_room_air_conditioner_container);

        btn_light_back = view.findViewById(R.id.dining_room_btn_light_back);
        btn_air_conditioner_back = view.findViewById(R.id.dining_room_btn_air_conditioner_back);

        //Call
        setUpDiningRoomAirConditionerButton(view);
        setUpBtnAirConditionerBack(view);
        setUpBtnLightBack(view);
        setUpDiningRoomLightButton(view);

        SwitchCompat switchCompat_1 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_light_1);
        switchCompat_1.setOnClickListener(this);
        SwitchCompat switchCompat_2 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_light_2);
        switchCompat_2.setOnClickListener(this);
        SwitchCompat switchCompat_3 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_light_3);
        switchCompat_3.setOnClickListener(this);
        SwitchCompat switchCompat_4 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_light_4);
        switchCompat_4.setOnClickListener(this);
        SwitchCompat switchCompat_5 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_air_conditioner_1);
        switchCompat_5.setOnClickListener(this);
        SwitchCompat switchCompat_6 = (SwitchCompat) view.findViewById(R.id.dining_room_switch_air_conditioner_2);
        switchCompat_6.setOnClickListener(this);

        mqttHelper = new MQTTHelper(view.getContext(), clientID);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        curlRequest("homeinfo");
        curlRequest("diningroom");
    }

    @Override
    public void onResume() {
        super.onResume();
        curlRequest("homeinfo");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setUpDiningRoomLightButton(View context) {
        dining_room_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().INVISIBLE);
                lightSetting.setVisibility(getView().VISIBLE);
            }
        });
    }

    private void setUpBtnLightBack(View context){
        btn_light_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().VISIBLE);
                lightSetting.setVisibility(getView().INVISIBLE);
            }
        });
    }

    private void setUpDiningRoomAirConditionerButton(View context) {
        dining_room_air_conditioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().INVISIBLE);
                airConditionerSetting.setVisibility(getView().VISIBLE);
            }
        });
    }

    private void setUpBtnAirConditionerBack(View context){
        btn_air_conditioner_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSetting.setVisibility(getView().VISIBLE);
                airConditionerSetting.setVisibility(getView().INVISIBLE);
            }
        });
    }



    public void curlRequest(String feeds) {
        String url = "https://io.adafruit.com/api/v2/bksmartiot/feeds/" + feeds + "/data/last?x-aio-key=" + MainActivity.AIO_key;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String value = response.getString("value");
                            JSONObject jsonVal = new JSONObject(value);
                            if (feeds.equals("homeinfo")) {
                                String temp = jsonVal.getString("temp");
                                String humid = jsonVal.getString("humidity");
                                String gas = jsonVal.getString("gas");
                                setHomeInfo(temp, humid, gas);
                            } else if (feeds.equals("diningroom")) {
                                JSONArray light = jsonVal.getJSONArray("light");
                                JSONArray air = jsonVal.getJSONArray("air");
                                handleData(light, air);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setHomeInfo(String temp, String humid, String gas) {
        tempView = getView().findViewById(R.id.tempText_3);
        humidView = getView().findViewById(R.id.humidText_3);
        gasView = getView().findViewById(R.id.gasText_3);

        tempProgress = getView().findViewById(R.id.tempProgressBar_3);
        humidProgress = getView().findViewById(R.id.humidProgressBar_3);
        gasProgress = getView().findViewById(R.id.gasProgressBar_3);

        tempView.setText(temp + "°C");
        humidView.setText(humid + "%");
        gasView.setText(gas + "%");
        tempProgress.setProgress(Integer.parseInt(temp), true);
        humidProgress.setProgress(Integer.parseInt(humid), true);
        gasProgress.setProgress(Integer.parseInt(gas), true);

    }

    public void handleData(JSONArray lightData, JSONArray airConditionerData) throws JSONException {
        int[] livingRoomLightSwitchList = {
                R.id.dining_room_switch_light_1,
                R.id.dining_room_switch_light_2,
                R.id.dining_room_switch_light_3,
                R.id.dining_room_switch_light_4
        };
        int[] livingRoomAirSwitchList = {R.id.dining_room_switch_air_conditioner_1, R.id.dining_room_switch_air_conditioner_2};

        for (int i = 0; i < lightData.length(); i++) {
            SwitchCompat switchCompat = getView().findViewById(livingRoomLightSwitchList[i]);
            switchCompat.setChecked(lightData.getString(i).equals("1"));
        }

        for (int i = 0; i < airConditionerData.length(); i++) {
            SwitchCompat switchCompat = getView().findViewById(livingRoomAirSwitchList[i]);
            switchCompat.setChecked(airConditionerData.getString(i).equals("1"));
        }

        light = new JSONArray(lightData.toString());
        airConditioner = new JSONArray(airConditionerData.toString());

        ((MainActivity) getActivity()).updateDiningRoomStatus(light, airConditioner);
    }

    public void handlePublishData(View v, String kind, int ID) throws JSONException {
        if (kind.equals("light")) {
            light.put(ID, light.getInt(ID) == 1 ? 0 : 1);
            JSONObject data = new JSONObject();
            data.put("light", light);
            data.put("air", airConditioner);
            sendDataMQTT(data, "diningroom");
        } else if (kind.equals("air")) {
            airConditioner.put(ID, airConditioner.getInt(ID) == 1 ? 0 : 1);
            JSONObject data = new JSONObject();
            data.put("light", light);
            data.put("air", airConditioner);
            sendDataMQTT(data, "diningroom");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dining_room_switch_light_1:
                try {
                    handlePublishData(v, "light", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dining_room_switch_light_2:
                try {
                    handlePublishData(v, "light", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dining_room_switch_light_3:
                try {
                    handlePublishData(v, "light", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dining_room_switch_light_4:
                try {
                    handlePublishData(v, "light", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dining_room_switch_air_conditioner_1:
                try {
                    handlePublishData(v, "air", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dining_room_switch_air_conditioner_2:
                try {
                    handlePublishData(v, "air", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void sendDataMQTT(JSONObject data, String topic) {
        MqttMessage message = new MqttMessage();
        message.setId(Integer.parseInt(clientID));
        message.setQos(0);
        message.setRetained(true);
        byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
        message.setPayload(bytes);
        Log.d("publish", "Publish:" + message);
        try {
            mqttHelper.mqttAndroidClient.publish("bksmartiot/feeds/" + topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}