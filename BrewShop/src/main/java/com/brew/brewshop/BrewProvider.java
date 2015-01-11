package com.brew.brewshop;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.RecipeList;
import com.brew.brewshop.xml.BeerXMLWriter;

import java.io.File;
import java.io.IOException;

public class BrewProvider extends ContentProvider {
    public static final String SCHEME = "content";
    public static final String AUTHORITY = "com.brew.brewshop.brewprovider";
    public static final String RECIPES_XML = "recipes.xml";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_ID_DELIMITER = ",";

    private static final String TAG = BrewProvider.class.getName();

    private BrewStorage mStorage;

    @Override
    public boolean onCreate() {
        mStorage = new BrewStorage(getContext());
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        RecipeList recipes = mStorage.retrieveRecipes();
        RecipeList toSave = new RecipeList();
        String idString = uri.getQueryParameter(RECIPE_ID);
        String[] idArray = idString.split(RECIPE_ID_DELIMITER);
        for (String id : idArray) {
            Log.d(TAG, "Adding recipe ID: " + Integer.parseInt(id));
            toSave.add(recipes.findById(Integer.parseInt(id)));
        }

        ParcelFileDescriptor descriptor = null;
        try {
            File outputDir = getContext().getCacheDir();
            File outputFile = File.createTempFile("recipes-", ".xml", outputDir);
            BeerXMLWriter beerXMLWriter = new BeerXMLWriter(getContext(), toSave.toArray(new Recipe[toSave.size()]));
            beerXMLWriter.writeRecipes(outputFile);
            descriptor = ParcelFileDescriptor.open(outputFile, ParcelFileDescriptor.MODE_READ_WRITE);
            outputFile.delete();//Not actually deleted until the descriptor is closed.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return descriptor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "application/xml";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
