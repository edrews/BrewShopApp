package com.brew.brewshop.xml;

import android.test.InstrumentationTestCase;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Recipe;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by doug on 05/12/14.
 */
public class BeerXMLTest extends InstrumentationTestCase {

    public void testReadWrite() {
        BeerXMLReader beerXMLReader = new BeerXMLReader(getInstrumentation().getTargetContext());
        File externalFile = new File("/sdcard/test.xml");
        if (!externalFile.exists()) {
            System.out.println("Couldn't find test file: " + externalFile.getAbsoluteFile());
            return;
        }
        Recipe[] recipeList = null;
        try {
            recipeList = beerXMLReader.readFile(externalFile);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception when reading file: " + e.getMessage());
            return;
        }
        Recipe recipeOne = recipeList[0];
        BeerXMLWriter beerXMLWriter = new BeerXMLWriter(getInstrumentation().getTargetContext(), recipeList);
        try {
            beerXMLWriter.writeRecipes(new File(getInstrumentation().getTargetContext().getExternalCacheDir() + "/test.xml"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("Exception when writing file: " + ioe.getMessage());
            return;
        }

        try {
            recipeList = beerXMLReader.readFile(new File(getInstrumentation().getTargetContext().getExternalCacheDir() + "/test.xml"));
            Recipe recipeTwo = recipeList[0];
            verifyRecipes(recipeOne, recipeTwo);
        } catch (Exception e) {
            fail("Exception when reading back file: " + e.getMessage());
        }
    }

    public void verifyRecipes(Recipe recipeOne, Recipe recipeTwo) {
       assertEquals(recipeOne.getBatchVolume(), recipeTwo.getBatchVolume());
        assertEquals(recipeOne.getBoilTime(), recipeTwo.getBoilTime());
        assertEquals(recipeOne.getBoilVolume(), recipeTwo.getBoilVolume());
        assertEquals(recipeOne.getFg(), recipeTwo.getFg());
        assertEquals(recipeOne.getOg(), recipeTwo.getOg());
        assertEquals(recipeOne.getAbv(), recipeTwo.getAbv());
        assertEquals(recipeOne.getEfficiency(), recipeTwo.getEfficiency());
        assertEquals(recipeOne.getSrm(), recipeTwo.getSrm());
        assertTrue(recipeOne.getStyle().equals(recipeTwo.getStyle()));

        assertTrue(recipeOne.getHops().size() == recipeTwo.getHops().size());
        for (int i = 0; i < recipeOne.getHops().size(); i++) {
            assertTrue(recipeTwo.getHops().get(i).equals(recipeOne.getHops().get(i)));
        }

        assertTrue(recipeOne.getMalts().size() == recipeTwo.getMalts().size());
        for (int i = 0; i < recipeOne.getMalts().size(); i++) {
            assertTrue(recipeOne.getMalts().get(i).equals(recipeTwo.getMalts().get(i)));
        }

        assertEquals(recipeOne.getYeast().size(), recipeTwo.getYeast().size());
        for (int i = 0; i < recipeOne.getYeast().size(); i++) {
            assertTrue(recipeOne.getYeast().get(i).equals(recipeTwo.getYeast().get(i)));
        }

    }

}

