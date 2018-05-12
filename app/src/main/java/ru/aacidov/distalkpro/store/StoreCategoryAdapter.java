package ru.aacidov.distalkpro.store;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.aacidov.distalkpro.Category;
import ru.aacidov.distalkpro.ImageItem;
import ru.aacidov.distalkpro.R;

/**
 * Created by aacidov on 21.03.2018.
 */

public class StoreCategoryAdapter  extends ArrayAdapter {


    private final int layoutResourceId;
    private final Context context;
    private final ImageItem[] data;

    public StoreCategoryAdapter(Context context, int layoutResourceId, ImageItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StoreCategoryAdapter.ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new StoreCategoryAdapter.ViewHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            ImageView image = holder.image = (ImageView) row.findViewById(R.id.list_image);

            row.setTag(holder);
        } else {
            holder = (StoreCategoryAdapter.ViewHolder) row.getTag();
        }
        ImageItem item = data[position];
        holder.title.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    public ImageItem getItemAtPosition (int position) {
        return data[position];
    }


    private class ViewHolder  {
        public ImageView image;
        public TextView title;
    }
}
