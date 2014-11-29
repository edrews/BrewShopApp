package com.brew.brewshop;

import android.widget.CheckedTextView;
import android.widget.ListView;

import java.io.IOException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class UiTest extends UiAutomatorTestCase {

    public void setUp() throws UiObjectNotFoundException, IOException {
        getUiDevice().pressHome();
        UiObject app = new UiObject(new UiSelector().text("Brew Shop"));
        app.clickAndWaitForNewWindow();
    }

    public void testCreateRecipe() throws UiObjectNotFoundException {
        findRes("action_new_recipe").clickAndWaitForNewWindow();
        setStats("My Favorite Recipe", "American Ale", "American Amber Ale");
        addMalt("Amber Malt", "10", null, null);
        addMalt("Crystal Malt - 20L", "1", "1.032", null);
        addHops("Centennial", "1", "Boil", "45");
        addHops("Cascade", "1", "Whirlpool", null);
        addYeast("American Ale Blend");
        addNotes("This is a good recipe :)");
        goBack();
    }

    private void setStats(String name, String style, String substyle) throws UiObjectNotFoundException {
        findRes("recipe_stats_layout").clickAndWaitForNewWindow();
        selectFromList("recipe_style", style);
        selectFromList("recipe_substyle", substyle);
        setText("recipe_name", name);
        goBack();
    }

    private void addNotes(String notes) throws UiObjectNotFoundException {
        findResScrollable("scroll_view").scrollToEnd(3);
        findText("Notes").clickAndWaitForNewWindow();
        setText("recipe_notes", notes);
        goBack();
    }

    private void addMalt(String malt, String lbs, String gravity, String color) throws UiObjectNotFoundException {
        findRes("new_ingredient_view").clickAndWaitForNewWindow();
        findText("Malt").clickAndWaitForNewWindow();
        selectFromList("malt_type", malt);
        setText("malt_weight_lb", lbs);
        setText("malt_gravity", gravity);
        setText("malt_color", color);
        goBack();
    }

    private void addHops(String hop, String oz, String usage, String minutes) throws UiObjectNotFoundException {
        findRes("new_ingredient_view").clickAndWaitForNewWindow();
        findText("Hops").clickAndWaitForNewWindow();
        selectFromList("hops_type", hop);
        setText("hops_weight", oz);
        selectFromList("hops_usage", usage);
        setText("boil_duration", minutes);
        goBack();
    }

    private void addYeast(String yeast) throws UiObjectNotFoundException {
        findRes("new_ingredient_view").clickAndWaitForNewWindow();
        findText("Yeast").clickAndWaitForNewWindow();
        selectFromList("yeast_type", yeast);
        goBack();
    }

    private void goBack() throws UiObjectNotFoundException{
        findDesc("Navigate up").clickAndWaitForNewWindow();
    }

    private UiObject findDesc(String desc) {
        return new UiObject(new UiSelector().description(desc));
    }

    private void setText(String resId, String text) throws UiObjectNotFoundException {
        if (text == null) {
            return;
        }
        findRes(resId).setText(text);
    }

    private void selectFromList(String listResId, String text) throws UiObjectNotFoundException {
        findRes(listResId).click();
        UiScrollable scrollable = resScrollable(ListView.class);
        scrollable.getChildByText(new UiSelector().className(CheckedTextView.class), text).click();
    }

    private UiScrollable resScrollable(Class clazz) {
        UiScrollable scrollable = new UiScrollable(new UiSelector().className(clazz));
        return scrollable.setAsVerticalList();
    }

    private UiObject findRes(String resource) {
        return new UiObject(new UiSelector().resourceId(resId(resource)));
    }

    private UiScrollable findResScrollable(String resource) {
        return new UiScrollable(new UiSelector().resourceId(resId(resource)));
    }

    private UiObject findText(String text) {
        return new UiObject(new UiSelector().text(text));
    }

    private String resId(String resource) {
        return "com.brew.brewshop:id/" + resource;
    }
}
