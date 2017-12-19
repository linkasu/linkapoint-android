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

import com.yandex.metrica.YandexMetrica;

import java.io.IOException;
import java.util.logging.Logger;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;


/**
 * Created by aacidov on 27.10.16.
 */
public class GridViewController implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private FileStorage mfs;
    private TTS tts;
    private GridView gv;
    private Context cxt;
    private String[] pictureMenuItems;
    private static GridViewController instance;

    public GridViewController() {
        mfs = FileStorage.getInstance();

        cxt = MainActivity.context;
        tts = TTS.getInstance();
        gv=(GridView) ((Activity)cxt).findViewById(R.id.gridView);
        load();
        gv.setOnItemClickListener(this);
        gv.setOnItemLongClickListener(this);
        pictureMenuItems = cxt.getResources().getStringArray(R.array.picture_menu);
    }

    public static GridViewController getInstance() {
        if(instance==null){
            instance=new GridViewController();
        }
        return instance;
    }

    public int setColumnWidth(int width){
        gv.setColumnWidth(width);
        return width;
    }

    public void load() {
        ImageItem[] items = mfs.getImages();
        gv.setAdapter(new ImageAdapter(cxt, R.layout.grid_view_item, items));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String text     = mfs.getText(i);

        tts.speak(text);
        YandexMetriсaHelper.saidEvent(text);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int id, long l) {
        YandexMetriсaHelper.pictureMenuEvent();
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

                        builder.setView(input);


                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i ){
                                String title = input.getText().toString();
                                mfs.rename(id, title);
                                YandexMetriсaHelper.renameEvent(title);
                            }
                        });
                        builder.show();
                        break;
                    case 1: //Delete
                        mfs.delete(id);
                        YandexMetriсaHelper.deleteEvent();
                        break;
                }
                load();
            }
        });


        builderSingle.show();
        return false;
    }

}
