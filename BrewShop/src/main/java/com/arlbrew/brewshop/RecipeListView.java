package com.arlbrew.brewshop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlbrew.brewshop.storage.BrewStorage;
import com.arlbrew.brewshop.storage.RecipeListAdapter;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.RecipeList;

public class RecipeListView {
    private static final String TAG = RecipeListView.class.getName();

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
        int id = getShowingId();
        mRecipeView.removeAllViews();
        RecipeList recipes = mStorage.retrieveRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            View view = mRecipeAdapter.getView(i, null, mRecipeView);
            view.setOnClickListener(mListener);
            view.setOnLongClickListener(mListener);
            mRecipeView.addView(view);
        }
        setShowing(id);
        updateRecipeListView();
    }

    public void updateRecipeListView() {
        int nRecipes = mRecipeView.getChildCount();
        if (nRecipes == 0) {
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
            for (int i = 0; i < nRecipes; i++) {
                View view = mRecipeView.getChildAt(i);
                View container = view.findViewById(R.id.item_container);
                if ((Boolean) view.getTag(R.integer.is_recipe_selected)) {
                    container.setBackgroundResource(R.color.color_accent);
                } else if ((Boolean) view.getTag(R.integer.is_recipe_showing)) {
                    container.setBackgroundResource(R.color.color_accent_light);
                } else {
                    container.setBackgroundResource(R.drawable.touchable);
                }
            }
        }
    }

    public void removeSelected() {
        int[] ids = getSelectedIds();
        for (int idCount = ids.length - 1; idCount >= 0; idCount--) {
            for (int viewCount = 0; viewCount < mRecipeView.getChildCount(); viewCount++) {
                View view = mRecipeView.getChildAt(viewCount);
                int id = (Integer) view.getTag(R.integer.recipe_id);
                if (id == ids[idCount]) {
                    mRecipeView.removeView(view);
                    break;
                }
            }
        }
        updateRecipeListView();
    }

    public int getShowingId() {
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_recipe_showing)) {
                return (Integer) view.getTag(R.integer.recipe_id);
            }
        }
        return -1;
    }

    public boolean isShowing(Recipe recipeToShow) {
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            int id = (Integer) view.getTag(R.integer.recipe_id);
            if (id == recipeToShow.getId()) {
                return (Boolean) view.getTag(R.integer.is_recipe_showing);
            }
        }
        return false;
    }

    public void setShowing(int recipeId) {
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            int id = (Integer) view.getTag(R.integer.recipe_id);
            view.setTag(R.integer.is_recipe_showing, id == recipeId);
        }
        updateRecipeListView();
    }

    public boolean areAllSelected() {
        return (mStorage.retrieveRecipes().size() == getSelectedCount());
    }

    public void setAllSelected(boolean selected) {
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            view.setTag(R.integer.is_recipe_selected, selected);
        }
        updateRecipeListView();
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_recipe_selected)) {
                count++;
            }
        }
        return count;
    }

    public void setSelected(int recipeId, boolean selected) {
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            int id = (Integer) view.getTag(R.integer.recipe_id);
            if (id == recipeId) {
                view.setTag(R.integer.is_recipe_selected, selected);
                break;
            }
        }
        updateRecipeListView();
    }

    public int[] getSelectedIds() {
        int[] ids = new int[getSelectedCount()];
        int indexOffset = 0;
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_recipe_selected)) {
                ids[indexOffset] = (Integer) view.getTag(R.integer.recipe_id);
                indexOffset++;

            }
        }
        return ids;
    }
}
