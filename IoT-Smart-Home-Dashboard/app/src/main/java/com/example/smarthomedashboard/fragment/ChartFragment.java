package com.example.smarthomedashboard.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.MyMarkerView;
import com.example.smarthomedashboard.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChartFragment extends Fragment {
    final int HOUR_OF_DAY = 24;
    LineChart tempChart;
    LineChart humidChart;

    HashMap<Integer, Float> tempHashMap = new HashMap<Integer, Float>() {{
        for (int i = 0; i < HOUR_OF_DAY; i++) {
            put(i, 0.0F);
        }
    }};

    HashMap<Integer, Float> humidHashMap = new HashMap<Integer, Float>() {{
        for (int i = 0; i < HOUR_OF_DAY; i++) {
            put(i, 0.0F);
        }
    }};

    HashMap<Integer, Integer> numOfRecord = new HashMap<Integer, Integer>() {{
        for (int i = 0; i < HOUR_OF_DAY; i++) {
            put(i, 0);
        }
    }};

    public ChartFragment() {

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        try {
            curlRequest("homeinfo");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tempChart = (LineChart) view.findViewById(R.id.tempChart);
        humidChart = (LineChart) view.findViewById(R.id.humidChart);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.refreshLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChartData();
                Toast toast = Toast.makeText(getContext(),"Refresh data successfully",Toast. LENGTH_SHORT);
                toast.show();
                pullToRefresh.setRefreshing(false);
            }
        });

        TextView day = view.findViewById(R.id.day);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String today = sdf.format(date);
        day.setText(today);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        refreshChartData();
        tempChart.notifyDataSetChanged();
        tempChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void curlRequest(String feeds) throws ParseException {
        Date date = Calendar.getInstance().getTime();
        date.setHours(0);
        date.setMinutes(0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String today = format.format(date);
        String tomorrow = getNextDate(today);

        String url = "https://io.adafruit.com/api/v2/bksmartiot/feeds/" + feeds + "/data?x-aio-key="+ MainActivity.AIO_key +"&start_time="
                + today + "&end_time=" + tomorrow;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    handleDataResponse(response);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void handleDataResponse(JSONArray data) throws JSONException, ParseException {
        if (data.length() != 0) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject parsedData = new JSONObject(data.getJSONObject(i).getString("value"));
                String time = data.getJSONObject(i).getString("created_at");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = dateFormat.parse(time);
                int localTime = date.getHours() + 7;
                int temp = parsedData.getInt("temp");
                int humid = parsedData.getInt("humidity");

                tempHashMap.put(localTime, tempHashMap.get(localTime) + (float) temp);
                humidHashMap.put(localTime, humidHashMap.get(localTime) + (float) humid);
                numOfRecord.put(localTime, numOfRecord.get(localTime) + 1);
            }

            for (int i = 0; i < HOUR_OF_DAY; i++) {
                if (numOfRecord.get(i) == 0)
                    continue;
                tempHashMap.put(i, tempHashMap.get(i) / numOfRecord.get(i));
                humidHashMap.put(i, humidHashMap.get(i) / numOfRecord.get(i));
            }

            handleDataChart(tempHashMap, "Temperature", tempChart, Color.RED);
            handleDataChart(humidHashMap, "Humidity", humidChart, Color.BLUE);
        }

    }

    public void handleDataChart(HashMap hashMap, String name, LineChart lineChart, int color) throws ParseException {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for (int i = 0; i < hashMap.size(); i++) {
            dataVals.add(new Entry(i, (float) hashMap.get(i)));
        }
        configChart(dataVals, name, lineChart, color);
    }

    public void configChart(ArrayList<Entry> dataVals, String name, LineChart lineChart, int color) {
        LineDataSet lineDataSet = new LineDataSet(dataVals, name);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(color);
        lineDataSet.setFillAlpha(15);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleHoleColor(color);
        lineDataSet.setCircleSize(2f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setCubicIntensity(1f);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        data.setValueTextSize(13f);
        data.setDrawValues(false);

        lineChart.setDescription(null);
        lineChart.animateY(300);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisMaximum(100f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        MyMarkerView marker = new MyMarkerView(getActivity(), R.layout.content);
        lineChart.setMarker(marker);
    }

    public static String getNextDate(String curDate) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        final Date date = format.parse(curDate);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return format.format(calendar.getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshChartData() {
        try {
            for(int i = 0; i < HOUR_OF_DAY; i++) {
                tempHashMap.put(i, 0.0F);
                humidHashMap.put(i, 0.0F);
                numOfRecord.put(i, 0);
            }
            curlRequest("homeinfo");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
