package com.brew.brewshop.storage;

import android.content.Context;
import android.util.Log;

import com.brew.brewshop.R;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StyleStorage {
    private static final String TAG = StyleStorage.class.getName();
    private Context mContext;

    public StyleStorage(Context context) {
        mContext = context;
    }

    public StyleInfoList getStyles() {
        StyleInfoList list = null;
        InputStream input = mContext.getResources().openRawResource(R.raw.styles);
        try {
            list = readAllStyles(input);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
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
        StyleInfoList list = new StyleInfoList();
        for (int i = 0; i < styles.length(); i++) {
            JSONObject style = styles.getJSONObject(i);
            int id = style.getInt("id");
            String name = style.getString("name");
            String description = style.optString("description", "");
            StyleInfo info = new StyleInfo(id, name, description);
            list.add(info);
        }
        Collections.sort(list, new StyleInfoComparator());
        return list;
    }
}
