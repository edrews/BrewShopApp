package com.brew.brewshop.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "brew.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPES_TABLE_NAME = "recipes";
    private static final String KEY_NAME = "name";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    private void createRecipes(SQLiteDatabase db) {
        String createCommand = new StringBuilder()
                .append("CREATE TABLE ")
                .append(RECIPES_TABLE_NAME)
                .append(" (")
                .append(KEY_NAME)
                .append(" TEXT);")
                .toString();
        db.execSQL(createCommand);
    }
}