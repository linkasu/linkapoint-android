package ru.aacidov.distalkpro;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by aacidov on 26.10.16.
 */
public class TTS {
    private TextToSpeech tts;
    private static TTS instance;

    public TTS() {
        tts = new TextToSpeech(MainActivity.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });

    }

    public static TTS getInstance() {
        if(instance==null){
            instance = new TTS();
        }

        return instance;
    }

    public void speak (String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
