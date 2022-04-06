package vn.iot.solution.bku;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;

    TextView txtTemp, txtHumi;
    ToggleButton btnLED;

    private int pointsPlotted = 0;
    private int graphIntervalCounter = 0;

    private int tryCounter = 0;

    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {

    });
    private Viewport viewport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        btnLED = findViewById(R.id.btnLED);

        txtTemp.setText("NULL" + "°C");
        txtHumi.setText("NULL" + "% ");

        btnLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    Log.d("mqtt", "Button is ON");
                    sendDataToMQTT("khanh_le1911363/feeds/bbc-led", "1");
                } else {
                    Log.d("mqtt", "Button is OFF");
                    sendDataToMQTT("khanh_le1911363/feeds/bbc-led", "0");
                }
            }
        });

        //sample graph code
        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);

        startMQTT();
    }


    private void sendDataToMQTT (String topic, String mess) {



        MqttMessage msg = new MqttMessage();
        msg.setId(1234); //filter id
        msg.setQos(0); // Qos [0..4] -> ^ accuracy >< v speed
        msg.setRetained(true); //get the last packet
        //Encapsulate data
        byte[] b = mess.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}
    }

    private void startMQTT() {
        mqttHelper = new MQTTHelper(getApplicationContext(), "002");

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("Mqtt", "Kết nối thành công");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "Received: " + message.toString());
                if (topic.contains("bbc-humi")) {
                    txtHumi.setText(message.toString() + "% ");
                }
                if (topic.contains("bbc-temp")) {
                    txtTemp.setText(message.toString() + "°C");
                    int tempValue = Integer.parseInt(message.toString());
                    //change background color
                    if (tempValue < 20) {
                        txtTemp.setBackgroundColor(Color.parseColor("#03fcf8"));
                    }
                    else if (tempValue > 30) {
                        txtTemp.setBackgroundColor(Color.parseColor("#ff6a00"));
                    }
                    else {
                        txtTemp.setBackgroundColor(Color.parseColor("#ffff00"));
                    }

                    //update the graph
                    ++pointsPlotted;

                    if (pointsPlotted > 20) {
                        pointsPlotted = 1;
                        series.resetData(new DataPoint[] {new DataPoint(0, tempValue)});
                    }

                    series.appendData(new DataPoint(pointsPlotted - 1, tempValue), true, pointsPlotted);
                    viewport.setMaxX(pointsPlotted);
                    viewport.setMinX(pointsPlotted - 5);
                    viewport.setMaxY(50);
                    viewport.setMinY(0);
                }
                if (topic.contains("bbc-led")) {
                    if (message.toString().equals("0")) {
                        btnLED.setChecked(false);
                    }
                    if (message.toString().equals("1")) {
                        btnLED.setChecked(true);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}