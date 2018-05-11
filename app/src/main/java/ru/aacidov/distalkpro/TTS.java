package ru.aacidov.distalkpro;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

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
                else if (i == TextToSpeech.LANG_MISSING_DATA ||
                        i == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(MainActivity.context, "Error initializing text to speech!", Toast.LENGTH_LONG).show();
                    return;
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
