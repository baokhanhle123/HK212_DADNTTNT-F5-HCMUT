package com.example.smarthomedashboard.mqtt;

import android.content.Context;
import android.util.Log;

import com.example.smarthomedashboard.mqtt.MQTTHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MQTTService {
    // Attronute
    MQTTHelper mqttHelper;
    List<MQTTBufferMess> buffer = new ArrayList<>();
    int waiting_period = 0;
    boolean sending_mess_agian = false;

    private static Context context;
    private static MQTTService mqttService;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private MQTTService(){
    }

    // Singleton Pattern
    public static MQTTService getMqttService(){
        if (mqttService == null){
            mqttService = new MQTTService();
        }
        return mqttService;
    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getContext(), "1913695");

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
                Log.d("Mqtt", "Received: " + message.toString());
                if (topic.contains("bbc-temp"))
                {

                }
                if (topic.contains("bbc-led"))
                {

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void sendDataToMQTT(String topic, String mess){

        MQTTBufferMess copyMess = new MQTTBufferMess();
        copyMess.topic = topic;
        copyMess.mess = mess;
        buffer.add(copyMess);

        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = mess.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}

    }

    public class MQTTBufferMess{
        public String topic;
        public String mess;
    }
}
