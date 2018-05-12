package ru.aacidov.distalkpro;

import android.content.Context;

/**
 * Created by aacidov on 18.12.2017.
 */

class SizeController {
    private static SizeController instance;

    public int size;
    private Context context;

    SizeController()
    {
        context = MainActivity.context;
       size = (Cookie.getInstance().getImageSize());
    }

    public static SizeController getInstance() {
        if(instance==null?true:instance.context!=MainActivity.context) {
            instance = new SizeController();
        }
            return instance;
    }

    public int setSize(int s){
        size = s;
        Cookie.getInstance().setImageSize(s);
        GridViewController.getInstance().setColumnWidth(s+5);
        return s;
    }
    public void update(){
        setSize(size);
    }
}
