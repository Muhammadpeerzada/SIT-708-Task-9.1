package com.example.lostandfound.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.lostandfound.model.Post;
import com.example.lostandfound.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_POST_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", Util.TABLE_NAME, Util.POST_ID, Util.POST_NAME, Util.PHONE_NUMBER, Util.DESCRIPTION, Util.STATE, Util.DATE, Util.LOCATION, Util.LATITUDE, Util.LONGITUDE);
        sqLiteDatabase.execSQL(CREATE_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_POST_TABLE = String.format("DROP TABLE IF EXISTS %s", Util.TABLE_NAME);
        sqLiteDatabase.execSQL(DROP_POST_TABLE);
        onCreate(sqLiteDatabase);
    }

    public long insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.POST_NAME, post.getPostName());
        contentValues.put(Util.PHONE_NUMBER, post.getPhoneNumber());
        contentValues.put(Util.DESCRIPTION, post.getDescription());
        contentValues.put(Util.STATE, post.getState());
        contentValues.put(Util.DATE, post.getDate());
        contentValues.put(Util.LOCATION, post.getLocation());
        contentValues.put(Util.LATITUDE, post.getLatitude());
        contentValues.put(Util.LONGITUDE, post.getLongitude());
        long newRowId = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {Util.POST_ID, Util.POST_NAME, Util.PHONE_NUMBER, Util.DESCRIPTION, Util.STATE, Util.DATE, Util.LOCATION, Util.LATITUDE, Util.LONGITUDE};
        Cursor cursor = db.query(Util.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(Util.POST_ID));
                Post post = getPostById(id);
                posts.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posts;
    }



    public Post getPostById(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {Util.POST_ID, Util.POST_NAME, Util.PHONE_NUMBER, Util.DESCRIPTION, Util.STATE, Util.DATE, Util.LOCATION, Util.LATITUDE, Util.LONGITUDE};
        String selection = Util.POST_ID + "=?";
        String[] selectionArgs = {String.valueOf(postId)};
        Cursor cursor = db.query(Util.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Post post = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Util.POST_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Util.POST_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Util.PHONE_NUMBER));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(Util.DESCRIPTION));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(Util.STATE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(Util.DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(Util.LOCATION));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow(Util.LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow(Util.LONGITUDE));

            post = new Post(id, name, phoneNumber, description, state, date, location, latitude, longitude);
        }

        cursor.close();
        return post;
    }


    public int deletePost(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = Util.POST_ID + "=?";
        String[] selectionArgs = {String.valueOf(postId)};
        int count = db.delete(Util.TABLE_NAME, selection, selectionArgs);
        db.close();
        return count;
    }

    public int getCountOfInvalidLocations() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + Util.TABLE_NAME +
                " WHERE (" + Util.LATITUDE + " IS NULL OR " + Util.LATITUDE + " = 0) AND (" +
                Util.LONGITUDE + " IS NULL OR " + Util.LONGITUDE + " = 0)";

        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }
}
