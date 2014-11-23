package com.brew.brewshop.xml;

import android.util.Log;

import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.HopUsage;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by doug on 21/11/14.
 */
public class BeerXML {

    public static Recipe[] readInputStream(InputStream inputStream) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e("BrewShop", "Couldn't create DocumentBuilderFactory", e1);
            return null;

        }

        Document recipeDocument = null;
        XPath xp = null;
        try {
            recipeDocument = dBuilder.parse(inputStream);
        } catch (Exception e) {
            Log.e("BrewShop", "Couldn't read XML File", e);
            return null;
        }

        return readDocument(recipeDocument);
    }

    public static Recipe[] readFile(File beerXMLFile) {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e("BrewShop", "Couldn't create DocumentBuilderFactory", e1);
            return null;

        }

        Document recipeDocument = null;
        try {
            recipeDocument = dBuilder.parse(beerXMLFile);

        } catch (Exception e) {
            Log.e("BrewShop", beerXMLFile
                    + " isn't an XML File");
            return null;
        }

        return readDocument(recipeDocument);
    }

    private static Recipe[] readDocument(Document recipeDocument) {
        XPath xp = null;
        try {
            xp = XPathFactory.newInstance().newXPath();
            NodeList recipeList =
                    (NodeList) xp.evaluate(
                            "/RECIPES/RECIPE", recipeDocument, XPathConstants.NODESET);
            if (recipeList.getLength() == 0) {
                Log.i("BrewShop", "No Recipes found in file");
                return null;
            }
            if (recipeList.getLength() > 1) {
                // TODO: Handle multiple recipes
            }

            return readRecipe(recipeDocument, null);
        } catch (XPathException xpe) {
            Log.e("BrewShop", "Couldn't run XPATH", xpe);
            return null;
        }
    }

    public static Recipe[] readRecipe(Document beerDocument, String name) throws XPathException {
        Recipe recipe = new Recipe();
        String recipeSelector = "";

        if (name != null) {
            recipeSelector = "[NAME[text()=\"" + name + "\"]]";
        }

        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList recipeData =
                (NodeList) xp.evaluate(
                        "/RECIPES/RECIPE" + recipeSelector, beerDocument, XPathConstants.NODESET);

        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

        for (int i = 0; i < recipeData.getLength(); i++) {
            try {
                recipeList.add(readSingleRecipe(recipeData.item(i)));
            } catch (XPathException xpe) {
                Log.e("BeerXML", "Couldn't read the recipe at index " + i, xpe);
            }
        }

        return recipeList.toArray(new Recipe[recipeList.size()]);
    }

    public static Recipe readSingleRecipe(Node recipeNode) throws XPathException {
        XPath xp = XPathFactory.newInstance().newXPath();
        Recipe recipe = new Recipe();

        // otherwise get the details from the recipe
        String recipeName = (String) xp.evaluate("NAME/text()", recipeNode, XPathConstants.STRING);
        String brewerName = (String) xp.evaluate("BREWER/text()", recipeNode, XPathConstants.STRING);
        String temp = (String) xp.evaluate("BATCH_SIZE/text()", recipeNode, XPathConstants.STRING);
        double batchSize = Double.parseDouble(temp);
        temp = (String) xp.evaluate("BOIL_SIZE/text()", recipeNode, XPathConstants.STRING);
        double boilSize = Double.parseDouble(temp);
        temp = (String) xp.evaluate("BOIL_TIME/text()", recipeNode, XPathConstants.STRING);
        double boilTime = Double.parseDouble(temp);

        recipe.setName(recipeName);
        recipe.setBatchVolume(batchSize);
        recipe.setBoilVolume(boilSize);
        recipe.setBoilTime(boilTime);
        //recipe.setBrewerName();

        NodeList hopsList = (NodeList) xp.evaluate("HOPS", recipeNode, XPathConstants.NODESET);
        parseHops(recipe, hopsList);
        NodeList maltList = (NodeList) xp.evaluate("FERMENTABLES", recipeNode, XPathConstants.NODESET);
        parseMalts(recipe, maltList);
        NodeList yeastList = (NodeList) xp.evaluate("YEASTS", recipeNode, XPathConstants.NODESET);
        parseMalts(recipe, yeastList);

        return recipe;
    }

    private static void parseHops(Recipe recipe, NodeList hops) throws XPathException {
        if (hops == null || hops.getLength() == 0) {
            return;
        }
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList hopList = (NodeList) xp.evaluate("HOP", hops.item(0), XPathConstants.NODESET);

        for (int  i = 0; i < hopList.getLength(); i++) {
            Node hop = hopList.item(i);

            // Get the values
            String name = (String) xp.evaluate("NAME", hop, XPathConstants.STRING);
            String origin = (String) xp.evaluate("ORIGIN", hop, XPathConstants.STRING);
            String temp = (String) xp.evaluate("AMOUNT", hop, XPathConstants.STRING);
            double amount = Double.parseDouble(temp);
            temp = (String) xp.evaluate("ALPHA", hop, XPathConstants.STRING);
            double alpha = Double.parseDouble(temp);
            temp = (String) xp.evaluate("BETA", hop, XPathConstants.STRING);
            double beta = Double.parseDouble(temp);
            temp = (String) xp.evaluate("TIME", hop, XPathConstants.STRING);
            int time = (int)Math.round(Double.parseDouble(temp));
            String use = (String) xp.evaluate("USE", hop, XPathConstants.STRING);
            String notes = (String) xp.evaluate("NOTES", hop, XPathConstants.STRING);
            String displayAmount = (String) xp.evaluate("DISPLAY_AMOUNT", hop, XPathConstants.STRING);

            Hop hopObject = new Hop();
            hopObject.setName(name);
            hopObject.setPercentAlpha(alpha);

            HopAddition hopAddition = new HopAddition();
            hopAddition.setHop(hopObject);
            try {
                Weight weight = new Weight(displayAmount);
                hopAddition.setWeight(weight);
            } catch (Exception e) {
                Log.e("BrewShop", "Couldn't parse " + displayAmount + " as an amount", e);
                return;
            }


            // Not all of these are used by beerxml 1.0, but we can change as and when
            if (use.equalsIgnoreCase("boil")) {
                hopAddition.setUsage(HopUsage.BOIL);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("dry hop")) {
                hopAddition.setUsage(HopUsage.DRY_HOP);
                hopAddition.setDryHopDays(time);
            } else if (use.equalsIgnoreCase("mash")) {
                hopAddition.setUsage(HopUsage.MASH);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("first wort")) {
                hopAddition.setUsage(HopUsage.FIRST_WORT);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("aroma")) {
                hopAddition.setUsage(HopUsage.WHIRLPOOL);
                hopAddition.setBoilTime(time);
            }

            // Everything is OK here, so add it in.
            recipe.addHop(hopAddition);
        }
    }

    private static void parseMalts(Recipe recipe, NodeList malts) throws XPathException {
        if (malts == null || malts.getLength() == 0) {
            return;
        }
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList fermentableList = (NodeList) xp.evaluate("FERMENTABLE", malts.item(0), XPathConstants.NODESET);

        for (int  i = 0; i < fermentableList.getLength(); i++) {
            try {
                Node fermentable = fermentableList.item(i);

                // Get the values
                String name = (String) xp.evaluate("NAME", fermentable, XPathConstants.STRING);
                String type = (String) xp.evaluate("TYPE", fermentable, XPathConstants.STRING);
                String amount = (String) xp.evaluate("DISPLAY_AMOUNT", fermentable, XPathConstants.STRING);
                String color = (String) xp.evaluate("COLOR", fermentable, XPathConstants.STRING);
                String yield = (String) xp.evaluate("YIELD", fermentable, XPathConstants.STRING);
                String potential = (String) xp.evaluate("POTENTIAL", fermentable, XPathConstants.STRING);
                String notes = (String) xp.evaluate("NOTES", fermentable, XPathConstants.STRING);
                String supplier = (String) xp.evaluate("SUPPLIER", fermentable, XPathConstants.STRING);

                Malt malt = new Malt();
                malt.setName(name);
                malt.setGravity(Double.parseDouble(potential));
                malt.setColor(Double.parseDouble(color));

                MaltAddition maltAddition = new MaltAddition();
                maltAddition.setWeight(new Weight(amount));
                maltAddition.setMalt(malt);

                recipe.addFermentable(maltAddition);
            } catch (NumberFormatException nfe) {
                Log.e("BrewShop", "Couldn't parse a number", nfe);
            } catch (Exception e) {
                if (e instanceof XPathException) {
                    throw (XPathException) e;
                } else {
                    Log.e("BrewShop", "Couldn't read the weight for a malt", e);
                }
            }
        }
    }

    private static void parseYeasts(Recipe recipe, NodeList yeasts) throws XPathException {
        if (yeasts == null || yeasts.getLength() == 0) {
            return;
        }

        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList yeastList = (NodeList) xp.evaluate("YEASTS", yeasts.item(0), XPathConstants.NODESET);

        for (int i = 0; i < yeastList.getLength(); i++) {
            try {
                Node yeastItem = yeastList.item(i);

                String name = (String) xp.evaluate("NAME", yeastItem, XPathConstants.STRING);
                String type = (String) xp.evaluate("TYPE", yeastItem, XPathConstants.STRING);
                String form = (String) xp.evaluate("FORM", yeastItem, XPathConstants.STRING);
                String amount = (String) xp.evaluate("DISPLAY_AMOUNT", yeastItem, XPathConstants.STRING);
                String attenuation = (String) xp.evaluate("ATTENUATION", yeastItem, XPathConstants.STRING);

                Yeast yeast = new Yeast();
                yeast.setName(name);
                yeast.setAttenuation(Double.parseDouble(attenuation));
            } catch (NumberFormatException nfe) {
                Log.e("BrewShop", "Couldn't parse a number", nfe);
            } catch (Exception e) {
                if (e instanceof XPathException) {
                    throw (XPathException) e;
                } else {
                    Log.e("BrewShop", "Couldn't read the weight for a malt", e);
                }
            }
        }
    }
}
