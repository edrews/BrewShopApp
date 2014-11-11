package com.brew.brewshop.fragments;

import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.NameableAdapter;
import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.malt.MaltInfoList;
import com.brew.brewshop.storage.malt.MaltStorage;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;

public class MaltFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @SuppressWarnings("unused")
    private static final String TAG = MaltFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String MALT_INDEX = "MaltIndex";

    private Recipe mRecipe;
    private MaltInfoList mMaltInfoList;
    private BrewStorage mStorage;
    private int mMaltIndex;
    NameableAdapter<MaltInfo> mAdapter;

    private Spinner mMaltSpinner;
    private TextView mDescription;
    private EditText mWeightLbEdit;
    private EditText mWeightOzEdit;
    private EditText mGravityEdit;
    private EditText mColorEdit;
    private EditText mCustomName;
    private View mCustomNameView;
    private View mDescriptionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_malt, container, false);
        mMaltSpinner = (Spinner) root.findViewById(R.id.malt_type);
        mWeightLbEdit = (EditText) root.findViewById(R.id.malt_weight_lb);
        mWeightOzEdit = (EditText) root.findViewById(R.id.malt_weight_oz);
        mColorEdit = (EditText) root.findViewById(R.id.malt_color);
        mGravityEdit = (EditText) root.findViewById(R.id.malt_gravity);
        mDescription = (TextView) root.findViewById(R.id.description);

        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mCustomNameView = root.findViewById(R.id.custom_name_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());
        mMaltInfoList = new MaltStorage(getActivity()).getMalts();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mMaltIndex = state.getInt(MALT_INDEX, -1);
        }

        String customName = getActivity().getResources().getString(R.string.custom_malt);
        mAdapter = new NameableAdapter<MaltInfo>(getActivity(), mMaltInfoList, customName);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaltSpinner.setAdapter(mAdapter);
        mMaltSpinner.setOnItemSelectedListener(this);

        if (mRecipe != null && mMaltIndex >= 0) {
            MaltAddition addition = getMaltAddition();
            setMalt(addition.getMalt());
            setWeight(addition.getWeight());
            mColorEdit.setText(Util.fromDouble(addition.getMalt().getColor(), 1));
            mGravityEdit.setText(Util.fromDouble(addition.getMalt().getGravity(), 3, false));
        }

        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_malt_addition));
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        MaltAddition addition = getMaltAddition();

        Nameable selectedMalt = (Nameable) mMaltSpinner.getSelectedItem();
        Malt storedMalt = new Malt();
        storedMalt.setColor(Util.toDouble(mColorEdit.getText()));

        double gravity = Util.toDouble(mGravityEdit.getText());
        if (gravity < 1) {
            gravity = 1;
        }
        storedMalt.setGravity(gravity);

        mAdapter.setNamedItem(selectedMalt, storedMalt, mCustomName.getText().toString());

        addition.setMalt(storedMalt);

        Weight weight = new Weight(Util.toDouble(mWeightLbEdit.getText()), Util.toDouble(mWeightOzEdit.getText()));
        addition.setWeight(weight);

        mStorage.updateRecipe(mRecipe);
        Util.hideKeyboard(getActivity());
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
        MaltInfo info = mMaltInfoList.findByName(malt.getName());
        int index = mMaltInfoList.indexOf(info);
        if (index < 0 ) {
            mCustomName.setText(malt.getName());
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            mMaltSpinner.setSelection(0);
        } else {
            mMaltSpinner.setSelection(index + 1);
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
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
        Nameable item = (Nameable) mMaltSpinner.getSelectedItem();
        if (handleCustomName(item)) {
            return;
        }
        MaltInfo info = (MaltInfo) item;
        if (!info.getName().equals(getMaltAddition().getMalt().getName())) {
            getMaltAddition().getMalt().setName(info.getName());
            mColorEdit.setText(Util.fromDouble(info.getSrm(), 1));
            mGravityEdit.setText(Util.fromDouble(info.getGravity(), 3, false));
        }
        if (info.getDescription().length() == 0) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(info.getDescription()));
        }
    }

    private boolean handleCustomName(Nameable item) {
        boolean handled = false;
        String customName = getActivity().getResources().getString(R.string.custom_malt);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getMaltAddition().getMalt().getName())) {
                mCustomName.setText("");
                mColorEdit.setText("0");
                mGravityEdit.setText("1.000");
            }
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            handled = true;
        } else {
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        return handled;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
