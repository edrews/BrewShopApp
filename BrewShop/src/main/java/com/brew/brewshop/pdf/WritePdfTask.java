package com.brew.brewshop.pdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.recipes.Recipe;

import java.io.OutputStream;

public class WritePdfTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog mDialog;
    private Context mContext;
    private Recipe mRecipe;
    private OutputStream mStream;

    public WritePdfTask(Context context, Recipe recipe, OutputStream outStream) {
        mContext = context;
        mRecipe = recipe;
        mStream = outStream;
        mDialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        mDialog.setMessage(mContext.getString(R.string.save_recipe_progress));
        mDialog.show();
    }

    @Override
    protected void onPostExecute(Void success) {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        PdfRecipeWriter writer = new PdfRecipeWriter(mContext, mRecipe);
        writer.write(mStream);
        return null;
    }
}
