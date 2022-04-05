package com.example.smarthomedashboard.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.smarthomedashboard.MainActivity;
import com.example.smarthomedashboard.R;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VoiceFragment extends Fragment {

    //Language processing
    public enum Location {
        NO_ROOM,
        LIVING_ROOM,
        BEDROOM,
        DINNING_ROOM
    }

    public enum Device {
        NO_DEVICE,
        LIGHT,
        AIR_CONDITIONER
    }

    public enum Action {
        NO_ACTION,
        ON,
        OFF,
        GET,
    }

    TextView textView;
    ImageButton speaker;

    TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        textView = (TextView) view.findViewById(R.id.textView);
        speaker = (ImageButton) view.findViewById(R.id.speaker);

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
                startActivityForResult(intent, 100);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            textView.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            //Text to speech
            //speak(textView.getText().toString());
            handleSpeech(textView.getText().toString());
        }
    }

    void speak(String txt) {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1.0f);
                    tts.speak(txt, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
    }

    void handleSpeech(String txt) {
        if (txt.contains("hello") || txt.contains("hi ") || txt.contains(" hi ")) {
            textView.setText("Hello, how can i help you");
            speak("Hello, how can i help you");
        } else if (txt.contains("what") && txt.contains("your name")) {
            textView.setText("My name is Smart Home");
            speak("My name is Smart Home");
        } else if (txt.equals("how are you")) {
            textView.setText("I'm fine, thank you, and you");
            speak("I'm fine, thank you, and you");
        } else if (txt.equals("what can I ask you") || txt.equals("help")) {
            textView.setText("You can ask me anything to help you");
            speak("You can ask me anything to help you");
        } else if (txt.equals("tell me a joke")) {
            textView.setText("What did one snowman say to the other? Do you smell carrots?");
            speak("What did one snowman say to the other? Do you smell carrots?");
        } else if (txt.contains("weather") && (txt.contains("what") || txt.contains("how"))) {
            textView.setText("It is sunny");
            speak("It is sunny");
        } else {
            //Process language
            List<Object> order = processLanguage(txt);
            Location location = (Location) order.get(0);
            Device device = (Device) order.get(1);
            Action action = (Action) order.get(2);

            if (location != Location.NO_ROOM && device != Device.NO_DEVICE && action != Action.NO_ACTION) {
                textView.setText(location.toString() + " " + device.toString() + " " + action.toString());
                if (location == Location.LIVING_ROOM && device == Device.LIGHT && action == Action.ON){
                    // TODO: code here
                    ((MainActivity) getActivity()).sendDataToLivingRoom(true);
                }
                else if (location == Location.LIVING_ROOM && device == Device.LIGHT && action == Action.OFF){
                    // TODO: code here
                    ((MainActivity) getActivity()).sendDataToLivingRoom(false);
                }
                else if (location == Location.BEDROOM && device == Device.LIGHT && action == Action.ON){
                    // TODO: code here
                    ((MainActivity) getActivity()).sendDataToBedRoom(true);
                }
                else if (location == Location.BEDROOM && device == Device.LIGHT && action == Action.OFF){
                    // TODO: code here
                    ((MainActivity) getActivity()).sendDataToBedRoom(false);
                }
            }
            else {
                textView.setText("Sorry I don't understand");
                speak("Sorry I don't understand");
            }
        }
    }

    public static List<Object> processLanguage(String text) {
        Location location = Location.NO_ROOM;
        Device device = Device.NO_DEVICE;
        Action action = Action.NO_ACTION;

        //Identify location
        if (text.contains("living") && text.contains("room")) {
            location = Location.LIVING_ROOM;
        } else if (text.contains("bed") && text.contains("room")) {
            location = Location.BEDROOM;
        } else if (text.contains("dining") && text.contains("room")) {
            location = Location.DINNING_ROOM;
        }

        //Identify device
        if (text.contains("light")) {
            device = Device.LIGHT;
        } else if (text.contains("air") && text.contains("condition")) {
            device = Device.AIR_CONDITIONER;
        }

        //Identify action
        if (text.contains(" on ") || text.contains(" on")) {
            action = Action.ON;
        } else if (text.contains("off")) {
            action = Action.OFF;
        } else if (text.contains("get") || text.contains("what") || text.contains("how") || text.contains("show")) {
            action = Action.GET;
        }

        return Arrays.asList(location, device, action);
    }


//    public interface ISendDataLister{
//        void sendData(String data);
//    }
//
//    public void sendDataToFragment(String data){
//
//    }



}