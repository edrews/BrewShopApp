package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.IngredientListAdapter;
import com.brew.brewshop.IngredientTypeAdapter;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.style.StyleInfo;
import com.brew.brewshop.storage.style.StyleStorage;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.IngredientComparator;
import com.brew.brewshop.util.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = RecipeFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String UNIT_GALLON = " gal";
    private static final String UNIT_MINUTES = " min";
    private static final String UNIT_IBU = " IBU";
    private static final String UNIT_SRM = " SRM";
    private static final String UNIT_PERCENT = "%";

    private Recipe mRecipe;
    private BrewStorage mStorage;
    private FragmentHandler mFragmentHandler;
    private Dialog mSelectIngredient;
    private List<Object> mSortedIngredients;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        root.findViewById(R.id.recipe_stats_layout).setOnClickListener(this);
        root.findViewById(R.id.recipe_notes_layout).setOnClickListener(this);
        root.findViewById(R.id.new_ingredient_view).setOnClickListener(this);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
        }
        if (mRecipe == null) {
            Log.d(TAG, "Loading recipe from storage");
            mRecipe = mFragmentHandler.getCurrentRecipe();
            mStorage.retrieveRecipe(mRecipe);
        }

        StyleInfo styleInfo = new StyleStorage(getActivity()).getStyles().findById(mRecipe.getStyle().getId());

        TextView textView;
        textView = (TextView) root.findViewById(R.id.recipe_name);
        textView.setText(mRecipe.getName());

        ImageView iconView = (ImageView) root.findViewById(R.id.recipe_icon);
        iconView.setBackgroundColor(Util.getColor(mRecipe.getSrm()));

        textView = (TextView) root.findViewById(R.id.recipe_style);
        textView.setText(styleInfo.getName());

        textView = (TextView) root.findViewById(R.id.batch_volume);
        textView.setText(Util.fromDouble(mRecipe.getBatchVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_volume);
        textView.setText(Util.fromDouble(mRecipe.getBoilVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_time);
        textView.setText(Util.fromDouble(mRecipe.getBoilTime(), 0) + UNIT_MINUTES);

        textView = (TextView) root.findViewById(R.id.efficiency);
        textView.setText(Util.fromDouble(mRecipe.getEfficiency(), 1) + UNIT_PERCENT);

        textView = (TextView) root.findViewById(R.id.recipe_og);
        textView.setText(Util.fromDouble(mRecipe.getOg(), 3, false));

        textView = (TextView) root.findViewById(R.id.recipe_fg);
        textView.setText("~"+Util.fromDouble(mRecipe.getFg(), 3, false));

        textView = (TextView) root.findViewById(R.id.recipe_abv);
        textView.setText("~"+Util.fromDouble(mRecipe.getAbv(), 1));

        textView = (TextView) root.findViewById(R.id.recipe_srm);
        textView.setText(Util.fromDouble(mRecipe.getSrm(), 1));

        textView = (TextView) root.findViewById(R.id.recipe_ibu);
        textView.setText(Util.fromDouble(mRecipe.getIbu(), 1));

        textView = (TextView) root.findViewById(R.id.recipe_calories);
        textView.setText("~"+Util.fromDouble(mRecipe.getCalories(), 1));

        textView = (TextView) root.findViewById(R.id.style_og);
        textView.setText(Util.fromDouble(styleInfo.getOgMin(), 3, false) + "+");

        textView = (TextView) root.findViewById(R.id.style_ibu);
        String ibu = Util.fromDouble(styleInfo.getIbuMin(), 1);
        if (styleInfo.getIbuMax() - styleInfo.getIbuMin() >= 0.1) {
            ibu += " - " + Util.fromDouble(styleInfo.getIbuMax(), 1);
        }
        textView.setText(ibu + UNIT_IBU);

        textView = (TextView) root.findViewById(R.id.style_srm);
        String srm = String.valueOf(styleInfo.getSrmMin());
        if (styleInfo.getSrmMin() != styleInfo.getSrmMax()) {
            srm += " - " + styleInfo.getSrmMax();
        }
        textView.setText(srm + UNIT_SRM);

        textView = (TextView) root.findViewById(R.id.style_fg);
        String fg = Util.fromDouble(styleInfo.getFgMin(), 3, false);
        if (styleInfo.getFgMax() - styleInfo.getFgMin() >= 0.001) {
            fg += " - " + Util.fromDouble(styleInfo.getFgMax(), 3, false);
        }
        textView.setText(fg);

        textView = (TextView) root.findViewById(R.id.style_abv);
        String abv = Util.fromDouble(styleInfo.getAbvMin(), 1);
        if (styleInfo.getAbvMax() - styleInfo.getAbvMin() >= 0.1) {
            abv += " - " + Util.fromDouble(styleInfo.getAbvMax(), 1);
        }
        textView.setText(abv + UNIT_PERCENT);

        LinearLayout ingredientList = (LinearLayout) root.findViewById(R.id.ingredient_list);
        mSortedIngredients = mRecipe.getIngredients();
        Collections.sort(mSortedIngredients, new IngredientComparator());
        IngredientListAdapter adapter = new IngredientListAdapter(getActivity(), mSortedIngredients);
        if (adapter.getCount() > 0) {
            ingredientList.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, ingredientList);
                view.setTag(R.string.ingredients, mSortedIngredients.get(i));
                view.setOnClickListener(this);
                ingredientList.addView(view);
            }
        }

        textView = (TextView) root.findViewById(R.id.recipe_notes);
        String notes;
        if (mRecipe.getNotes().isEmpty()) {
            notes = getActivity().getResources().getString(R.string.add_recipe_notes);
            textView.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
        } else {
            notes = mRecipe.getNotes();
        }
        textView.setText(notes);

        getActivity().getActionBar().setTitle(findString(R.string.edit_recipe));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStorage.close();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (state == null) {
            state = new Bundle();
        }
        state.putParcelable(RECIPE, mRecipe);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe_menu, menu);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recipe_stats_layout:
                mFragmentHandler.showRecipeStatsEditor(mRecipe);
                break;
            case R.id.recipe_notes_layout:
                mFragmentHandler.showRecipeNotesEditor(mRecipe);
                break;
            case R.id.new_ingredient_view:
                addNewIngredient();
                break;
        }
        Object ingredient = view.getTag(R.string.ingredients);
        if (ingredient != null) {
            editIngredient(ingredient);
        }
    }

    private void editIngredient(Object ingredient) {
        if (ingredient instanceof MaltAddition) {
            mFragmentHandler.showMaltEditor(mRecipe, (MaltAddition) ingredient);
        } else if (ingredient instanceof HopAddition) {
            mFragmentHandler.showHopsEditor(mRecipe, (HopAddition) ingredient);
        } else if (ingredient instanceof Yeast) {
            mFragmentHandler.showYeastEditor(mRecipe, (Yeast) ingredient);
        }
    }

    private void addNewIngredient() {
        int maxIngredients = getActivity().getResources().getInteger(R.integer.max_ingredients);
        if (mSortedIngredients.size() >= maxIngredients) {
            String message = String.format(findString(R.string.max_ingredients_reached), maxIngredients);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        mSelectIngredient = new Dialog(getActivity());
        mSelectIngredient.setContentView(R.layout.select_ingredient);

        IngredientTypeAdapter adapter = new IngredientTypeAdapter(getActivity(), getIngredientTypes());
        ListView listView = (ListView) mSelectIngredient.findViewById(R.id.recipe_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        mSelectIngredient.setCancelable(true);
        mSelectIngredient.setTitle(findString(R.string.add_ingredient));
        mSelectIngredient.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentHandler = (FragmentHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentHandler.class.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSelectIngredient.dismiss();
        String ingredient = getIngredientTypes().get(position);
        if (findString(R.string.malt).equals(ingredient)) {
            MaltAddition malt = new MaltAddition();
            mRecipe.getMalts().add(malt);
            mFragmentHandler.showMaltEditor(mRecipe, malt);
        } else if (findString(R.string.hops).equals(ingredient)) {
            HopAddition hop = new HopAddition();
            mRecipe.getHops().add(hop);
            mFragmentHandler.showHopsEditor(mRecipe, hop);
        } else if (findString(R.string.yeast).equals(ingredient)) {
            Yeast yeast = new Yeast();
            mRecipe.getYeast().add(yeast);
            mFragmentHandler.showYeastEditor(mRecipe, yeast);
        }
    }

    private List<String> getIngredientTypes() {
        String[] ingredients = getActivity().getResources().getStringArray(R.array.ingredient_types);
        return Arrays.asList(ingredients);
    }

    private String findString(int id) {
        return getActivity().getResources().getString(id);
    }
}
