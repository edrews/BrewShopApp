package com.brew.brewshop.xml;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.brew.brewshop.R;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.storage.recipes.BeerStyle;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.HopUsage;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Quantity;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.storage.style.BjcpCategory;
import com.brew.brewshop.storage.style.BjcpCategoryList;
import com.brew.brewshop.storage.style.BjcpCategoryStorage;
import com.brew.brewshop.storage.style.BjcpSubcategory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class BeerXMLReader extends AsyncTask<InputStream, Integer, Recipe[]>  {
    private static final String TAG = BeerXMLReader.class.getName();
    ProgressDialog dialog;
    RecipeListFragment parentFragment = null;
    BjcpCategoryList mBjcpCategoryList;

    public BeerXMLReader(RecipeListFragment parentFragment) {
        this.parentFragment = parentFragment;
        dialog = new ProgressDialog(parentFragment.getActivity());
        mBjcpCategoryList = new BjcpCategoryStorage(parentFragment.getActivity()).getStyles();
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(parentFragment.getActivity().getString(R.string.open_recipe_progress));
        dialog.show();
    }

    @Override
    protected Recipe[] doInBackground(InputStream... inputStreams) {
        return readInputStream(inputStreams[0]);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        int current = progress[0] + 1;
        int total = progress[1];
        if (total > 1) {
            dialog.setMessage(String.format(
                    parentFragment.getActivity().getString(R.string.open_recipes_progress),
                    current, total));
        } else {
            dialog.setMessage(parentFragment.getActivity().getString(R.string.open_recipe_progress));
        }
    }

    @Override
    protected void onPostExecute(final Recipe[] recipes) {
        parentFragment.addRecipes(recipes);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private Recipe[] readInputStream(InputStream inputStream) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e(TAG, "Couldn't create DocumentBuilderFactory", e1);
            return null;

        }

        Document recipeDocument = null;
        XPath xp = null;
        try {
            recipeDocument = dBuilder.parse(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't read XML File", e);
            return null;
        }

        return readDocument(recipeDocument);
    }

    private Recipe[] readFile(File beerXMLFile) {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e(TAG, "Couldn't create DocumentBuilderFactory", e1);
            return null;
        }

        Document recipeDocument = null;
        try {
            recipeDocument = dBuilder.parse(beerXMLFile);
        } catch (Exception e) {
            Log.e(TAG, beerXMLFile + " isn't an XML File");
            return null;
        }

        return readDocument(recipeDocument);
    }

    private Recipe[] readDocument(Document recipeDocument) {
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

            return readRecipe(recipeDocument, null);
        } catch (XPathException xpe) {
            Log.e(TAG, "Couldn't run XPATH", xpe);
            return null;
        }
    }

    private Recipe[] readRecipe(Document beerDocument, String name) throws XPathException {
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
            publishProgress(i, recipeData.getLength());
            try {
                recipeList.add(readSingleRecipe(recipeData.item(i)));
            } catch (XPathException xpe) {
                Log.e(TAG, "Couldn't read the recipe at index " + i, xpe);
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Couldn't read the recipe at index " + i + " due to a bad number", nfe);
            }
        }

        return recipeList.toArray(new Recipe[recipeList.size()]);
    }

    private Recipe readSingleRecipe(Node recipeNode) throws XPathException, NumberFormatException {
        XPath xp = XPathFactory.newInstance().newXPath();
        Recipe recipe = new Recipe();

        // otherwise get the details from the recipe
        String recipeName = (String) xp.evaluate("NAME/text()", recipeNode, XPathConstants.STRING);
        String brewerName = (String) xp.evaluate("BREWER/text()", recipeNode, XPathConstants.STRING);

        double efficiency = getDouble(recipeNode, "EFFICIENCY", xp);
        double batchSize = getDouble(recipeNode, "BATCH_SIZE", xp);
        double boilSize = getDouble(recipeNode, "BOIL_SIZE", xp);
        double boilTime = getDouble(recipeNode, "BOIL_TIME", xp);

        recipe.setName(recipeName);
        recipe.setBrewerName(brewerName);
        recipe.setBatchVolume(Quantity.convertUnit("litres", "gallons", batchSize));
        recipe.setBoilVolume(Quantity.convertUnit("litres", "gallons", boilSize));
        recipe.setBoilTime(boilTime);
        recipe.setEfficiency(efficiency);

        NodeList hopsList = (NodeList) xp.evaluate("HOPS", recipeNode, XPathConstants.NODESET);
        parseHops(recipe, hopsList);
        NodeList maltList = (NodeList) xp.evaluate("FERMENTABLES", recipeNode, XPathConstants.NODESET);
        parseMalts(recipe, maltList);
        NodeList yeastList = (NodeList) xp.evaluate("YEASTS", recipeNode, XPathConstants.NODESET);
        parseYeasts(recipe, yeastList);
        Node styleList = (Node) xp.evaluate("STYLE", recipeNode, XPathConstants.NODE);
        parseStyle(recipe, styleList);

        return recipe;
    }

    private void parseHops(Recipe recipe, NodeList hops) throws XPathException, NumberFormatException {
        if (hops == null || hops.getLength() == 0) {
            return;
        }
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList hopList = (NodeList) xp.evaluate("HOP", hops.item(0), XPathConstants.NODESET);

        for (int i = 0; i < hopList.getLength(); i++) {
            Node hop = hopList.item(i);

            // Get the values
            String name = (String) xp.evaluate("NAME", hop, XPathConstants.STRING);
            String origin = (String) xp.evaluate("ORIGIN", hop, XPathConstants.STRING);
            String temp = (String) xp.evaluate("AMOUNT", hop, XPathConstants.STRING);
            double amount = Double.parseDouble(temp);
            temp = (String) xp.evaluate("ALPHA", hop, XPathConstants.STRING);
            double alpha = Double.parseDouble(temp);
            temp = (String) xp.evaluate("BETA", hop, XPathConstants.STRING);
            if (!temp.equals("")) {
                double beta = Double.parseDouble(temp);
            }

            temp = (String) xp.evaluate("TIME", hop, XPathConstants.STRING);
            int time = (int) Math.round(Double.parseDouble(temp));
            String use = (String) xp.evaluate("USE", hop, XPathConstants.STRING);
            String notes = (String) xp.evaluate("NOTES", hop, XPathConstants.STRING);
            String displayAmount = (String) xp.evaluate("DISPLAY_AMOUNT", hop, XPathConstants.STRING);

            Hop hopObject = new Hop();
            hopObject.setName(name);
            hopObject.setPercentAlpha(alpha);

            HopAddition hopAddition = new HopAddition();
            hopAddition.setHop(hopObject);

            Weight weight = null;

            try {
                if (displayAmount != null) {
                    weight = new Weight(displayAmount);
                } else if (amount >= 0.0) {
                    weight = new Weight(amount + " kg");
                }
            } catch (Exception e) {
                Log.e(TAG, "Couldn't parse hop weight", e);
                return;
            }

            hopAddition.setWeight(weight);

            // Not all of these are used by beerxml 1.0, but we can change as and when
            if (use.equalsIgnoreCase("boil")) {
                hopAddition.setUsage(HopUsage.BOIL);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("dry hop")) {
                hopAddition.setUsage(HopUsage.DRY_HOP);
                int days = (time/60)/24;
                hopAddition.setDryHopDays(days);
            } else if (use.equalsIgnoreCase("mash")) {
                hopAddition.setUsage(HopUsage.MASH);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("first wort")) {
                hopAddition.setUsage(HopUsage.FIRST_WORT);
                hopAddition.setBoilTime(time);
            } else if (use.equalsIgnoreCase("aroma") || use.equalsIgnoreCase("whirlpool")) {
                hopAddition.setUsage(HopUsage.WHIRLPOOL);
                hopAddition.setBoilTime(time);
            }

            // Everything is OK here, so add it in.
            recipe.addHop(hopAddition);
        }
    }

    private void parseMalts(Recipe recipe, NodeList malts) throws XPathException, NumberFormatException {
        if (malts == null || malts.getLength() == 0) {
            return;
        }

        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList fermentableList = (NodeList) xp.evaluate("FERMENTABLE", malts.item(0), XPathConstants.NODESET);

        for (int i = 0; i < fermentableList.getLength(); i++) {
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
                malt.setMashed(true);

                MaltAddition maltAddition = new MaltAddition();
                maltAddition.setWeight(new Weight(amount));
                maltAddition.setMalt(malt);

                recipe.addFermentable(maltAddition);
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Couldn't parse a number", nfe);
            } catch (Exception e) {
                if (e instanceof XPathException) {
                    throw (XPathException) e;
                } else {
                    Log.e(TAG, "Couldn't read the weight for a malt", e);
                }
            }
        }
    }

    private void parseYeasts(Recipe recipe, NodeList yeasts) throws XPathException, NumberFormatException {
        if (yeasts == null || yeasts.getLength() == 0) {
            return;
        }

        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList yeastList = (NodeList) xp.evaluate("YEAST", yeasts.item(0), XPathConstants.NODESET);

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
                recipe.addYeast(yeast);
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Couldn't parse a number", nfe);
            } catch (Exception e) {
                if (e instanceof XPathException) {
                    throw (XPathException) e;
                } else {
                    Log.e(TAG, "Couldn't read the weight for a malt", e);
                }
            }
        }
    }

    private void parseStyle(Recipe recipe, Node style) throws XPathExpressionException {
        if (style == null) {
            return;
        }

        XPath xp = XPathFactory.newInstance().newXPath();

        String name = (String) xp.evaluate("NAME", style, XPathConstants.STRING);
        String notes = (String) xp.evaluate("NOTES", style, XPathConstants.STRING);
        int categoryNumber = getInteger(style, "CATEGORY_NUMBER", xp);
        String styleLetter = (String) xp.evaluate("STYLE_LETTER", style, XPathConstants.STRING);
        String styleGuide = (String) xp.evaluate("STYLE_GUIDE", style, XPathConstants.STRING);
        String type = (String) xp.evaluate("TYPE", style, XPathConstants.STRING);

        double ogMin = getDouble(style, "OG_MIN", xp);
        double ogMax = getDouble(style, "OG_MAX", xp);
        double fgMin = getDouble(style, "FG_MIN", xp);
        double fgMax = getDouble(style, "FG_MAX", xp);
        double ibuMin = getDouble(style, "IBU_MIN", xp);
        double ibuMax = getDouble(style, "IBU_MAX", xp);
        double colorMin = getDouble(style, "COLOR_MIN", xp);
        double colorMax = getDouble(style, "COLOR_MAX", xp);
        double abvMin = getDouble(style, "ABV_MIN", xp);
        double abvMax = getDouble(style, "ABV_MAX", xp);

        BjcpCategory bjcpCategory = mBjcpCategoryList.findByName(name);

        if (bjcpCategory == null && name.contains("&amp")) {
            bjcpCategory = mBjcpCategoryList.findByName(name.replace("&amp", "and"));
        }

        if (bjcpCategory == null) {
            return;
        }

        BjcpSubcategory bjcpSubcategory = bjcpCategory.findSubcategoryByLetter(styleLetter);
        BeerStyle beerStyle = new BeerStyle();
        beerStyle.setStyleName(bjcpCategory.getName());
        beerStyle.setDescription(notes);
        beerStyle.setStyleGuide(styleGuide);
        beerStyle.setType(type);
        beerStyle.setAbvMax(abvMax);
        beerStyle.setAbvMin(abvMin);
        beerStyle.setFgMax(fgMax);
        beerStyle.setFgMin(fgMin);
        beerStyle.setOgMax(ogMax);
        beerStyle.setOgMin(ogMin);
        beerStyle.setSrmMax(colorMax);
        beerStyle.setSrmMin(colorMin);
        beerStyle.setIbuMax(ibuMax);
        beerStyle.setIbuMin(ibuMin);
        beerStyle.setSubstyleName(bjcpSubcategory.getName());
        recipe.setStyle(beerStyle);
    }

    private double getDouble(NodeList element, String name, XPath xp) {
        try {
            String temp = (String) xp.evaluate(name.toUpperCase(), element, XPathConstants.STRING);
            return Double.parseDouble(temp);
        } catch (XPathException xpe) {
            Log.e(TAG, "Failed to run XPATH", xpe);
            return 0.0;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "Failed to parse string", nfe);
            return 0.0;
        }
    }

    private double getDouble(Node element, String name, XPath xp) {
        try {
            String temp = (String) xp.evaluate(name.toUpperCase(), element, XPathConstants.STRING);
            return Double.parseDouble(temp);
        } catch (XPathException xpe) {
            Log.e(TAG, "Failed to run XPATH", xpe);
            return 0.0;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "Failed to parse string", nfe);
            return 0.0;
        }
    }

    private int getInteger(Node element, String name, XPath xp) {
        try {
            String temp = (String) xp.evaluate(name.toUpperCase(), element, XPathConstants.STRING);
            return Integer.parseInt(temp);
        } catch (XPathException xpe) {
            Log.e(TAG, "Failed to run XPATH", xpe);
            return 0;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "Failed to parse string", nfe);
            return 0;
        }
    }
}
