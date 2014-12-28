package com.brew.brewshop.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = SettingsFragment.class.getName();

    private FragmentHandler mFragmentHandler;
    private Spinner mUnitsSpinner;
    private ArrayAdapter<CharSequence> mUnitsAdapter;
    private Settings mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mSettings = new Settings(getActivity());
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        mUnitsSpinner = (Spinner) root.findViewById(R.id.units_spinner);
        mUnitsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.units, R.layout.spinner_item);
        mUnitsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mUnitsSpinner.setAdapter(mUnitsAdapter);
        mUnitsSpinner.setOnItemSelectedListener(this);
        setUnitsSpinner();

        setHasOptionsMenu(true);
        mFragmentHandler.setTitle(getTitle());
        return root;
    }

    public String getTitle() {
        return getActivity().getResources().getString(R.string.settings);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if (item.equals(getActivity().getString(R.string.imperial))) {
            mSettings.setUnits(Settings.Units.IMPERIAL);
        } else if (item.equals(getActivity().getString(R.string.metric))) {
            mSettings.setUnits(Settings.Units.METRIC);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setUnitsSpinner() {
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                mUnitsSpinner.setSelection(mUnitsAdapter.getPosition(getActivity().getString(R.string.imperial)));
                break;
            case METRIC:
                mUnitsSpinner.setSelection(mUnitsAdapter.getPosition(getActivity().getString(R.string.metric)));
                break;
        }
    }
}
