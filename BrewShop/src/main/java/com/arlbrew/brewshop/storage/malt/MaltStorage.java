package com.arlbrew.brewshop.storage.malt;

import android.content.Context;

import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.storage.JsonReader;

import org.json.JSONException;

import java.io.IOException;

public class MaltStorage {
    private static MaltInfoList sMaltCache;

    private Context mContext;

    public MaltStorage(Context context) {
        mContext = context;
    }

    @SuppressWarnings("unchecked")
    public MaltInfoList getMalts() {
        if (sMaltCache != null) {
            return sMaltCache;
        }
        try {
            sMaltCache = new MaltInfoList();
            JsonReader reader = new JsonReader(mContext, MaltInfoList.class);
            sMaltCache.addAll(reader.readAll(R.raw.fermentables1));
            sMaltCache.addAll(reader.readAll(R.raw.fermentables2));
            sMaltCache.addAll(reader.readAll(R.raw.fermentables3));
            sMaltCache.addAll(reader.readAll(R.raw.fermentables4));
            sMaltCache.addAll(reader.readAll(R.raw.fermentables5));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sMaltCache;
    }


}
