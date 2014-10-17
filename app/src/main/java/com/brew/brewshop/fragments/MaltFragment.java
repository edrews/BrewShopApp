package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.NameableAdapter;
import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.NameableList;
import com.brew.brewshop.storage.malt.MaltInfoList;
import com.brew.brewshop.storage.malt.MaltStorage;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;

public class MaltFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MaltFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String MALT_INDEX = "MaltIndex";

    private Recipe mRecipe;
    private MaltInfoList mMaltInfo;
    private BrewStorage mStorage;
    private int mMaltIndex;

    private Spinner mMaltSpinner;
    private TextView mDescription;
    private EditText mWeightEdit;
    private EditText mGravityEdit;
    private EditText mColorEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_malt, container, false);
        mMaltSpinner = (Spinner) root.findViewById(R.id.malt_type);
        mWeightEdit = (EditText) root.findViewById(R.id.malt_weight);
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

        if (mRecipe != null && mMaltIndex >= 0) {
            MaltAddition addition = mRecipe.getMalts().get(mMaltIndex);
            setMalt(addition.getMalt());
            mWeightEdit.setText(String.valueOf(addition.getWeight().getPounds()));
            mColorEdit.setText(String.valueOf(addition.getMalt().getColor()));
            mGravityEdit.setText(String.valueOf(addition.getMalt().getGravity()));
        }

        mMaltSpinner.setOnItemSelectedListener(this);

        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.edit_malt_addition));

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        MaltAddition addition = mRecipe.getMalts().get(mMaltIndex);

        MaltInfo maltInfo = (MaltInfo) mMaltSpinner.getSelectedItem();
        Malt malt = new Malt();
        malt.setColor(Util.toDouble(mColorEdit.getText()));
        malt.setGravity(Util.toDouble(mGravityEdit.getText()));
        malt.setId(maltInfo.getId());
        malt.setName(maltInfo.getName());
        addition.setMalt(malt);

        Weight weight = new Weight(Util.toDouble(mWeightEdit.getText()), 0);
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

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setMaltIndex(int index) {
        mMaltIndex = index;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        MaltInfo info = (MaltInfo) mMaltSpinner.getSelectedItem();
        mColorEdit.setText(String.valueOf(info.getSrm()));
        mGravityEdit.setText(String.valueOf(info.getGravity()));
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
