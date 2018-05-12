package ru.aacidov.distalkpro.store;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.aacidov.distalkpro.ImageItem;
import ru.aacidov.distalkpro.MainActivity;

/**
 * Created by aacidov on 21.03.2018.
 */

public class PicServer {
    private final RequestQueue queue;
    private String server = "http://picserver.linka.su";

    private static PicServer instance;

    public static PicServer getInstance() {
        if (PicServer.instance==null) PicServer.instance = new PicServer();
        return PicServer.instance;
    }

    PicServer (){
        queue =  Volley.newRequestQueue(MainActivity.context);
    }

    private void getURL(String path, final OnPicServerResponce listener){
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, server + "/" + path, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                listener.onResponce(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }

        }
        );
        queue.add(jor);
    }



    public void getCategories (final OnPicServerResponce listener) {
        getURL("categories", new OnPicServerResponce() {
            @Override
            public void onResponce(JSONObject responce) {
                listener.onResponce(responce);
                try {
                    JSONArray data = responce.getJSONArray("categories");

                    int size = data.length();
                    ImageItem[] items = new ImageItem[size];

                    for (int i = 0; i < size; i++) {
                        JSONObject element = data.getJSONObject(i);
                        byte[] bytes = Base64.decode(element.getString("preview"), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        String title = element.getString("title");
                        items[i] = new ImageItem(bitmap, title, i);
                    }
                    listener.onImageItems(items);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                super.onError(error);
                listener.onError(error);
            }
        });
    }

    public void getCategory(String title, final OnPicServerResponce listener) {

        getURL("category/"+title, new OnPicServerResponce() {
            @Override
            public  void onResponce(JSONObject responce) {
                listener.onResponce(responce);
                try {
                    JSONArray data = responce.getJSONArray("pictures");

                    int size = data.length();
                    ImageItem[] items = new ImageItem[size];

                    for (int i = 0; i < size; i++) {
                        JSONObject element = data.getJSONObject(i);
                        byte[] bytes = Base64.decode(element.getString("preview"), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        String title = element.getString("title");
                        items[i] = new ImageItem(bitmap, title, i);
                    }
                    listener.onImageItems(items);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

                    @Override
                    public void onError(VolleyError error) {
                        super.onError(error);
                        listener.onError(error);
                    }
                }
        );
    }


    public static class OnPicServerResponce {
        public void onResponce(JSONObject response) {
        }

        public void onError(VolleyError error) {
        }

        public void onImageItems(ImageItem[] items) {

        }
    }
}
