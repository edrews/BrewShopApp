package com.brew.brewshop.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.RecipeList;
import com.google.gson.Gson;

import java.util.Collections;

public class BrewStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "brew.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPES_TABLE = "recipes";
    private static final String INVENTORY_TABLE = "inventory";
    private static final String ID_COLUMN = "_id";
    private static final String DATA_COLUMN = "data";

    private static RecipeList sRecipeCache;
    private static InventoryList sInventoryCache;

    public BrewStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("drop table if exists " + RECIPES_TABLE);
        db.execSQL("drop table if exists " + INVENTORY_TABLE);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        createTable(db, RECIPES_TABLE);
        createTable(db, INVENTORY_TABLE);
    }

    private void createTable(SQLiteDatabase db, String table) {
        String command = "create table if not exists " + table + " (" +
                ID_COLUMN + " integer primary key autoincrement, " +
                DATA_COLUMN + " text not null);";
        db.execSQL(command);
    }

    public void createRecipe(Recipe recipe) {
        create(recipe, RECIPES_TABLE);
        sRecipeCache = null;
    }

    public void createInventoryItem(InventoryItem item) {
        create(item, INVENTORY_TABLE);
        sInventoryCache = null;
    }

    private void create(Storeable storeable, String table) {
        Gson gson = new Gson();
        String json = gson.toJson(storeable);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATA_COLUMN, json);
        db.insert(table, DATA_COLUMN, cv);
        readNewId(db, storeable, table);
    }

    private void readNewId(SQLiteDatabase db, Storeable storeable, String table) {
        String query = "select " + ID_COLUMN +
                       " from " + table +
                       " order by " + ID_COLUMN +
                       " desc limit 1";
        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
            storeable.setId(id);
        }
        db.close();
    }

    public RecipeList retrieveRecipes() {
        if (sRecipeCache != null) {
            return sRecipeCache;
        }
        RecipeList recipes = new RecipeList();
        Gson gson = new Gson();
        SQLiteDatabase db = this.getReadableDatabase();
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

    public InventoryList retrieveInventory() {
        if (sInventoryCache != null) {
            return sInventoryCache;
        }
        InventoryList inventory = new InventoryList();
        Gson gson = new Gson();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + INVENTORY_TABLE, new String[]{});
        if (cursor != null ) {
            if  (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
                    String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                    InventoryItem item = gson.fromJson(data, InventoryItem.class);
                    item.setId(id);
                    inventory.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Collections.sort(inventory, new InventoryComparator());
        sInventoryCache = inventory;
        return sInventoryCache;
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
        update(recipe, RECIPES_TABLE);
        sRecipeCache = null;
    }

    public void updateInventoryItem(InventoryItem item) {
        update(item, INVENTORY_TABLE);
        sInventoryCache = null;
    }

    private void update(Storeable storeable, String table) {
        String filter = ID_COLUMN + "=" + storeable.getId();
        ContentValues args = new ContentValues();
        String json = getJson(storeable);
        args.put(DATA_COLUMN, json);
        SQLiteDatabase db = getWritableDatabase();
        db.update(table, args, filter, null);
        db.close();
    }

    public void deleteRecipe(Recipe recipe) {
        delete(recipe, RECIPES_TABLE);
        sRecipeCache = null;
    }

    public void deleteInventoryItem(InventoryItem item) {
        delete(item, INVENTORY_TABLE);
        sInventoryCache = null;
    }

    private void delete(Storeable storeable, String table) {
        int id = storeable.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, ID_COLUMN + "=" + id, null);
        db.close();
    }

    private String getJson(Storeable storeable) {
        Gson gson = new Gson();
        return gson.toJson(storeable);
    }
}