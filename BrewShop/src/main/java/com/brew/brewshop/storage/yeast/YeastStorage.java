package com.brew.brewshop.storage.yeast;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public class YeastStorage {
    private static YeastInfoList sCache;

    private Context mContext;

    public YeastStorage(Context context) {
        mContext = context;
    }

    @SuppressWarnings("unchecked")
    public YeastInfoList getYeast() {
        if (sCache != null) {
            return sCache;
        }
        try {
            sCache = new YeastInfoList();
            JsonReader reader = new JsonReader(mContext, YeastInfoList.class);
            sCache.addAll(reader.readAll(R.raw.yeasts, "yeasts"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sCache;
    }
}
