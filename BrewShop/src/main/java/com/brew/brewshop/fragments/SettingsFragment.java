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
    private Spinner mExtractUnitsSpinner;
    private ArrayAdapter<CharSequence> mUnitsAdapter;
    private ArrayAdapter<CharSequence> mExtractUnitsAdapter;
    private Settings mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mSettings = new Settings(getActivity());
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        mUnitsSpinner = (Spinner) root.findViewById(R.id.measurement_units_spinner);
        mUnitsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.units, R.layout.spinner_item);
        mUnitsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mUnitsSpinner.setAdapter(mUnitsAdapter);
        mUnitsSpinner.setOnItemSelectedListener(this);
        setUnitsSpinner();

        mExtractUnitsSpinner = (Spinner) root.findViewById(R.id.extract_units_spinner);
        mExtractUnitsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.extract_scales, R.layout.spinner_item);
        mExtractUnitsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mExtractUnitsSpinner.setAdapter(mExtractUnitsAdapter);
        mExtractUnitsSpinner.setOnItemSelectedListener(this);
        setExtractUnitsSpinner();

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
        switch (parent.getId()) {
            case R.id.measurement_units_spinner:
                setMeasurementUnits(parent, position);
                break;
            case R.id.extract_units_spinner:
                setExtractUnits(parent, position);
                break;
        }
    }

    private void setMeasurementUnits(AdapterView<?> parent, int position) {
        String item = (String) parent.getItemAtPosition(position);
        if (item.equals(getActivity().getString(R.string.imperial))) {
            mSettings.setUnits(Settings.Units.IMPERIAL);
        } else if (item.equals(getActivity().getString(R.string.metric))) {
            mSettings.setUnits(Settings.Units.METRIC);
        }
    }

    private void setExtractUnits(AdapterView<?> parent, int position) {
        String item = (String) parent.getItemAtPosition(position);
        if (item.equals(getActivity().getString(R.string.specific_gravity))) {
            mSettings.setExtractUnits(Settings.ExtractUnits.SPECIFIC_GRAVITY);
        } else if (item.equals(getActivity().getString(R.string.degrees_plato))) {
            mSettings.setExtractUnits(Settings.ExtractUnits.DEGREES_PLATO);
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

    private void setExtractUnitsSpinner() {
        switch (mSettings.getExtractUnits()) {
            case SPECIFIC_GRAVITY:
                mExtractUnitsSpinner.setSelection(mExtractUnitsAdapter.getPosition(getActivity().getString(R.string.specific_gravity)));
                break;
            case DEGREES_PLATO:
                mExtractUnitsSpinner.setSelection(mExtractUnitsAdapter.getPosition(getActivity().getString(R.string.degrees_plato)));
                break;
        }
    }
}
