package ru.aacidov.distalkpro;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;

/**
 * Created by aacidov on 26.03.2018.
 */

public class SetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private int categoryId;
    private TTS tts;
    private Context cxt;
    private GridView gv;
    private String[] pictureMenuItems;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_choose_directory);


        cxt = this;
        gv=(GridView) findViewById(R.id.gridView);
        gv.setOnItemClickListener(this);
        gv.setOnItemLongClickListener(this);

        pictureMenuItems = getResources().getStringArray(R.array.picture_menu);
        load();

    }

    private void load() {
        final Category[] items = DB.getInstance().getCategories();

        ImageItem[] imageItems = new ImageItem[items.length];

        for (int i = 0; i<items.length; i++){
            Category item = items[i];
            Bitmap preview = item.getPreview();
            if (preview == null) preview = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            imageItems[i] = new ImageItem(preview, item.getLabel(), item.getId());
        }
        adapter = new ImageAdapter(this, R.layout.grid_view_item, imageItems);

        gv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int id = adapter.getItemAtPosition(i).getId();
        GridViewController.getInstance().setCategory(id);
        GridViewController.getInstance().load();
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        final ImageItem item = ((ImageAdapter) adapterView.getAdapter()).getItemAtPosition(i);

        if(item.getId()==1){
            Toast.makeText(cxt, R.string.cant_edit_set, Toast.LENGTH_LONG).show();
            return true;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(cxt);
        builderSingle.setTitle(R.string.action_set_menu);
        builderSingle.setAdapter(new ArrayAdapter<String>(cxt, R.layout.support_simple_spinner_dropdown_item, pictureMenuItems), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0: //Rename
                        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
                        builder.setTitle(R.string.new_set_title);
                        final EditText input = new EditText(cxt);
                        input.setText(item.getTitle());
                        builder.setView(input);


                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                String title = input.getText().toString();
                                DB.getInstance().editCategory(item.getId(), title);
                                load();
                            }
                        });
                        builder.show();
                        break;
                    case 1: //Delete
                        DB.getInstance().deleteCategory(item.getId());
                        load();
                        break;
                }
            }
        });
        builderSingle.show();
        return false;
    }
}
