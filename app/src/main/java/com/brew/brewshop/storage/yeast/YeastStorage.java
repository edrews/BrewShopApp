package com.brew.brewshop.storage.yeast;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public class YeastStorage {
    private static final String TAG = YeastStorage.class.getName();

    private static YeastInfoList sCache;

    private Context mContext;

    public YeastStorage(Context context) {
        mContext = context;
    }

    public YeastInfoList getYeast() {
        if (sCache != null) {
            return sCache;
        }
        try {
            sCache = new YeastInfoList();
            JsonReader reader = new JsonReader(mContext, YeastInfoList.class);
            sCache.addAll(reader.readAll(R.raw.yeast1));
            sCache.addAll(reader.readAll(R.raw.yeast2));
            sCache.addAll(reader.readAll(R.raw.yeast3));
            sCache.addAll(reader.readAll(R.raw.yeast4));
            sCache.addAll(reader.readAll(R.raw.yeast5));
            sCache.addAll(reader.readAll(R.raw.yeast6));
            sCache.addAll(reader.readAll(R.raw.yeast7));
            sCache.addAll(reader.readAll(R.raw.yeast8));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sCache;
    }


}
