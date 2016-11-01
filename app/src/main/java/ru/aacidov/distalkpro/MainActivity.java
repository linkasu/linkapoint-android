package ru.aacidov.distalkpro;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.yandex.metrica.YandexMetrica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;

public class MainActivity extends AppCompatActivity {

    private FileStorage mfs;
    private GridViewController gvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        YandexMetriсaHelper.activate(getApplication(), getString(R.string.appmetrica_key));


        TTS tts = new TTS(this);
        Resources res = getResources();


        mfs = new FileStorage(this, this, res.getStringArray(R.array.pictitles));

        GridView gv = (GridView) findViewById(R.id.gridView);

        gvc = new GridViewController(this, gv, mfs, tts);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private static final int PICK_PHOTO_FOR_AVATAR = 0;

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            try {

                final InputStream inputStream = this.getContentResolver().openInputStream(data.getData());

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.image_title);
                final EditText input = new EditText(this);

                builder.setView(input);


                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String title = input.getText().toString();
                        try {
                            mfs.copyFile(inputStream, title);
                            gvc.load();
                            YandexMetriсaHelper.addPictureEvent(title);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } catch (FileNotFoundException e) {


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_picture) {
            pickImage();
            return true;
        }
        if (id==R.id.action_create_directory){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_directory_title);
            final EditText input = new EditText(this);

            builder.setView(input);


            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i ){
                    String title = input.getText().toString();
                    mfs.createDirectory(title);
                    mfs.setCurrentDirectory(title);
                    gvc.load();
                    YandexMetriсaHelper.createDirectoryEvent(title);
                }
            });
            builder.show();
            return true;
        }
        if(id== R.id.action_choose_directory){
            final String[] directories = mfs.getDirectories();
            final String[] items = new String[directories.length+1];
            items[0]= getString(R.string.root);
            for (int i = 1; i < items.length; i++) {
                items[i] = directories[i-1];
            }

            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(R.string.action_choose_directory);
            builderSingle.setAdapter(mArrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String dir = i==0?"/":directories[i-1];
                    mfs.setCurrentDirectory(dir);
                    gvc.load();
                    YandexMetriсaHelper.chooseDirectoryEvent(dir);
                }
            });

            builderSingle.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
