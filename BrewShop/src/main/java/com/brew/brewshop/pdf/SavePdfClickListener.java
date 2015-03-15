package com.brew.brewshop.pdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.EditText;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.recipes.Recipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePdfClickListener implements DialogInterface.OnClickListener {

    private Context mContext;
    private EditText mEditText;
    private Recipe mRecipe;

    public SavePdfClickListener(Context context, EditText editText, Recipe recipe) {
        mContext = context;
        mEditText = editText;
        mRecipe = recipe;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String filename = mEditText.getText().toString();
        if (filename.equals("")) {
            return;
        }

        if (!filename.endsWith(".pdf")) {
            filename += ".pdf";
        }
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            WritePdfTask task = new WritePdfTask(mContext, mRecipe, fileOutputStream);
            task.execute();
        } catch (IOException ioe) {
            AlertDialog.Builder errorMsg = new AlertDialog.Builder(mContext);
            String message = String.format(mContext.getString(R.string.cannot_write_file), outputFile.getAbsolutePath());
            errorMsg.setMessage(message);
            errorMsg.setTitle(R.string.open);
            errorMsg.create().show();
        }
    }
}
