package com.brew.brewshop.storage.malt;

import android.content.Context;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.NameComparator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class MaltStorage {
    private static final String TAG = MaltStorage.class.getName();

    private static MaltInfoList sMaltCache;

    private Context mContext;

    public MaltStorage(Context context) {
        mContext = context;
    }

    public MaltInfoList getMalts() {
        if (sMaltCache != null) {
            return sMaltCache;
        }
        try {
            sMaltCache = new MaltInfoList();
            sMaltCache.addAll(readAll(R.raw.fermentables1));
            sMaltCache.addAll(readAll(R.raw.fermentables2));
            sMaltCache.addAll(readAll(R.raw.fermentables3));
            sMaltCache.addAll(readAll(R.raw.fermentables4));
            sMaltCache.addAll(readAll(R.raw.fermentables5));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sMaltCache;
    }

    private MaltInfoList readAll(int resource) throws IOException, JSONException {
        InputStream input = mContext.getResources().openRawResource(resource);
        BufferedReader r = new BufferedReader(new InputStreamReader(input));
        StringBuilder total = new StringBuilder(input.available());
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        JSONObject object = new JSONObject(total.toString());
        JSONArray malts = object.getJSONArray("data");
        Gson gson = new Gson();
        MaltInfoList list = gson.fromJson(malts.toString(), MaltInfoList.class);
        Collections.sort(list, new NameComparator());
        return list;
    }
}
