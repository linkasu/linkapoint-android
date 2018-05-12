package ru.aacidov.distalkpro;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.yandex.metrica.YandexMetrica;

import java.io.IOException;
import java.util.logging.Logger;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;


/**
 * Created by aacidov on 27.10.16.
 */
public class GridViewController implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private TTS tts;
    private GridView gv;
    private Context cxt;
    private String[] pictureMenuItems;
    private static GridViewController instance;
    public int category = 1;

    public GridViewController() {

        tts = TTS.getInstance();
        cxt = MainActivity.context;
        gv=(GridView) ((Activity)cxt).findViewById(R.id.gridView);
        load();
        gv.setOnItemClickListener(this);
        gv.setOnItemLongClickListener(this);
        pictureMenuItems = cxt.getResources().getStringArray(R.array.picture_menu);

    }

    public static GridViewController getInstance() {

        if(instance==null?true:instance.cxt!=MainActivity.context){
            instance=new GridViewController();
        }
        return instance;
    }

    public int setColumnWidth(int width){
        gv.setColumnWidth(width);
        return width;
    }

    public void load() {
        Log.d(GridViewController.class.getCanonicalName(), "load");
        DB.getInstance();

        ImageItem[] items = DB.getInstance().getPictures(category);
        setAdapter(new ImageAdapter(cxt, R.layout.grid_view_item, items));
    }
    public void setAdapter (ImageAdapter ia){
        gv.setAdapter(ia);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String text     = ((TextView) view.findViewById(R.id.text)).getText().toString();

        tts.speak(text);
        YandexMetriсaHelper.saidEvent(text);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int id, long l) {
        YandexMetriсaHelper.pictureMenuEvent();
        final ImageItem item = ((ImageAdapter) adapterView.getAdapter()).getItemAtPosition(id);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(cxt);
        builderSingle.setTitle(R.string.action_picture_menu);
        builderSingle.setAdapter(new ArrayAdapter<String>(cxt, R.layout.support_simple_spinner_dropdown_item, pictureMenuItems), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0: //Rename
                        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
                        builder.setTitle(R.string.new_image_title);
                        final EditText input = new EditText(cxt);
                        input.setText(item.getTitle());
                        builder.setView(input);


                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i ){
                                String title = input.getText().toString();
                                DB.getInstance().editPicture(item.getId(), title);
                                YandexMetriсaHelper.renameEvent(title);
                                load();
                            }
                        });
                        builder.show();
                        break;
                    case 1: //Delete
                        DB.getInstance().deletePicture(item.getId());
                        YandexMetriсaHelper.deleteEvent();
                        load();
                        break;
                }
                load();
            }
        });


        builderSingle.show();
        return false;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
