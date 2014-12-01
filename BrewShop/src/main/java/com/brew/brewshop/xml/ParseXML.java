package com.brew.brewshop.xml;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.xml.BeerXML;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Doug Edey on 21/11/14.
 */
public class ParseXML {
//    public static Recipe[] readBeerXMLRecipe(String filename) {
//        File recipeFile = new File(filename);
//        return BeerXML.readFile(recipeFile);
//    }
//
//    public Recipe[] readBeerXMLRecipe(InputStream inputStream) {
//        return BeerXML.readInputStream(inputStream);
//    }

    public static String checkRecipeType(String buffer) {
        if (buffer.indexOf("BeerXML Format") > -1 || buffer.indexOf("<RECIPES>") > -1 )
            return "beerxml";
        if (buffer.indexOf("STRANGEBREWRECIPE") > -1)
            return "sb";
        if ((buffer.indexOf("generator=\"qbrew\"") > -1) || (buffer.indexOf("application=\"qbrew\"") > -1))
            return "qbrew";

        return null;
    }

    private static String checkFileType(File recipeFile) {

        if (recipeFile.getAbsolutePath().endsWith(".rec"))
            return "promash";

        // let's open it up and have a peek
        // we'll only read 10 lines

        if (recipeFile.getAbsolutePath().endsWith(".qbrew") || (recipeFile.getAbsolutePath().endsWith(".xml"))) {
            try {
                FileReader in = new FileReader(recipeFile);
                BufferedReader inb = new BufferedReader(in);
                String c;
                int i = 0;
                while ((c = inb.readLine()) != null && i < 10) {
                    // check for an opening tag of Recipes too
                    if (c.indexOf("BeerXML Format") > -1 || c.indexOf("<RECIPES>") > -1 )
                        return "beerxml";
                    if (c.indexOf("STRANGEBREWRECIPE") > -1)
                        return "sb";
                    if ((c.indexOf("generator=\"qbrew\"") > -1) || (c.indexOf("application=\"qbrew\"") > -1))
                        return "qbrew";
                    i++;
                }
            } catch (Exception e) {
                Log.e("BrewShop", "Error opening XML File", e);
            }
        }
        return "";
    }

}
