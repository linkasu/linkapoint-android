package ru.aacidov.distalkpro.utils;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;

import ru.aacidov.distalkpro.DisTalkPro;


/**
 * Created by aacidov on 29.10.16.
 */
public class YandexMetriсaHelper {
    public static void activate( String key){
        Application application = (Application) DisTalkPro.getAppContext();
        YandexMetrica.activate(application,key);

        YandexMetrica.enableActivityAutoTracking(application);
    }
    public static void saidEvent(String text){
        YandexMetrica.reportEvent("said", "{\"text\":\""+text+"\"}");
    }

    public static void pictureMenuEvent() {
        YandexMetrica.reportEvent("open picture menu");
    }

    public static void  openTTSSettings(){
        YandexMetrica.reportEvent("Open TTS Settings");
    }
    public static void  openTTSInstall(){
        YandexMetrica.reportEvent("Open TTS install");
    }
    public static void renameEvent(String text) {
        YandexMetrica.reportEvent("rename", "{\"text\":\""+text+"\"}");
    }

    public static void deleteEvent() {
        YandexMetrica.reportEvent("delete");
    }

    public static void addPictureEvent(String text) {
        YandexMetrica.reportEvent("add picture", "{\"text\":\""+text+"\"}");
    }

    public static void createDirectoryEvent(String text) {
        YandexMetrica.reportEvent("create category", "{\"text\":\""+text+"\"}");
    }

    public static void chooseDirectoryEvent(String text) {
        YandexMetrica.reportEvent("choose category", "{\"text\":\""+text+"\"}");
    }

    public static void showCreateDirectoryButton(boolean checked) {
        YandexMetrica.reportEvent("show choose directory button", "{\"on\":"+checked+"}");
    }

    public static void feedbackEvent(String email, String text) {
        YandexMetrica.reportEvent("feedback", "{\"text\":\""+email+":"+text+"\"}");

    }
    public static void storeOpenEvent() {
        YandexMetrica.reportEvent("open store");
    }
    public static void storeOpenSetEvent() {
        YandexMetrica.reportEvent("open set");
    }
    public static void storeDownloadEvent() {
        YandexMetrica.reportEvent("download set");
    }

}
