package com.arlbrew.brewshop;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlbrew.brewshop.storage.BrewStorage;
import com.arlbrew.brewshop.storage.RecipeListAdapter;

public class RecipeListView {
    private LinearLayout mRecipeView;
    private RecipeListAdapter mRecipeAdapter;
    private BrewStorage mStorage;
    private TextView mMessageView;
    private ViewClickListener mListener;

    public RecipeListView(Context context, View view, BrewStorage storage, ViewClickListener listener) {
        mRecipeView = (LinearLayout) view.findViewById(R.id.recipe_layout);
        mMessageView = (TextView) view.findViewById(R.id.error_message);
        mStorage = storage;
        mListener = listener;
        mRecipeAdapter = new RecipeListAdapter(context, mStorage, mRecipeView);
    }

    public void drawRecipeList() {
        mRecipeView.removeAllViews();
        int nRecipes = mStorage.retrieveRecipes().size();
        if (nRecipes == 0) {
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
            for (int i = 0; i < nRecipes; i++) {
                View view = mRecipeAdapter.getView(i, null, mRecipeView);
                view.setTag(R.integer.list_index, i);
                view.setTag(R.integer.is_selected, false);
                view.setOnClickListener(mListener);
                view.setOnLongClickListener(mListener);
                mRecipeView.addView(view);
            }
        }
    }

    public boolean areAllSelected() {
        return (mStorage.retrieveRecipes().size() == getSelectedCount());
    }

    public void setAllSelected(boolean selected) {
        for (int i = 0; i < mStorage.retrieveRecipes().size(); i++) {
            setSelected(i, selected);
        }
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_selected)) {
                count++;
            }
        }
        return count;
    }

    public boolean isRecipeSelected(int index) {
        View view = mRecipeView.getChildAt(index);
        return (Boolean) view.getTag(R.integer.is_selected);
    }

    public void setSelected(int position, boolean selected) {
        View view = mRecipeView.getChildAt(position);
        view.setTag(R.integer.is_selected, selected);
        View container = view.findViewById(R.id.item_container);
        if (selected) {
            container.setBackgroundResource(R.color.color_accent);
        } else {
            container.setBackgroundResource(R.drawable.touchable);
        }
    }

    public int[] getSelectedIndexes() {
        int[] indexes = new int[getSelectedCount()];
        int indexOffset = 0;
        for (int i = 0; i < mStorage.retrieveRecipes().size(); i++) {
            if (isRecipeSelected(i)) {
                indexes[indexOffset] = i;
                indexOffset++;
            }
        }
        return indexes;
    }
}
