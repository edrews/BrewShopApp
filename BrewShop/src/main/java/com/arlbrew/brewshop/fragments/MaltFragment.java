package com.arlbrew.brewshop.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.storage.BrewStorage;
import com.arlbrew.brewshop.storage.NameableAdapter;
import com.arlbrew.brewshop.storage.malt.MaltInfo;
import com.arlbrew.brewshop.storage.malt.MaltInfoList;
import com.arlbrew.brewshop.storage.malt.MaltStorage;
import com.arlbrew.brewshop.storage.recipes.Malt;
import com.arlbrew.brewshop.storage.recipes.MaltAddition;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.Weight;
import com.arlbrew.brewshop.util.Util;

public class MaltFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @SuppressWarnings("unused")
    private static final String TAG = MaltFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String MALT_INDEX = "MaltIndex";

    private Recipe mRecipe;
    private MaltInfoList mMaltInfo;
    private BrewStorage mStorage;
    private int mMaltIndex;

    private Spinner mMaltSpinner;
    private TextView mDescription;
    private EditText mWeightLbEdit;
    private EditText mWeightOzEdit;
    private EditText mGravityEdit;
    private EditText mColorEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_malt, container, false);
        mMaltSpinner = (Spinner) root.findViewById(R.id.malt_type);
        mWeightLbEdit = (EditText) root.findViewById(R.id.malt_weight_lb);
        mWeightOzEdit = (EditText) root.findViewById(R.id.malt_weight_oz);
        mColorEdit = (EditText) root.findViewById(R.id.malt_color);
        mGravityEdit = (EditText) root.findViewById(R.id.malt_gravity);
        mDescription = (TextView) root.findViewById(R.id.description);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());
        mMaltInfo = new MaltStorage(getActivity()).getMalts();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mMaltIndex = state.getInt(MALT_INDEX, -1);
        }

        NameableAdapter<MaltInfo> adapter = new NameableAdapter<MaltInfo>(getActivity(), mMaltInfo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaltSpinner.setAdapter(adapter);
        mMaltSpinner.setOnItemSelectedListener(this);

        if (mRecipe != null && mMaltIndex >= 0) {
            MaltAddition addition = getMaltAddition();
            setMalt(addition.getMalt());
            setWeight(addition.getWeight());
            mColorEdit.setText(Util.fromDouble(addition.getMalt().getColor(), 1));
            mGravityEdit.setText(Util.fromDouble(addition.getMalt().getGravity(), 3, false));
        }

        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_malt_addition));
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        MaltAddition addition = mRecipe.getMalts().get(mMaltIndex);

        MaltInfo maltInfo = (MaltInfo) mMaltSpinner.getSelectedItem();
        Malt malt = new Malt();
        malt.setColor(Util.toDouble(mColorEdit.getText()));

        double gravity = Util.toDouble(mGravityEdit.getText());
        if (gravity < 1) {
            gravity = 1;
        }
        malt.setGravity(gravity);
        malt.setId(maltInfo.getId());
        malt.setName(maltInfo.getName());
        addition.setMalt(malt);

        Weight weight = new Weight(Util.toDouble(mWeightLbEdit.getText()), Util.toDouble(mWeightOzEdit.getText()));
        addition.setWeight(weight);

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
        state.putInt(MALT_INDEX, mMaltIndex);
    }

    private void setMalt(Malt malt) {
        MaltInfo info = mMaltInfo.findById(malt.getId());
        int index = mMaltInfo.indexOf(info);
        if (index < 0 ) {
            mMaltSpinner.setSelection(0);
        } else {
            mMaltSpinner.setSelection(index);
        }
    }

    private void setWeight(Weight weight) {
        mWeightLbEdit.setText(String.valueOf(weight.getPoundsPortion()));
        mWeightOzEdit.setText(Util.fromDouble(weight.getOuncesPortion(), 1));
    }

    private MaltAddition getMaltAddition() {
        return mRecipe.getMalts().get(mMaltIndex);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setMaltIndex(int index) {
        mMaltIndex = index;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        MaltInfo info = (MaltInfo) mMaltSpinner.getSelectedItem();
        if (info.getId() != getMaltAddition().getMalt().getId()) {
            mColorEdit.setText(Util.fromDouble(info.getSrm(), 1));
            mGravityEdit.setText(Util.fromDouble(info.getGravity(), 3, false));
        }
        if (info.getDescription().isEmpty()) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(info.getDescription()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
