package ru.aacidov.distalkpro.store;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;

import ru.aacidov.distalkpro.DB;
import ru.aacidov.distalkpro.ImageAdapter;
import ru.aacidov.distalkpro.ImageItem;
import ru.aacidov.distalkpro.R;
import ru.aacidov.distalkpro.TTS;
import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;

/**
 * Created by aacidov on 26.03.2018.
 */

public class SetPreviewActivity  extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private PicServer picServer;
    private GridView previewGridView;
    private ImageAdapter adapter;
    private SetPreviewActivity context;
    private FloatingActionButton downloadButtton;
    private String currentCategoryLabel;
    private ImageItem[] items;
    private boolean downloaded;
    private ProgressBar progressBar;
    private View connectionErrorView;
    private Button tryagainButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_category_preview);

        toolbar = (Toolbar) findViewById(R.id.toolbar) ;
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        context = this;

        previewGridView = (GridView) findViewById(R.id.preview_gridview);
        downloadButtton = (FloatingActionButton) findViewById(R.id.download_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        connectionErrorView = findViewById(R.id.connection_error);
        tryagainButton = (Button) findViewById(R.id.try_button);


        tryagainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });


        downloadButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });

        previewGridView.setOnItemClickListener(this);

        Bundle params = getIntent().getExtras();
        if(params!=null) {
            picServer = PicServer.getInstance();
            currentCategoryLabel = params.getString("category");
            load();
        }
        YandexMetriсaHelper.storeOpenSetEvent();

    }



    public void download(){
        if (downloaded) return;
        Toast.makeText(context, R.string.downloading, Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            downloadButtton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        }
        DB db = DB.getInstance();


        int id = db.createCategory(currentCategoryLabel);

        for (ImageItem item : items){
            Bitmap bitmap = item.getImage();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bytes = bos.toByteArray();
            DB.getInstance().createPicture(item.getTitle(), bytes, id);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            downloadButtton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_light)));
        }
        Toast.makeText(context, R.string.downloaded, Toast.LENGTH_LONG).show();


        downloaded = true;
        YandexMetriсaHelper.storeDownloadEvent();

    }
    void load(){
        String category = currentCategoryLabel;
        toolbar.setTitle(category);

        connectionErrorView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        picServer.getCategory(category, new PicServer.OnPicServerResponce(){
            @Override
            public void onImageItems(ImageItem[] items) {
                super.onImageItems(items);
                progressBar.setVisibility(View.INVISIBLE);
                context.items = items;
                adapter = new ImageAdapter(context, R.layout.grid_view_item, items);
                previewGridView.setAdapter(adapter);
            }

                    @Override
                    public void onError(VolleyError error) {
                        super.onError(error);
                        progressBar.setVisibility(View.INVISIBLE);
                        connectionErrorView.setVisibility(View.VISIBLE);

                    }
                }
        );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TTS.getInstance().speak(adapter.getItemAtPosition(i).getTitle());
    }
}
