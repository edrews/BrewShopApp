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
import com.arlbrew.brewshop.storage.hops.HopsInfo;
import com.arlbrew.brewshop.storage.hops.HopsInfoList;
import com.arlbrew.brewshop.storage.hops.HopsStorage;
import com.arlbrew.brewshop.storage.recipes.Hop;
import com.arlbrew.brewshop.storage.recipes.HopAddition;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.Weight;
import com.arlbrew.brewshop.util.Util;

public class HopsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @SuppressWarnings("unused")
    private static final String TAG = HopsFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String HOP_INDEX = "HopIndex";

    private Recipe mRecipe;
    private HopsInfoList mHopInfo;
    private BrewStorage mStorage;
    private int mHopIndex;

    private Spinner mSpinner;
    private TextView mDescription;
    private EditText mWeightEdit;
    private EditText mAlphaEdit;
    private EditText mTimeEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_hops, container, false);
        mSpinner = (Spinner) root.findViewById(R.id.hops_type);
        mWeightEdit = (EditText) root.findViewById(R.id.hops_weight);
        mAlphaEdit = (EditText) root.findViewById(R.id.hops_alpha);
        mTimeEdit = (EditText) root.findViewById(R.id.hops_time);
        mDescription = (TextView) root.findViewById(R.id.description);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());
        mHopInfo = new HopsStorage(getActivity()).getHops();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mHopIndex = state.getInt(HOP_INDEX, -1);
        }

        NameableAdapter<HopsInfo> adapter = new NameableAdapter<HopsInfo>(getActivity(), mHopInfo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        if (mRecipe != null && mHopIndex >= 0) {
            HopAddition addition = getHopAddition();
            setHop(addition.getHop());
            mWeightEdit.setText(Util.fromDouble(addition.getWeight().getOunces(), 3));
            mAlphaEdit.setText(Util.fromDouble(addition.getHop().getPercentAlpha(), 3));
            mTimeEdit.setText(String.valueOf(addition.getTime()));
        }

        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_hop_addition));
        }
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        HopAddition addition = getHopAddition();

        HopsInfo info = (HopsInfo) mSpinner.getSelectedItem();
        Hop hop = new Hop();
        double alpha = Util.toDouble(mAlphaEdit.getText());
        if (alpha > 100) {
            alpha = 100;
        }
        hop.setPercentAlpha(alpha);
        hop.setName(info.getName());
        addition.setHop(hop);

        Weight weight = new Weight(0, Util.toDouble(mWeightEdit.getText()));
        addition.setWeight(weight);
        addition.setTime(Util.toInt(mTimeEdit.getText()));

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
        state.putInt(HOP_INDEX, mHopIndex);
    }

    private HopAddition getHopAddition() {
        return  mRecipe.getHops().get(mHopIndex);
    }

    private void setHop(Hop hop) {
        HopsInfo info = mHopInfo.findByName(hop.getName());
        int index = mHopInfo.indexOf(info);
        if (index < 0 ) {
            mSpinner.setSelection(0);
        } else {
            mSpinner.setSelection(index);
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
        HopsInfo hopsInfo = (HopsInfo) mSpinner.getSelectedItem();
        if (!hopsInfo.getName().equals(getHopAddition().getHop().getName())) {
            mAlphaEdit.setText(Util.fromDouble(hopsInfo.getAlphaAcid(), 3));
            getHopAddition().getHop().setName(hopsInfo.getName());
        }
        if (hopsInfo.getDescription().isEmpty()) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(hopsInfo.getDescription()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
