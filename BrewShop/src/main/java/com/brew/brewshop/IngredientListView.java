package com.brew.brewshop;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brew.brewshop.storage.recipes.Recipe;

public class IngredientListView {
    private LinearLayout mIngredientView;
    private IngredientListAdapter mIngredientAdapter;
    private Recipe mRecipe;
    private TextView mEmptyView;
    private ViewClickListener mListener;

    public IngredientListView(Context context, View view, Recipe recipe, ViewClickListener listener) {
        mIngredientView = (LinearLayout) view.findViewById(R.id.ingredient_list);
        mEmptyView = (TextView) view.findViewById(R.id.no_ingredients);
        mRecipe = recipe;
        mListener = listener;
        mIngredientAdapter = new IngredientListAdapter(context, recipe);
    }

    public void drawList() {
        mIngredientView.removeAllViews();
        int count = mRecipe.getIngredients().size();
        if (count == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            for (int i = 0; i < count; i++) {
                View view = mIngredientAdapter.getView(i, null, mIngredientView);
                view.setTag(R.integer.list_index, i);
                view.setTag(R.integer.is_selected, false);
                view.setTag(R.string.ingredients, mRecipe.getIngredients().get(i));
                view.setOnClickListener(mListener);
                view.setOnLongClickListener(mListener);
                mIngredientView.addView(view);
            }
        }
    }

    public boolean areAllSelected() {
        return (mRecipe.getIngredients().size() == getSelectedCount());
    }

    public void setAllSelected(boolean selected) {
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            setSelected(i, selected);
        }
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < mIngredientView.getChildCount(); i++) {
            View view = mIngredientView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_selected)) {
                count++;
            }
        }
        return count;
    }

    public boolean isSelected(int index) {
        View view = mIngredientView.getChildAt(index);
        return (Boolean) view.getTag(R.integer.is_selected);
    }

    public void setSelected(int position, boolean selected) {
        View view = mIngredientView.getChildAt(position);
        view.setTag(R.integer.is_selected, selected);
        View ingredientView = view.findViewById(R.id.ingredient_layout);
        if (selected) {
            ingredientView.setBackgroundResource(R.color.color_accent);
        } else {
            ingredientView.setBackgroundResource(R.drawable.touchable);
        }
    }

    public int[] getSelectedIndexes() {
        int[] indexes = new int[getSelectedCount()];
        int indexOffset = 0;
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            if (isSelected(i)) {
                indexes[indexOffset] = i;
                indexOffset++;
            }
        }
        return indexes;
    }
}
