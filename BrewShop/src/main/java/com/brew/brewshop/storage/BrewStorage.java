package com.brew.brewshop.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brew.brewshop.storage.recipes.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class BrewStorage extends SQLiteOpenHelper {
    private static final String TAG = BrewStorage.class.getName();
    private static final String DATABASE_NAME = "brew.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPES_TABLE = "recipes";
    private static final String ID_COLUMN = "_id";
    private static final String DATA_COLUMN = "data";

    private List<Recipe> mRecipeCache;

    public BrewStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("drop table if exists " + RECIPES_TABLE);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        String createCommand = new StringBuilder()
                .append("create table ")
                .append(RECIPES_TABLE)
                .append(" (")
                .append(ID_COLUMN)
                .append(" integer primary key autoincrement, ")
                .append(DATA_COLUMN)
                .append(" text not null);")
                .toString();
        db.execSQL(createCommand);
    }

    public void createRecipe(Recipe recipe) {
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATA_COLUMN, json);
        db.insert(RECIPES_TABLE, DATA_COLUMN, cv);
        readNewId(db, recipe);
        mRecipeCache = null;
    }

    private void readNewId(SQLiteDatabase db, Recipe recipe) {
        StringBuilder builder = new StringBuilder()
                .append("select ").append(ID_COLUMN)
                .append(" from ").append(RECIPES_TABLE)
                .append(" order by ").append(ID_COLUMN)
                .append(" desc limit 1");
        Cursor cursor = db.rawQuery(builder.toString(), new String[]{});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
            recipe.setId(id);
        }
        db.close();
    }

    public List<Recipe> retrieveRecipes() {
        if (mRecipeCache != null) {
            return mRecipeCache;
        }
        List<Recipe> recipes = new ArrayList<Recipe>();
        Gson gson = new Gson();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RECIPES_TABLE, new String[]{});
        if (cursor != null ) {
            if  (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
                    String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                    Recipe recipe = gson.fromJson(data, Recipe.class);
                    recipe.setId(id);
                    recipes.add(recipe);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        mRecipeCache = recipes;
        return mRecipeCache;
    }

    public Recipe retrieveRecipe(Recipe recipe) {
        Gson gson = new Gson();
        SQLiteDatabase db=this.getReadableDatabase();
        StringBuilder builder = new StringBuilder()
                .append("select * from ").append(RECIPES_TABLE).append(" where " )
                .append(ID_COLUMN).append("=").append(recipe.getId());
        Cursor cursor = db.rawQuery(builder.toString(), new String[]{});
        if (cursor != null ) {
            if (cursor.moveToFirst()) {
                String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                recipe = gson.fromJson(data, Recipe.class);
            }
        }
        cursor.close();
        return recipe;
    }

    public void updateRecipe(Recipe recipe) {
        String filter = ID_COLUMN + "=" + recipe.getId();
        ContentValues args = new ContentValues();
        String json = getJson(recipe);
        args.put(DATA_COLUMN, json);
        SQLiteDatabase db = getWritableDatabase();
        db.update(RECIPES_TABLE, args, filter, null);
        db.close();
        mRecipeCache = null;
    }

    public void deleteRecipe(Recipe recipe) {
        int id = recipe.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(RECIPES_TABLE, ID_COLUMN + "=" + id, null);
        db.close();
        mRecipeCache = null;
    }

    public String getJson(Recipe recipe) {
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(recipe);
        //Log.d(TAG, "Updating recipe: " + json);
        return json;
    }
}