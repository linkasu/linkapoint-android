package ru.aacidov.distalkpro;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aacidov on 12.11.16.
 */
public class Cookie {

    private static final String STORAGENAME = "STORAGE";
    private final SharedPreferences sharedPref;
    private final String icds;
    static Cookie instance = null;
    private SharedPreferences.Editor editor;

    static Cookie getInstance(){
        if (instance==null){
            instance = new Cookie();
        }
        return instance;
    }

    public Cookie(){
        Context context = MainActivity.context;
        sharedPref = context.getSharedPreferences(STORAGENAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        icds = context.getString(R.string.isChooseDirectoryShow);
    }

    public void setChooseDirectoryShow (boolean show){

        editor.putBoolean(icds, show);
        editor.commit();

    }

    public boolean getChooseDirectoryShow(){
        return sharedPref.getBoolean(icds, true);
    }
}
