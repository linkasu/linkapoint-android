package ru.aacidov.distalkpro;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by aacidov on 26.10.16.
 */
public class TTS {
    private TextToSpeech tts;

    public TTS(Context cxt) {
        tts = new TextToSpeech(cxt, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.setLanguage(Locale.getDefault());
            }
        });

    }

    public void speak (String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
