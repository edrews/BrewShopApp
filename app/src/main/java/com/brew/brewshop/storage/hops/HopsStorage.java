package com.brew.brewshop.storage.hops;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.JsonReader;
import com.brew.brewshop.storage.malt.MaltInfoList;

import org.json.JSONException;

import java.io.IOException;

public class HopsStorage {
    private static final String TAG = HopsStorage.class.getName();

    private static HopsInfoList sHopsCache;

    private Context mContext;

    public HopsStorage(Context context) {
        mContext = context;
    }

    public HopsInfoList getHops() {
        if (sHopsCache != null) {
            return sHopsCache;
        }
        try {
            sHopsCache = new HopsInfoList();
            JsonReader reader = new JsonReader(mContext, HopsInfoList.class);
            sHopsCache.addAll(reader.readAll(R.raw.hops1));
            sHopsCache.addAll(reader.readAll(R.raw.hops2));
            sHopsCache.addAll(reader.readAll(R.raw.hops3));
            sHopsCache.addAll(reader.readAll(R.raw.hops4));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sHopsCache;
    }


}
