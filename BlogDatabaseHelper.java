package com.example.blogmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BlogDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BlogDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BLOGS = "blogs";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_AUTHOR = "author";
    private static final String COL_CONTENT = "content";
    private static final String COL_DATE = "date";

    public BlogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_BLOGS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_AUTHOR + " TEXT NOT NULL, " +
                COL_CONTENT + " TEXT NOT NULL, " +
                COL_DATE + " TEXT NOT NULL)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOGS);
        onCreate(db);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a", Locale.getDefault()
        ).format(new Date());
    }

    // CREATE: Insert a new blog
    public long addBlog(String title, String author,
                        String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TITLE, title);
        values.put(COL_AUTHOR, author);
        values.put(COL_CONTENT, content);
        values.put(COL_DATE, getCurrentDate());

        long result = db.insert(TABLE_BLOGS, null, values);
        db.close();
        return result;
    }

    // READ: Retrieve all blogs
    public ArrayList<Blog> getAllBlogs() {
        ArrayList<Blog> blogs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_BLOGS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_TITLE));
                String author = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_AUTHOR));
                String content = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_CONTENT));
                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_DATE));

                blogs.add(new Blog(
                        id, title, author, content, date));
            }
        } finally {
            cursor.close();
            db.close();
        }

        return blogs;
    }

    // UPDATE: Edit an existing blog
    public int updateBlog(int id, String title, String author,
                          String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TITLE, title);
        values.put(COL_AUTHOR, author);
        values.put(COL_CONTENT, content);
        values.put(COL_DATE, getCurrentDate());

        int result = db.update(
                TABLE_BLOGS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result;
    }

    // DELETE: Remove a blog
    public int deleteBlog(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_BLOGS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result;
    }
}