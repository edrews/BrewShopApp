package com.brew.brewshop.storage.style;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public class StyleStorage {
    private static StyleInfoList sStyleCache;

    private Context mContext;

    public StyleStorage(Context context) {
        mContext = context;
    }

    public StyleInfoList getStyles() {
        if (sStyleCache != null) {
            return sStyleCache;
        }
        try {
            JsonReader reader = new JsonReader(mContext, StyleInfoList.class);
            sStyleCache = reader.readAll(R.raw.styles);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sStyleCache;
    }
}
