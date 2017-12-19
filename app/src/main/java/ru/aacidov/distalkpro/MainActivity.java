package ru.aacidov.distalkpro;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;
import ru.aacidov.disfeedback.*;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    private FileStorage mfs;
    private GridViewController gvc;
    private MenuItemImpl mni;
    private FeedBack fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;


        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        YandexMetriсaHelper.activate(getApplication(), getString(R.string.appmetrica_key));

        mfs = FileStorage.getInstance();

        fb = new FeedBack(this);
        Resources res = getResources();

        gvc = GridViewController.getInstance();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        SizeController.getInstance().update();
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                boolean checked = Cookie.getInstance().getChooseDirectoryShow();

                mni = (MenuItemImpl) menu.findItem(R.id.action_show_create_directory);
                mni.setChecked(checked);

                mni = (MenuItemImpl) menu.findItem(R.id.action_choose_directory);
                mni.setShowAsAction(checked?MenuItem.SHOW_AS_ACTION_ALWAYS:MenuItem.SHOW_AS_ACTION_NEVER);
            }
        });


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
        if (id == R.id.action_set_size) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.action_set_size);
            final SeekBar input = new SeekBar(this);

            input.setMax(400);
            input.setProgress(SizeController.getInstance().size);
            builder.setView(input);


            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i ){
                    int size = input.getProgress()+100;
                    SizeController.getInstance().setSize(size);
                    gvc.load();
                }
            });
            builder.show();

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

        if (id==R.id.action_show_create_directory){
            boolean checked = !item.isChecked();
            item.setChecked(checked);
            int showStatus = checked?MenuItem.SHOW_AS_ACTION_ALWAYS:MenuItem.SHOW_AS_ACTION_NEVER;
            mni.setShowAsAction(showStatus);
            Cookie.getInstance().setChooseDirectoryShow(checked);
            YandexMetriсaHelper.showCreateDirectoryButton(checked);
            return true;
        }

        if (id==R.id.action_feedback){

            fb.openFeedbackForm();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
