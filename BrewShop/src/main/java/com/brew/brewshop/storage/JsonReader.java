package com.brew.brewshop.storage;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class JsonReader {
    private static final String DEFAULT_ARRAY = "data";
    private Context mContext;
    private Class mClass;

    public JsonReader(Context context, Class clazz) {
        mContext = context;
        mClass = clazz;
    }

    public <T extends NameableList> T readAll(int resource) throws IOException, JSONException {
        return readAll(resource, DEFAULT_ARRAY);
    }

    public <T extends NameableList> T readAll(int resource, String arrayName) throws IOException, JSONException {
        InputStream input = mContext.getResources().openRawResource(resource);
        BufferedReader r = new BufferedReader(new InputStreamReader(input));
        StringBuilder total = new StringBuilder(input.available());
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        JSONObject object = new JSONObject(total.toString());
        JSONArray array = object.getJSONArray(arrayName);
        Gson gson = new Gson();

        @SuppressWarnings("unchecked")
        T list = (T) gson.fromJson(array.toString(), mClass);

        Collections.sort(list, new NameComparator());
        return list;
    }
}
