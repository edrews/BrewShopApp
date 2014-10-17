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
import com.brew.brewshop.storage.hops.HopsInfo;
import com.brew.brewshop.storage.hops.HopsInfoList;
import com.brew.brewshop.storage.hops.HopsStorage;
import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.malt.MaltStorage;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;

public class HopsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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

        if (mRecipe != null && mHopIndex >= 0) {
            HopAddition addition = mRecipe.getHops().get(mHopIndex);
            setHop(addition.getHop());
            mWeightEdit.setText(String.valueOf(addition.getWeight().getOunces()));
            mAlphaEdit.setText(String.valueOf(addition.getHop().getAlpha()));
            mTimeEdit.setText(String.valueOf(addition.getTime()));
        }

        mSpinner.setOnItemSelectedListener(this);

        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.edit_hop_addition));

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        HopAddition addition = mRecipe.getHops().get(mHopIndex);

        HopsInfo info = (HopsInfo) mSpinner.getSelectedItem();
        Hop hop = new Hop();
        hop.setAlpha(Util.toDouble(mAlphaEdit.getText()));
        hop.setId(info.getId());
        hop.setName(info.getName());
        addition.setHop(hop);

        Weight weight = new Weight(0, Util.toDouble(mWeightEdit.getText()));
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
        state.putInt(HOP_INDEX, mHopIndex);
    }

    private void setHop(Hop hop) {
        HopsInfo info = mHopInfo.findById(hop.getId());
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
        HopsInfo info = (HopsInfo) mSpinner.getSelectedItem();
        mAlphaEdit.setText(String.valueOf(info.getAlphaAcidMin()));
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
