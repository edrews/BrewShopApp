package com.brew.brewshop.fragments;

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
import com.brew.brewshop.storage.hops.HopsInfo;
import com.brew.brewshop.storage.hops.HopsInfoList;
import com.brew.brewshop.storage.hops.HopsStorage;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;

public class HopsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @SuppressWarnings("unused")
    private static final String TAG = HopsFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String HOP_INDEX = "HopIndex";

    private Recipe mRecipe;
    private HopsInfoList mHopInfo;
    private BrewStorage mStorage;
    private int mHopIndex;
    NameableAdapter<HopsInfo> mAdapter;

    private Spinner mSpinner;
    private TextView mDescription;
    private EditText mWeightEdit;
    private EditText mAlphaEdit;
    private EditText mTimeEdit;
    private EditText mCustomName;
    private View mCustomNameView;
    private View mDescriptionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_hops, container, false);
        mSpinner = (Spinner) root.findViewById(R.id.hops_type);
        mWeightEdit = (EditText) root.findViewById(R.id.hops_weight);
        mAlphaEdit = (EditText) root.findViewById(R.id.hops_alpha);
        mTimeEdit = (EditText) root.findViewById(R.id.hops_time);
        mDescription = (TextView) root.findViewById(R.id.description);
        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mCustomNameView = root.findViewById(R.id.custom_malt_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());
        mHopInfo = new HopsStorage(getActivity()).getHops();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mHopIndex = state.getInt(HOP_INDEX, -1);
        }

        String customName = getActivity().getResources().getString(R.string.custom_hop);
        mAdapter = new NameableAdapter<HopsInfo>(getActivity(), mHopInfo, customName);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(this);

        if (mRecipe != null && mHopIndex >= 0) {
            HopAddition addition = getHopAddition();
            setHop(addition.getHop());
            mWeightEdit.setText(Util.fromDouble(addition.getWeight().getOunces(), 3));
            mAlphaEdit.setText(Util.fromDouble(addition.getHop().getPercentAlpha(), 3));
            mTimeEdit.setText(String.valueOf(addition.getTime()));
        }

        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_hop_addition));
        }
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        HopAddition addition = getHopAddition();

        Nameable selectedHop = (Nameable) mSpinner.getSelectedItem();
        Hop storedHop = new Hop();
        double alpha = Util.toDouble(mAlphaEdit.getText());
        if (alpha > 100) {
            alpha = 100;
        }
        storedHop.setPercentAlpha(alpha);
        mAdapter.setNamedItem(selectedHop, storedHop, mCustomName.getText().toString());
        addition.setHop(storedHop);

        Weight weight = new Weight(0, Util.toDouble(mWeightEdit.getText()));
        addition.setWeight(weight);
        addition.setTime(Util.toInt(mTimeEdit.getText()));

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
        state.putInt(HOP_INDEX, mHopIndex);
    }

    private HopAddition getHopAddition() {
        return  mRecipe.getHops().get(mHopIndex);
    }

    private void setHop(Hop hop) {
        HopsInfo info = mHopInfo.findByName(hop.getName());
        int index = mHopInfo.indexOf(info);
        if (index < 0 ) {
            mCustomName.setText(hop.getName());
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            mSpinner.setSelection(0);
        } else {
            mSpinner.setSelection(index + 1);
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setHopIndex(int index) {
        mHopIndex = index;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Nameable item = (Nameable) mSpinner.getSelectedItem();
        if (handleCustomName(item)) {
            return;
        }
        HopsInfo hopsInfo = (HopsInfo) item;
        if (!hopsInfo.getName().equals(getHopAddition().getHop().getName())) {
            mAlphaEdit.setText(Util.fromDouble(hopsInfo.getAlphaAcid(), 3));
            getHopAddition().getHop().setName(hopsInfo.getName());
        }
        if (hopsInfo.getDescription().length() == 0) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(hopsInfo.getDescription()));
        }
    }

    private boolean handleCustomName(Nameable item) {
        boolean handled = false;
        String customName = getActivity().getResources().getString(R.string.custom_hop);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getHopAddition().getHop().getName())) {
                mCustomName.setText("");
                mAlphaEdit.setText("0");
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
