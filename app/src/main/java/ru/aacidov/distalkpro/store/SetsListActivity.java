package ru.aacidov.distalkpro.store;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import ru.aacidov.distalkpro.ImageItem;
import ru.aacidov.distalkpro.R;
import ru.aacidov.distalkpro.utils.YandexMetriсaHelper;

/**
 * Created by aacidov on 26.03.2018.
 */

public class SetsListActivity  extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView categoryListView;
    private PicServer picServer;
    private StoreCategoryAdapter adapter;
    private SetsListActivity context;
    private ProgressBar progressBar;
    private View connectionErrorView;
    private Button tryagainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.store_toolbar);

        toolbar.setLogo(R.drawable.ic_shopping_cart_24px);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(toolbar);

        context = this;

        categoryListView = (ListView) findViewById(R.id.category_listview);
        categoryListView.setOnItemClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        connectionErrorView = findViewById(R.id.connection_error);

        tryagainButton = (Button) findViewById(R.id.try_button);

        tryagainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });

        picServer = PicServer.getInstance();
        load();
        YandexMetriсaHelper.storeOpenEvent();
    }

    private void load() {
        connectionErrorView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        picServer.getCategories(new PicServer.OnPicServerResponce(){
            @Override
            public void onImageItems(ImageItem[] items) {
                super.onImageItems(items);
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new StoreCategoryAdapter(context, R.layout.store_category_item, items);
                categoryListView.setAdapter(adapter);
            }

            @Override
            public void onError(VolleyError error) {
                super.onError(error);

                progressBar.setVisibility(View.INVISIBLE);
                connectionErrorView.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ImageItem category = adapter.getItemAtPosition(i);
        Intent intent = new Intent(this, SetPreviewActivity.class);

        Bundle params = new Bundle();
        params.putString("category",  category.getTitle());
        intent.putExtras(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(intent);
        } else {
            Toast.makeText(context, R.string.version_doesnt_support, Toast.LENGTH_LONG).show();
        }
    }

}
