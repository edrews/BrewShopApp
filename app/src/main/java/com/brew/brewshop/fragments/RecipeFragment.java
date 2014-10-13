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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.IngredientListAdapter;
import com.brew.brewshop.IngredientTypeAdapter;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.Util;

import java.util.Arrays;
import java.util.List;

public class RecipeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = RecipeFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String UNIT_GALLON = " gal";
    private static final String UNIT_MINUTES = " min";
    private static final String UNIT_PERCENT = "%";

    private Recipe mRecipe;
    private BrewStorage mStorage;
    private FragmentHandler mFragmentHandler;
    private Dialog mSelectIngredient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        root.findViewById(R.id.recipe_stats_layout).setOnClickListener(this);
        root.findViewById(R.id.recipe_notes).setOnClickListener(this);
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

        TextView textView;
        textView = (TextView) root.findViewById(R.id.recipe_name);
        textView.setText(mRecipe.getName());

        textView = (TextView) root.findViewById(R.id.recipe_style);
        textView.setText(mRecipe.getStyle().getName());

        textView = (TextView) root.findViewById(R.id.batch_volume);
        textView.setText(Util.fromDouble(mRecipe.getBatchVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_volume);
        textView.setText(Util.fromDouble(mRecipe.getBoilVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_time);
        textView.setText(Util.fromDouble(mRecipe.getBoilTime(), 0) + UNIT_MINUTES);

        textView = (TextView) root.findViewById(R.id.efficiency);
        textView.setText(Util.fromDouble(mRecipe.getEfficiency(), 1) + UNIT_PERCENT);

        LinearLayout ingredientList = (LinearLayout) root.findViewById(R.id.ingredient_list);
        IngredientListAdapter adapter = new IngredientListAdapter(getActivity(), mRecipe.getIngredients());
        if (adapter.getCount() > 0) {
            ingredientList.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, ingredientList);
                view.setTag(R.string.ingredients, mRecipe.getIngredients().get(i));
                view.setOnClickListener(this);
                ingredientList.addView(view);
            }
        }

        textView = (TextView) root.findViewById(R.id.recipe_notes);
        textView.setText(mRecipe.getNotes());

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
            case R.id.recipe_notes:
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
        if (mRecipe.getIngredients().size() >= maxIngredients) {
            String message = String.format(findString(R.string.max_ingredients_reached), maxIngredients);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        mSelectIngredient = new Dialog(getActivity());
        mSelectIngredient.setContentView(R.layout.select_ingredient);

        IngredientTypeAdapter adapter = new IngredientTypeAdapter(getActivity(), getIngredients());

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
        String ingredient = getIngredients().get(position);
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

    private List<String> getIngredients() {
        String[] ingredients = getActivity().getResources().getStringArray(R.array.ingredient_types);
        return Arrays.asList(ingredients);
    }

    private String findString(int id) {
        return getActivity().getResources().getString(id);
    }
}
