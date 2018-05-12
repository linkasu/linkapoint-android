package ru.aacidov.distalkpro;

import android.graphics.Bitmap;

/**
 * Created by aacidov on 26.10.16.
 */

public class ImageItem {
    private Bitmap image;
    private String title;
    private int id;
    public ImageItem(Bitmap image, String title, int id) {
        super();
        this.image = image;
        this.title = title;
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
