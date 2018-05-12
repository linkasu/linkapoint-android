package ru.aacidov.distalkpro;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.logging.Logger;

import static ru.aacidov.distalkpro.MainActivity.context;

/**
 * Created by aacidov on 21.03.2018.
 */

public class DB {
    private static DB instance;
    private final DBHelper dbHelper;
    private String withoutCategory;
    private AssetManager am;
    private String[] titles;

    public static DB getInstance() {
        if (DB.instance==null) DB.instance = new DB();
        return DB.instance;
    }

    DB() {
        withoutCategory = context.getResources().getString(R.string.root);
        am = context.getAssets();
        titles= context.getResources().getStringArray(R.array.pictitles);
        dbHelper = new DBHelper(context);
    }
    boolean isTableNull(){
        return getPictures(1).length==0;
    }
    void loadDefaultPictures(SQLiteDatabase db){

        String files[] = new String[12];
        for (int i = 0; i < 12; i++) {
            files[i] = i+".jpg";
        }
        for (int i = 0; i < 12; i++) {

            try {
                InputStream in = am.open(files[i]);
                byte[] image = ByteStreams.toByteArray(in);
                createPicture(db, titles[i], image, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int createCategory(String label) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = createCategory(db, label);
        db.close();
        return id;
    }
    private int createCategory(SQLiteDatabase db, String label) {
        ContentValues cv = new ContentValues();
        cv.put("label", label);
        return (int) db.insert("categories", null, cv);
    }

    public void createPicture(String label, byte[] image, int category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        createPicture(db, label, image, category);
        db.close();
    }

    public void createPicture(SQLiteDatabase db, String label, byte[] image, int category) {
        String sql                      =   "INSERT INTO pictures (label, category, image) VALUES(?,?,?)";
        SQLiteStatement insertStmt      =   db.compileStatement(sql);
        insertStmt.clearBindings();
        insertStmt.bindString(1, label);
        insertStmt.bindString(2, Integer.toString(category));
        insertStmt.bindBlob(3, image);
        insertStmt.executeInsert();
    }


    public ImageItem[] getPictures(int category) {
        ArrayList<ImageItem> pictures = new ArrayList<ImageItem>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("pictures", new String[]{"label", "category", "image", "id"}, "`category`=?", new String[]{Integer.toString(category)}, null, null,  null);
        if (c.moveToFirst()) {

            int labelColIndex = c.getColumnIndex("label");
            int imageColIndex = c.getColumnIndex("image");

            do {
                byte[] bytes = c.getBlob(imageColIndex);
                ImageItem item = new ImageItem(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), c.getString((labelColIndex)), c.getInt(c.getColumnIndex("id")));
                pictures.add(item);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return  pictures.toArray(new ImageItem[pictures.size()]);
    }



    public void editPicture(int id, String newText) {
        ContentValues cv = new ContentValues();
        cv.put("label", newText);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update("pictures", cv, "id='" + id + "'", null);
        db.close();
    }
    public void deletePicture(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("pictures", "id='" + id + "'", new String[]{});
        db.close();
    }

    public Category[] getCategories() {
        ArrayList<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("categories", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int labelColIndex = c.getColumnIndex("label");
            int idColIndex = c.getColumnIndex("id");

            do {
                int id = c.getInt(idColIndex);
                categories.add(new Category(id, c.getString(labelColIndex), getPreview(id)));
            } while (c.moveToNext());
        }
        db.close();
        c.close();
        return categories.toArray(new Category[categories.size()]);
    }

    private Bitmap getPreview(int id) {
        ArrayList<ImageItem> pictures = new ArrayList<ImageItem>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("pictures", new String[]{ "image"}, "`category`=?", new String[]{Integer.toString(id)}, null, null, null, "1");
        if (c.moveToFirst()) {
            int imageColIndex = c.getColumnIndex("image");

            do {
                byte[] bytes = c.getBlob(imageColIndex);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return null;
    }

    public void editCategory(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put("label", title);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update("categories", cv, "id='" + id + "'", null);
        db.close();
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("categories", "id='" + id + "'", new String[]{});
        db.close();
    }


    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "LINKaShow", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE `pictures` ( `id` INTEGER primary key autoincrement  , `label` VARCHAR(500) NOT NULL , `image` BLOB NOT NULL , `category` INT DEFAULT 1);");
            db.execSQL("CREATE TABLE `categories` ( `id` INTEGER primary key autoincrement  , `label` VARCHAR(200) NOT NULL );");
            DB.getInstance().createCategory(db, withoutCategory);

            DB.getInstance().loadDefaultPictures(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
