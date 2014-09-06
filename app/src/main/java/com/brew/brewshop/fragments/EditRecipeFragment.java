package com.brew.brewshop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.brew.brewshop.BeerStyleAdapter;
import com.brew.brewshop.R;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeFragment extends Fragment {
    private static final String TAG = EditRecipeFragment.class.getName();
    private List<String> mStyles;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        Spinner styleSpinner = (Spinner) root.findViewById(R.id.recipe_style);
        Context context = getActivity().getApplicationContext();

        mStyles = new ArrayList<String>();
        mStyles.add("American Lager");
        mStyles.add("India Pale Ale");
        mStyles.add("Irish Stout");
        styleSpinner.setAdapter(new BeerStyleAdapter(context, mStyles));
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe, menu);
    }
}
