package com.brew.brewshop.storage.style;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public class BjcpCategoryStorage {
    private static BjcpCategoryList sStyleCache;

    private Context mContext;

    public BjcpCategoryStorage(Context context) {
        mContext = context;
    }

    public BjcpCategoryList getStyles() {
        if (sStyleCache != null) {
            return sStyleCache;
        }
        try {
            JsonReader reader = new JsonReader(mContext, BjcpCategoryList.class);
            sStyleCache = reader.readAll(R.raw.bjcp, "beers");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sStyleCache;
    }
}
