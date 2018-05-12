package ru.aacidov.distalkpro;

import android.graphics.Bitmap;

/**
 * Created by aacidov on 21.03.2018.
 */

public class Category {
    String label;
    int id;
    private Bitmap preview;

    public Category(int id, String label, Bitmap preview) {
        this.id = id;
        this.label = label;
        this.preview = preview;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void seLabel(String label) {
        this.label = label;
    }

    public Bitmap getPreview() {
        return preview;
    }
}
