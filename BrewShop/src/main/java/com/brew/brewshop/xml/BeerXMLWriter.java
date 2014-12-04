package com.brew.brewshop.xml;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.brew.brewshop.R;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.storage.recipes.BeerStyle;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.HopUsage;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Quantity;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.storage.style.BjcpCategory;
import com.brew.brewshop.storage.style.BjcpCategoryList;
import com.brew.brewshop.storage.style.BjcpCategoryStorage;
import com.brew.brewshop.storage.style.BjcpSubcategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by doug on 30/11/14.
 */
public class BeerXMLWriter extends AsyncTask<OutputStream, Integer, Integer> {

    Recipe[] recipes;
    RecipeListFragment parentFragment = null;
    ProgressDialog progressDialog;
    BjcpCategoryList mBjcpCategoryList;

    public BeerXMLWriter(RecipeListFragment parentFragment, Recipe[] recipes) {
        this.parentFragment = parentFragment;
        progressDialog = new ProgressDialog(parentFragment.getActivity());
        mBjcpCategoryList = new BjcpCategoryStorage(parentFragment.getActivity()).getStyles();
        this.recipes = recipes;
    }

    @Override
    protected void onPreExecute() {
        //set message of the dialog
        progressDialog.setMessage(parentFragment.getActivity().getString(R.string.opening_file));
        //show dialog
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Integer success) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        parentFragment.savedRecipes(success);
    }

    public int writeRecipes(File outputFile) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        return writeRecipes(outputStream);
    }

    public int writeRecipes(OutputStream recipeOutputStream) throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e("BrewShop", "Couldn't create DocumentBuilderFactory", e1);
            return -1;
        }

        Document recipeDocument = null;
        XPath xp = null;
        // Create the Recipe Node
        recipeDocument = dBuilder.newDocument();
        Element recipesElement = recipeDocument.createElement("RECIPES");

        int success = 0;
        for (int i = 0; i < recipes.length; i++) {
            publishProgress(i, recipes.length);

            Recipe recipe = recipes[i];
            try {
                Element recipeElement = writeRecipe(recipe, recipeDocument);
                if (recipeElement != null) {
                    recipesElement.appendChild(recipeElement);
                }
                success++;
            } catch (IOException ioe) {
                Log.e("BeerXMLReader", "Couldn't add recipe", ioe);
            }
        }

        recipeDocument.appendChild(recipesElement);
        try {
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(recipeDocument);

            xp = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList) xp.evaluate(
                    "//text()[normalize-space(.)='']", recipeDocument,
                    XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                node.getParentNode().removeChild(node);
            }

            StreamResult configResult = new StreamResult(recipeOutputStream);
            transformer.transform(source, configResult);
        } catch (TransformerConfigurationException e) {
            Log.e("BeerXMLReader Writer", "Could not transform config file", e);
        } catch (TransformerException e) {
            Log.e("BeerXMLReader Writer", "Could not transformer file", e);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return success;
    }

    public Element writeRecipe(Recipe recipe, Document recipeDocument) throws IOException {
        Element recipeElement = recipeDocument.createElement("RECIPE");

        // Generic recipe stuff
        Element tElement = recipeDocument.createElement("NAME");
        tElement.setTextContent(recipe.getName());
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TYPE");
        //tElement.setTextContent(recipe.getType());
        tElement.setTextContent("Partial Mash");
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BREWER");
        tElement.setTextContent(recipe.getBrewerName());
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("EFFICIENCY");
        tElement.setTextContent("" + recipe.getEfficiency());
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BATCH_SIZE");
        tElement.setTextContent("" + Quantity.convertUnit("gallons US", "litres", recipe.getBatchVolume()));
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BOIL_SIZE");
        tElement.setTextContent("" + Quantity.convertUnit("gallons US", "litres", recipe.getBoilVolume()));
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BOIL_TIME");
        tElement.setTextContent("" + recipe.getBoilTime());
        recipeElement.appendChild(tElement);

        Element hopsElement = recipeDocument.createElement("HOPS");

        for (HopAddition hopAddition : recipe.getHops()) {
            Element hopElement = createHopElement(hopAddition, recipeDocument);
            hopsElement.appendChild(hopElement);
        }

        recipeElement.appendChild(hopsElement);

        Element fermentablesElement = recipeDocument.createElement("FERMENTABLES");

        for (MaltAddition maltAddition : recipe.getMalts()) {
            Element fermentableElement = createFermentableElement(maltAddition, recipeDocument);
            fermentablesElement.appendChild(fermentableElement);
        }

        recipeElement.appendChild(fermentablesElement);

        Element yeastsElement = recipeDocument.createElement("YEASTS");

        for (Yeast yeast : recipe.getYeast()) {
            Element yeastElement = createYeastElement(yeast, recipeDocument);
            yeastsElement.appendChild(yeastElement);
        }

        recipeElement.appendChild(createStyleElement(recipe.getStyle(), recipeDocument));

        return recipeElement;
    }

    private Element createHopElement(HopAddition hopAddition, Document recipeDocument) {
        Element hopElement = recipeDocument.createElement("HOP");

        Element tElement = recipeDocument.createElement("NAME");
        tElement.setTextContent(hopAddition.getHop().getName());
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("VERSION");
        tElement.setTextContent("1");
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("ALPHA");
        tElement.setTextContent("" + hopAddition.getHop().getPercentAlpha());
        hopElement.appendChild(tElement);

        // Amount is in KG
        tElement = recipeDocument.createElement("AMOUNT");
        tElement.setTextContent("" + Quantity.convertUnit("lbs", "kg", hopAddition.getWeight().getPounds()));
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("DISPLAY_AMOUNT");
        tElement.setTextContent(hopAddition.getWeight().getOunces() + " oz");
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("USE");
        tElement.setTextContent(hopAddition.getUsage().toString());
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TIME");

        if (hopAddition.getUsage() == HopUsage.DRY_HOP) {
            double days = hopAddition.getDryHopDays()*24*60;
            tElement.setTextContent("" + days);
        } else {
            tElement.setTextContent("" + hopAddition.getBoilTime());
        }

        hopElement.appendChild(tElement);

        /*
        For later

        tElement = recipeDocument.createElement("NOTES");
        tElement.setTextContent("" + hopAddition.getNotes());
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TYPE");
        tElement.setTextContent("" + hopAddition.getType());
        hopElement.appendChild(tElement);

        tElement = recipeDocument.createElement("FORM");
        tElement.setTextContent("" + hopAddition.getForm()());
        hopElement.appendChild(tElement);
        */
        return hopElement;
    }

    private Element createFermentableElement(MaltAddition maltAddition, Document recipeDocument) {
        Element fermentableElement = recipeDocument.createElement("FERMENTABLE");

        Element tElement = recipeDocument.createElement("VERSION");
        tElement.setTextContent("1");
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("NAME");
        tElement.setTextContent(maltAddition.getMalt().getName());
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TYPE");
        //tElement.setTextContent(maltAddition.getMalt().getType());
        tElement.setTextContent("Grain");
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("AMOUNT");
        tElement.setTextContent("" + Quantity.convertUnit("lbs", "kgs", maltAddition.getWeight().getPounds()));
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("DISPLAY_AMOUNT");
        tElement.setTextContent(maltAddition.getWeight().getPounds() + " lbs");
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("YIELD");
        tElement.setTextContent("" + maltAddition.getMalt().getGravity());
        fermentableElement.appendChild(tElement);

        tElement = recipeDocument.createElement("COLOR");
        tElement.setTextContent("" + maltAddition.getMalt().getColor());
        fermentableElement.appendChild(tElement);

        return fermentableElement;
    }

    private Element createYeastElement(Yeast yeast, Document recipeDocument) {
        Element yeastElement = recipeDocument.createElement("YEAST");

        Element tElement = recipeDocument.createElement("VERSION");
        tElement.setTextContent("1");
        yeastElement.appendChild(tElement);

        tElement = recipeDocument.createElement("NAME");
        tElement.setTextContent(yeast.getName());
        yeastElement.appendChild(tElement);

        tElement = recipeDocument.createElement("ATTENUATION");
        tElement.setTextContent("" + yeast.getAttenuation());
        yeastElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TYPE");
        tElement.setTextContent("ALE");
        yeastElement.appendChild(tElement);

        tElement = recipeDocument.createElement("FORM");
        tElement.setTextContent("DRY");
        yeastElement.appendChild(tElement);

        return yeastElement;
    }

    private Element createStyleElement(BeerStyle style, Document recipeDocument) {
        Element styleElement = recipeDocument.createElement("STYLE");

        BjcpCategory bjcpCategory = mBjcpCategoryList.findByName(style.getStyleName());
        if (bjcpCategory == null) {
            return styleElement;
        }

        BjcpSubcategory bjcpSubcategory = bjcpCategory.findSubcategoryByName(style.getSubstyleName());

        Element tElement = recipeDocument.createElement("VERSION");
        tElement.setTextContent("1");
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("NAME");
        tElement.setTextContent(style.getDisplayName());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("CATEGORY");
        tElement.setTextContent(style.getStyleName());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("CATEGORY_NUMBER");
        tElement.setTextContent("" + bjcpCategory.getId());
        styleElement.appendChild(tElement);

        if (bjcpSubcategory != null) {
            tElement = recipeDocument.createElement("STYLE_LETTER");
            tElement.setTextContent(bjcpSubcategory.getLetter());
            styleElement.appendChild(tElement);
        }

        tElement = recipeDocument.createElement("STYLE_GUIDE");
        tElement.setTextContent(style.getStyleGuide());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("TYPE");
        tElement.setTextContent(style.getType());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("OG_MIN");
        tElement.setTextContent("" + style.getOgMin());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("OG_MAX");
        tElement.setTextContent("" + style.getOgMax());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("FG_MIN");
        tElement.setTextContent("" + style.getFgMin());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("FG_MAX");
        tElement.setTextContent("" + style.getFgMax());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("IBU_MIN");
        tElement.setTextContent("" + style.getIbuMin());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("IBU_MAX");
        tElement.setTextContent("" + style.getIbuMax());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("COLOR_MIN");
        tElement.setTextContent("" + style.getSrmMin());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("COLOR_MAX");
        tElement.setTextContent("" + style.getSrmMax());
        styleElement.appendChild(tElement);

        return styleElement;
    }

    @Override
    protected Integer doInBackground(OutputStream... outputStreams) {
        try {
            return writeRecipes(outputStreams[0]);
        } catch (IOException ioe) {
            return -1;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage(String.format(
                parentFragment.getActivity().getString(R.string.save_progress), progress[0], progress[1]));
    }
}
