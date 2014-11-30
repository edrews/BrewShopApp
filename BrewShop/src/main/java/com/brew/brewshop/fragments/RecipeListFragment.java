package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.ViewClickListener;
import com.brew.brewshop.fragments.com.brew.brewshop.fragments.NewRecipeAdapter;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.xml.BeerXML;
import com.brew.brewshop.xml.ParseXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeListFragment extends Fragment implements ViewClickListener,
        DialogInterface.OnClickListener,
        AdapterView.OnItemClickListener,
        RecipeChangeHandler,
        ActionMode.Callback {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeListFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";
    private static final String SHOWING_ID = "ShowingId";
    private static final int READ_REQUEST_CODE = 1;
    private static final int WRITE_REQUEST_CODE = 2;

    private BrewStorage mStorage;
    private FragmentHandler mViewSwitcher;
    private ActionMode mActionMode;
    private RecipeListView mRecipeView;
    private Dialog mSelectNewRecipe = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        setHasOptionsMenu(true);

        checkResumeActionMode(bundle);
        mViewSwitcher.setTitle(getTitle());

        mStorage = new BrewStorage(getActivity());
        mRecipeView = new RecipeListView(getActivity(), rootView, mStorage, this);
        mRecipeView.drawRecipeList();
        if (bundle != null) {
            int id = bundle.getInt(SHOWING_ID, -1);
            mRecipeView.setShowing(id);
        }

        return rootView;
    }

    public String getTitle() {
        return getActivity().getResources().getString(R.string.homebrew_recipes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStorage.close();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(ACTION_MODE, mActionMode != null);
        if (mRecipeView != null) {
            bundle.putInt(SHOWING_ID, mRecipeView.getShowingId());
        }
        if (mActionMode != null) {
            bundle.putIntArray(SELECTED_INDEXES, mRecipeView.getSelectedIds());
        }
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
    public void onClick(View view) {
        boolean selected = (Boolean) view.getTag(R.integer.is_recipe_selected);
        int id = (Integer) view.getTag(R.integer.recipe_id);
        if (mActionMode != null) {
            mRecipeView.setSelected(id, !selected);
            if (mRecipeView.getSelectedCount() == 0) {
                mActionMode.finish();
            }
            updateActionBar();
        } else {
            if (mViewSwitcher != null) {
                Recipe recipe = mStorage.retrieveRecipes().findById(id);
                if (!mRecipeView.isShowing(recipe)) {
                    showRecipe(recipe);
                }
            } else {
                Log.d(TAG, "Recipe manager is not set");
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = (Integer) view.getTag(R.integer.recipe_id);
        if (mActionMode != null) {
            updateActionBar();
            return false;
        } else {
            startActionMode(new int[] {id});
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_new_recipe && canCreateRecipe()) {
            if (getResources().getBoolean(R.bool.show_open_recipe)) {
                showSelectNewRecipeDialog();
            } else {
                createAndShowNewRecipe();
            }
            return true;
        }
        return false;
    }

    private void showSelectNewRecipeDialog() {
        mSelectNewRecipe = new Dialog(getActivity());
        mSelectNewRecipe.setContentView(R.layout.select_ingredient);

        NewRecipeAdapter newRecipeAdapter = new NewRecipeAdapter(getActivity(), getNewRecipeTypes());

        ListView listView = (ListView) mSelectNewRecipe.findViewById(R.id.recipe_list);
        listView.setAdapter(newRecipeAdapter);
        listView.setOnItemClickListener(this);
        mSelectNewRecipe.setCancelable(true);
        mSelectNewRecipe.setTitle(findString(R.string.new_recipe));
        mSelectNewRecipe.show();
    }

    private List<String> getNewRecipeTypes() {
        String[] ingredients = getActivity().getResources().getStringArray(R.array.new_recipe_types);
        return Arrays.asList(ingredients);
    }

    private void showRecipe(Recipe recipe) {
        int id = -1;
        if (recipe != null) {
            id = recipe.getId();
        }
        mRecipeView.setShowing(id);
        mViewSwitcher.showRecipeEditor(recipe);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int checked = mRecipeView.getSelectedCount();
        mActionMode.setTitle(getResources().getString(R.string.select_recipes));
        mActionMode.setSubtitle(checked + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (mRecipeView.getSelectedCount() > 0);
        mActionMode.getMenu().findItem(R.id.action_delete).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_save).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_select_all).setVisible(!mRecipeView.areAllSelected());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                Log.d(TAG, "Select all");
                mRecipeView.setAllSelected(true);
                updateActionBar();
                return true;
            case R.id.action_delete:
                int count = mRecipeView.getSelectedCount();
                String message;
                if (count > 1) {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_recipes), count);
                } else {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_recipe), count);
                }
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(R.string.yes, this)
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case R.id.action_save:
                saveRecipes();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mRecipeView.setAllSelected(false);
        mActionMode = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int deleted = deleteSelected();
        mActionMode.finish();
        toastDeleted(deleted);
    }

    private void checkResumeActionMode(Bundle bundle) {
        if (bundle != null) {
            if (bundle.getBoolean(ACTION_MODE)) {
                int[] selected = bundle.getIntArray(SELECTED_INDEXES);
                startActionMode(selected);
            }
        }
    }

    private void startActionMode(int[] selectedIds) {
        for (int i = 0; i < selectedIds.length; i++) {
            mRecipeView.setSelected(selectedIds[i], true);
        }
        ((ActionBarActivity) getActivity()).startSupportActionMode(this);
    }

    private boolean canCreateRecipe() {
        int maxRecipes = getActivity().getResources().getInteger(R.integer.max_recipes);
        if (mStorage.retrieveRecipes().size() >= maxRecipes) {
            String message = String.format(getActivity().getResources().getString(R.string.max_recipes_reached), maxRecipes);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int deleteSelected() {
        int[] selectedIds = mRecipeView.getSelectedIds();
        int showingId = mRecipeView.getShowingId();
        for (int i = 0; i < selectedIds.length; i ++) {
            int id = selectedIds[i];
            mStorage.deleteRecipe(mStorage.retrieveRecipes().findById(id));
            if (showingId == id) {
                showRecipe(null);
            }
        }
        mRecipeView.removeSelected();
        return selectedIds.length;
    }

    private int saveRecipes() {
        Intent fileIntent;
        fileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("*/*");

        if (isAvailable(getActivity().getBaseContext(), fileIntent)) {
            startActivityForResult(fileIntent, WRITE_REQUEST_CODE);
        } else {
            manualSaveRecipes();
        }
        return 1;
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void toastDeleted(int deleted) {
        Context context = getActivity();
        String message;
        if (deleted > 1) {
            message = String.format(context.getResources().getString(R.string.deleted_recipes), deleted);
        } else {
            message = context.getResources().getString(R.string.deleted_recipe);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void toastSaved(int saved) {
        Context context = getActivity();
        String message;
        if (saved > 1) {
            message = String.format(context.getResources().getString(R.string.saved_recipes), saved);
        } else {
            message = context.getResources().getString(R.string.saved_recipe);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecipeClosed(int recipeId) {
        if (mRecipeView != null) {
            mRecipeView.setShowing(-1);
        }
    }

    @Override
    public void onRecipeUpdated(int recipeId) {
        if (mRecipeView != null) {
            mRecipeView.setShowing(recipeId);
            mRecipeView.drawRecipeList();
        }
    }

    private String findString(int id) {
        return getActivity().getResources().getString(id);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSelectNewRecipe.dismiss();
        String type = getNewRecipeTypes().get(position);
        if (findString(R.string.new_recipe).equals(type)) {
            createAndShowNewRecipe();
        } else if (findString(R.string.open).equals(type)) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            Intent fileIntent;
            if (currentAPIVersion >= Build.VERSION_CODES.KITKAT) {
                // Use the system file chooser only showing XML files we can open
                fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileIntent.setType("*/*");
            } else {
                Intent chooseFile;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                fileIntent = Intent.createChooser(chooseFile, "Choose a file");
            }
            startActivityForResult(fileIntent, READ_REQUEST_CODE);
        }
    }

    private void createAndShowNewRecipe() {
        Recipe recipe = new Recipe();
        mStorage.createRecipe(recipe);
        mRecipeView.drawRecipeList();
        showRecipe(recipe);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // We got a file. Open it for parsing.
            Uri recipeUri = null;

            if (resultData != null) {
                recipeUri = resultData.getData();
                Recipe[] recipes = null;
                try {
                    InputStream recipeStream =
                            getActivity().getContentResolver().openInputStream(recipeUri);

                    byte[] buffer = new byte[100];
                    String type = null;

                    try {
                        recipeStream.read(buffer);
                        type = ParseXML.checkRecipeType(new String(buffer));
                        recipeStream.close();
                    } catch (IOException ioe) {
                        Log.e("BrewShop", "Couldn't check file type");
                        return;
                    }

                    if (type == null) {
                        AlertDialog.Builder alertDialog =
                                new AlertDialog.Builder(this.getActivity());
                        alertDialog.setMessage(R.string.no_recipe_type)
                                .setTitle(R.string.open);
                        alertDialog.create().show();
                        return;
                    }
                    recipeStream =
                            getActivity().getContentResolver().openInputStream(recipeUri);
                    recipes = ParseXML.readRecipe(recipeStream, type);
                } catch (FileNotFoundException fnfe) {
                    // Shouldn't happen
                    Log.e("BrewShop", "Couldn't find file: " + fnfe.getMessage(), fnfe);
                    return;
                }

                if (recipes != null && recipes.length > 0) {
                    for (Recipe recipe: recipes) {
                        mStorage.createRecipe(recipe);
                    }
                    mRecipeView.drawRecipeList();

                    AlertDialog.Builder alertDialog =
                            new AlertDialog.Builder(this.getActivity());
                    alertDialog.setMessage(String.format(
                            getActivity().getResources().getString(
                                    R.string.opened_recipes), recipes.length))
                            .setTitle(R.string.open);
                    alertDialog.create().show();

                    if (recipes.length == 1) {
                        showRecipe(recipes[0]);
                    }
                }
            }
        }

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int[] selectedIds = mRecipeView.getSelectedIds();
            ArrayList<Recipe> toSave = new ArrayList<Recipe>();

            for (int i = 0; i < selectedIds.length; i++) {
                int id = selectedIds[i];
                toSave.add(mStorage.retrieveRecipes().findById(id));
            }

            final Recipe[] recipeArray = toSave.toArray(new Recipe[toSave.size()]);

            if (resultData == null) {
                return;
            }

            OutputStream recipeOutputStream;
            try {
                Uri recipeUri = resultData.getData();
                recipeOutputStream = getActivity().getContentResolver().openOutputStream(recipeUri);
            } catch (FileNotFoundException e) {
                Log.e("Saving recipes", "File not found.", e);
                return;
            }

            try {
                BeerXML.writeRecipes(recipeArray, recipeOutputStream);
            } catch (IOException ioe) {
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(String.format(
                        getActivity().getResources().getString(
                                R.string.saved_recipes), recipeArray.length))
                        .setTitle(R.string.open);
                alertDialog.create().show();
            }

            mActionMode.finish();
            toastSaved(recipeArray.length);
        }
    }

    private void manualSaveRecipes() {
        int[] selectedIds = mRecipeView.getSelectedIds();
        ArrayList<Recipe> toSave = new ArrayList<Recipe>();

        for (int i = 0; i < selectedIds.length; i++) {
            int id = selectedIds[i];
            toSave.add(mStorage.retrieveRecipes().findById(id));
        }

        final Recipe[] recipeArray = toSave.toArray(new Recipe[toSave.size()]);
        final EditText input = new EditText(this.getActivity());

        new AlertDialog.Builder(this.getActivity())
                .setTitle("File name?")
                .setMessage("What would you like to save the file as")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String filename = input.getText().toString();

                        if (filename.equals("")) {
                            return;
                        }

                        if (!filename.endsWith(".xml")) {
                            filename += ".xml";
                        }
                        File outputFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);

                        try {
                            BeerXML.writeRecipes(recipeArray, outputFile);
                        } catch (IOException ioe) {
                            AlertDialog.Builder alertDialog =
                                    new AlertDialog.Builder(getActivity());
                            alertDialog.setMessage(String.format(
                                    getActivity().getResources().getString(
                                            R.string.cannot_write_file), outputFile.getAbsolutePath()))
                                    .setTitle(R.string.open);
                            alertDialog.create().show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
