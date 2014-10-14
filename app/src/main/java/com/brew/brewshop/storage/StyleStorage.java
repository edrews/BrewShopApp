package com.brew.brewshop.storage;

import android.content.Context;

import com.brew.brewshop.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class StyleStorage {
    private static final String TAG = StyleStorage.class.getName();

    private static StyleInfoList sStyleCache;

    private Context mContext;

    public StyleStorage(Context context) {
        mContext = context;
    }

    public StyleInfoList getStyles() {
        if (sStyleCache != null) {
            return sStyleCache;
        }

        InputStream input = mContext.getResources().openRawResource(R.raw.styles);
        try {
            sStyleCache = readAllStyles(input);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sStyleCache;
    }

    private StyleInfoList readAllStyles(InputStream input) throws IOException, JSONException {
        BufferedReader r = new BufferedReader(new InputStreamReader(input));
        StringBuilder total = new StringBuilder(input.available());
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        JSONObject object = new JSONObject(total.toString());
        JSONArray styles = object.getJSONArray("data");
        Gson gson = new Gson();
        StyleInfoList list = gson.fromJson(styles.toString(), StyleInfoList.class);
        Collections.sort(list, new StyleInfoComparator());
        return list;
    }
}
