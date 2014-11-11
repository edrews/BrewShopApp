package com.brew.brewshop.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.NameableAdapter;
import com.brew.brewshop.storage.hops.HopsInfo;
import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.storage.yeast.YeastInfo;
import com.brew.brewshop.storage.yeast.YeastInfoList;
import com.brew.brewshop.storage.yeast.YeastStorage;
import com.brew.brewshop.util.Util;

public class YeastFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    @SuppressWarnings("unused")
    private static final String TAG = YeastFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String YEAST_INDEX = "YeastIndex";

    private Recipe mRecipe;
    private YeastInfoList mYeastInfo;
    private BrewStorage mStorage;
    private int mYeastIndex;
    NameableAdapter<YeastInfo> mAdapter;

    private Spinner mSpinner;
    private TextView mDescription;
    private EditText mAttenuationEdit;
    private FragmentHandler mViewSwitcher;
    private EditText mCustomName;
    private View mCustomNameView;
    private View mDescriptionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_yeast, container, false);
        mSpinner = (Spinner) root.findViewById(R.id.yeast_type);
        mAttenuationEdit = (EditText) root.findViewById(R.id.yeast_attenuation);
        mDescription = (TextView) root.findViewById(R.id.description);
        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mCustomNameView = root.findViewById(R.id.custom_name_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());
        mYeastInfo = new YeastStorage(getActivity()).getYeast();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mYeastIndex = state.getInt(YEAST_INDEX, -1);
        }

        String customName = getActivity().getResources().getString(R.string.custom_yeast);
        mAdapter = new NameableAdapter<YeastInfo>(getActivity(), mYeastInfo, customName);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);

        if (mRecipe != null && mYeastIndex >= 0) {
            Yeast yeast = getYeast();
            setYeast(yeast);
            mAttenuationEdit.setText(Util.fromDouble(yeast.getAttenuation(), 3));
        }

        mSpinner.setOnItemSelectedListener(this);
        mViewSwitcher.setTitle(getActivity().getResources().getString(R.string.edit_yeast_addition));
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Yeast storedYeast = mRecipe.getYeast().get(mYeastIndex);

        Nameable selectedYeast = (Nameable) mSpinner.getSelectedItem();
        mAdapter.setNamedItem(selectedYeast, storedYeast, mCustomName.getText().toString());

        double attenuation = Util.toDouble(mAttenuationEdit.getText());
        if (attenuation > 100) {
            attenuation = 100;
        }
        storedYeast.setAttenuation(attenuation);

        mStorage.updateRecipe(mRecipe);
        Util.hideKeyboard(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mViewSwitcher = (FragmentHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentHandler.class.getName());
        }
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
        state.putInt(YEAST_INDEX, mYeastIndex);
    }

    private Yeast getYeast() {
        return mRecipe.getYeast().get(mYeastIndex);
    }

    private void setYeast(Yeast yeast) {
        YeastInfo info = mYeastInfo.findByName(yeast.getName());
        int index = mYeastInfo.indexOf(info);
        if (index < 0 ) {
            mCustomName.setText(yeast.getName());
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

    public void setYeastIndex(int index) {
        mYeastIndex = index;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Nameable item = (Nameable) mSpinner.getSelectedItem();
        if (handleCustomName(item)) {
            return;
        }
        YeastInfo info = (YeastInfo) item;
        if (!info.getName().equals(getYeast().getName())) {
            getYeast().setName(info.getName());
            mAttenuationEdit.setText(Util.fromDouble(getAttenuation(info), 3));
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
        String customName = getActivity().getResources().getString(R.string.custom_yeast);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getYeast().getName())) {
                mCustomName.setText("");
                mAttenuationEdit.setText("0");
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

    private double getAttenuation(YeastInfo info) {
        return (info.getAttenuationMax() + info.getAttenuationMin()) / 2;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
