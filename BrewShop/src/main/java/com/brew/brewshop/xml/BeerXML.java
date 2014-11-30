package com.brew.brewshop.xml;

import android.util.Log;

import com.brew.brewshop.storage.malt.MaltInfo;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
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
            } catch (NumberFormatException nfe) {
                Log.e("BeerXML", "Couldn't read the recipe at index " + i + " due to a bad number", nfe);
            }
        }

        return recipeList.toArray(new Recipe[recipeList.size()]);
    }

    public static Recipe readSingleRecipe(Node recipeNode) throws XPathException, NumberFormatException {
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
        recipe.setBrewerName(brewerName);
        recipe.setBatchVolume(batchSize);
        recipe.setBoilVolume(boilSize);
        recipe.setBoilTime(boilTime);
        //recipe.setBrewerName();

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

    private static void parseHops(Recipe recipe, NodeList hops) throws XPathException, NumberFormatException {
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
                Log.e("BrewShop", "Couldn't parse hop weight", e);
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
            } else if (use.equalsIgnoreCase("aroma")) {
                hopAddition.setUsage(HopUsage.WHIRLPOOL);
                hopAddition.setBoilTime(time);
            }

            // Everything is OK here, so add it in.
            recipe.addHop(hopAddition);
        }
    }

    private static void parseMalts(Recipe recipe, NodeList malts) throws XPathException, NumberFormatException {
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

    private static void parseYeasts(Recipe recipe, NodeList yeasts) throws XPathException, NumberFormatException {
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

    private static void parseStyle(Recipe recipe, Node style) throws XPathExpressionException {
        if (style == null) {
            return;
        }

        XPath xp = XPathFactory.newInstance().newXPath();

        String name = (String) xp.evaluate("NAME", style, XPathConstants.STRING);
        String category = (String) xp.evaluate("CATEGORY", style, XPathConstants.STRING);
        String notes = (String) xp.evaluate("NOTES", style, XPathConstants.STRING);
        String categoryNumber = (String) xp.evaluate("CATEGORY_NUMBER", style, XPathConstants.STRING);
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

        BeerStyle beerStyle = new BeerStyle();
        beerStyle.setStyleName(name);
        beerStyle.setSubstyleName(category);
        beerStyle.setDescription(notes);
        beerStyle.setCategoryNumber(categoryNumber);
        beerStyle.setStyleLetter(styleLetter);
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

        recipe.setStyle(beerStyle);
    }

    private static double getDouble(NodeList element, String name, XPath xp) {
        try {
            String temp = (String) xp.evaluate(name.toUpperCase(),
                    element, XPathConstants.STRING);
            return Double.parseDouble(temp);
        } catch (XPathException xpe) {
            Log.e("BeerXML", "Failed to run XPATH", xpe);
            return 0.0;
        } catch (NumberFormatException nfe) {
            Log.e("BeerXML", "Failed to parse string", nfe);
            return 0.0;
        }
    }

    private static double getDouble(Node element, String name, XPath xp) {
        try {
            String temp = (String) xp.evaluate(name.toUpperCase(),
                    element, XPathConstants.STRING);
            return Double.parseDouble(temp);
        } catch (XPathException xpe) {
            Log.e("BeerXML", "Failed to run XPATH", xpe);
            return 0.0;
        } catch (NumberFormatException nfe) {
            Log.e("BeerXML", "Failed to parse string", nfe);
            return 0.0;
        }
    }


    public static boolean writeRecipes(Recipe[] recipes, String path) throws IOException {
        if (path == null || path.equals("")) {
            throw new IOException("No output path provided");
        }

        File recipeFile = new File(path);
        return writeRecipes(recipes, recipeFile);
    }

    public static boolean writeRecipes(Recipe[] recipes, File recipeFile) throws IOException {
        OutputStream recipeOutputStream = new FileOutputStream(recipeFile);

        return writeRecipes(recipes, recipeOutputStream);
    }

    public static boolean writeRecipes(Recipe[] recipes, OutputStream recipeOutputStream) throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            Log.e("BrewShop", "Couldn't create DocumentBuilderFactory", e1);
            return false;
        }

        Document recipeDocument = null;
        XPath xp = null;
        // Create the Recipe Node
        recipeDocument = dBuilder.newDocument();
        Element recipesElement = recipeDocument.createElement("RECIPES");

        for (Recipe recipe : recipes) {
            try {
                Element recipeElement = BeerXML.writeRecipe(recipe, recipeDocument);
                if (recipeElement != null) {
                    recipesElement.appendChild(recipeElement);
                }
            } catch (IOException ioe) {
                Log.e("BeerXML", "Couldn't add recipe", ioe);
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
            Log.e("BeerXML Writer", "Could not transform config file", e);
        } catch (TransformerException e) {
            Log.e("BeerXML Writer", "Could not transformer file", e);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Element writeRecipe(Recipe recipe, Document recipeDocument) throws IOException {
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

        tElement = recipeDocument.createElement("BATCH_SIZE");
        tElement.setTextContent("" + Quantity.convertUnit("gallons", "litres", recipe.getBatchVolume()));
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BOIL_SIZE");
        tElement.setTextContent("" + Quantity.convertUnit("gallons", "litres", recipe.getBoilVolume()));
        recipeElement.appendChild(tElement);

        tElement = recipeDocument.createElement("BOIL_TIME");
        tElement.setTextContent("" + recipe.getBoilTime());
        recipeElement.appendChild(tElement);

        Element hopsElement = recipeDocument.createElement("HOPS");

        for (HopAddition hopAddition : recipe.getHops()) {
            Element hopElement = BeerXML.createHopElement(hopAddition, recipeDocument);
            hopsElement.appendChild(hopElement);
        }

        recipeElement.appendChild(hopsElement);

        Element fermentablesElement = recipeDocument.createElement("FERMENTABLES");

        for (MaltAddition maltAddition : recipe.getMalts()) {
            Element fermentableElement = BeerXML.createFermentableElement(maltAddition, recipeDocument);
            fermentablesElement.appendChild(fermentableElement);
        }

        recipeElement.appendChild(fermentablesElement);

        Element yeastsElement = recipeDocument.createElement("YEASTS");

        for (Yeast yeast : recipe.getYeast()) {
            Element yeastElement = BeerXML.createYeastElement(yeast, recipeDocument);
            yeastsElement.appendChild(yeastElement);
        }

        recipeElement.appendChild(BeerXML.createStyleElement(recipe.getStyle(), recipeDocument));

        return recipeElement;
    }

    private static Element createHopElement(HopAddition hopAddition, Document recipeDocument) {
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

    private static Element createFermentableElement(MaltAddition maltAddition, Document recipeDocument) {
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

    private static Element createYeastElement(Yeast yeast, Document recipeDocument) {
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

    private static Element createStyleElement(BeerStyle style, Document recipeDocument) {
        Element styleElement = recipeDocument.createElement("STYLE");

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
        tElement.setTextContent(style.getCategoryNumber());
        styleElement.appendChild(tElement);

        tElement = recipeDocument.createElement("STYLE_LETTER");
        tElement.setTextContent(style.getStyleLetter());
        styleElement.appendChild(tElement);

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
}
