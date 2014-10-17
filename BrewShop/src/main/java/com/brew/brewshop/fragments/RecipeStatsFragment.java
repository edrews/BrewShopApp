package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.NameableAdapter;
import com.brew.brewshop.storage.style.StyleInfo;
import com.brew.brewshop.storage.style.StyleInfoList;
import com.brew.brewshop.storage.style.StyleStorage;
import com.brew.brewshop.storage.recipes.BeerStyle;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.util.Util;

public class RecipeStatsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = RecipeStatsFragment.class.getName();
    private static final String RECIPE = "Recipe";

    private Recipe mRecipe;
    private BrewStorage mStorage;

    private EditText mRecipeName;
    private Spinner mStyle;
    private EditText mBatchVolume;
    private EditText mBoilVolume;
    private EditText mBoilTime;
    private EditText mEfficiency;
    private TextView mDescription;
    private StyleInfoList mStyleInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe_stats, container, false);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
        }

        if (mRecipe != null) {
            mRecipeName = (EditText) root.findViewById(R.id.recipe_name);
            mRecipeName.setText(mRecipe.getName());

            mDescription = (TextView) root.findViewById(R.id.description);
            mStyleInfoList = new StyleStorage(getActivity()).getStyles();
            mStyle = (Spinner) root.findViewById(R.id.recipe_style);
            NameableAdapter adapter = new NameableAdapter(getActivity(), mStyleInfoList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mStyle.setAdapter(adapter);
            mStyle.setOnItemSelectedListener(this);
            setStyle(mRecipe.getStyle());

            mBatchVolume = (EditText) root.findViewById(R.id.batch_volume);
            mBatchVolume.setText(Util.fromDouble(mRecipe.getBatchVolume(), 5));

            mBoilVolume = (EditText) root.findViewById(R.id.boil_volume);
            mBoilVolume.setText(Util.fromDouble(mRecipe.getBoilVolume(), 5));

            mBoilTime = (EditText) root.findViewById(R.id.boil_time);
            mBoilTime.setText(Util.fromDouble(mRecipe.getBoilTime(), 0));

            mEfficiency = (EditText) root.findViewById(R.id.efficiency);
            mEfficiency.setText(Util.fromDouble(mRecipe.getEfficiency(), 5));
        }
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.edit_recipe_stats));

        return root;
    }

    private void setStyle(BeerStyle style) {
        StyleInfo info = mStyleInfoList.findById(style.getId());
        int index = mStyleInfoList.indexOf(info);
        if (index < 0 ) {
            mStyle.setSelection(0);
        } else {
            mStyle.setSelection(index);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        String name = mRecipeName.getText().toString();
        if (name.isEmpty()) {
            name = getActivity().getResources().getString(R.string.unnamed_recipe);
        }
        mRecipe.setName(name);

        mRecipe.setBatchVolume(Util.toDouble(mBatchVolume.getText()));
        mRecipe.setBoilVolume(Util.toDouble(mBoilVolume.getText()));
        mRecipe.setBoilTime(Util.toDouble(mBoilTime.getText()));

        double efficiency = Util.toDouble(mEfficiency.getText());
        if (efficiency > 100) {
            efficiency = 100;
        }
        mRecipe.setEfficiency(efficiency);

        StyleInfo styleInfo = (StyleInfo) mStyle.getSelectedItem();
        mRecipe.setStyle(new BeerStyle(styleInfo.getId()));
        mStorage.updateRecipe(mRecipe);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        setDescription(((StyleInfo) mStyle.getSelectedItem()));
    }

    private void setDescription(StyleInfo info) {
        if (info.getDescription().isEmpty()) {
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setText(Util.separateSentences(info.getDescription()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
