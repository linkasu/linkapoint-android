package ru.aacidov.distalkpro;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;

public class DisTalkPro extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        YandexMetriсaHelper.activate(getResources().getString(R.string.appmetrica_key)); }

    @NonNull
    public static Context getAppContext() {
        return appContext;
    }
}
