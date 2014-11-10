package com.brew.brewshop.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.RecipeList;
import com.google.gson.Gson;

import java.util.Collections;

public class BrewStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "brew.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPES_TABLE = "recipes";
    private static final String ID_COLUMN = "_id";
    private static final String DATA_COLUMN = "data";

    private static RecipeList sRecipeCache;

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
        String command = "create table " + RECIPES_TABLE +
                         " (" + ID_COLUMN +
                         " integer primary key autoincrement, " + DATA_COLUMN +
                         " text not null);";
        db.execSQL(command);
    }

    public void createRecipe(Recipe recipe) {
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATA_COLUMN, json);
        db.insert(RECIPES_TABLE, DATA_COLUMN, cv);
        readNewId(db, recipe);
        sRecipeCache = null;
    }

    private void readNewId(SQLiteDatabase db, Recipe recipe) {
        String query = "select " + ID_COLUMN +
                       " from " + RECIPES_TABLE +
                       " order by " + ID_COLUMN +
                       " desc limit 1";
        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
            recipe.setId(id);
        }
        db.close();
    }

    public RecipeList retrieveRecipes() {
        if (sRecipeCache != null) {
            return sRecipeCache;
        }
        RecipeList recipes = new RecipeList();
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
            cursor.close();
        }
        Collections.sort(recipes, new RecipeComparator());
        sRecipeCache = recipes;
        return sRecipeCache;
    }

    public Recipe retrieveRecipe(Recipe recipe) {
        Gson gson = new Gson();
        SQLiteDatabase db=this.getReadableDatabase();
        String query = "select * from " + RECIPES_TABLE +
                       " where " + ID_COLUMN +
                       "=" + recipe.getId();
        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor != null ) {
            if (cursor.moveToFirst()) {
                String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                recipe = gson.fromJson(data, Recipe.class);
            }
            cursor.close();
        }
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
        sRecipeCache = null;
    }

    public void deleteRecipe(Recipe recipe) {
        int id = recipe.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(RECIPES_TABLE, ID_COLUMN + "=" + id, null);
        db.close();
        sRecipeCache = null;
    }

    public String getJson(Recipe recipe) {
        Gson gson = new Gson();
        return gson.toJson(recipe);
    }
}