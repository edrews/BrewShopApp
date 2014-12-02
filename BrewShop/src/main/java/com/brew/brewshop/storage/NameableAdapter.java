package com.brew.brewshop.storage;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.Yeast;

import java.util.ArrayList;
import java.util.List;

public class NameableAdapter<T> extends BaseAdapter {
    private static final String TAG = NameableAdapter.class.getName();
    private String mCustomName;
    private List<T> mList;
    private Context mContext;

    public NameableAdapter(Context context, List<T> nameables) {
        this(context, nameables, null);
    }

    public NameableAdapter(Context context, List<T> nameables, String customName) {
        mCustomName = customName;
        mContext = context;
        mList = new ArrayList<T>();
        if (mCustomName != null) {
            mList.add((T) new CustomNameable(customName));
        }
        for (T nameable : nameables) {
            mList.add(nameable);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView rowView = (TextView) inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        Nameable item = (Nameable) getItem(position);
        rowView.setText(item.getName());
        modifyView(rowView);
        return rowView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView rowView = (TextView) inflater.inflate(R.layout.spinner_item, parent, false);
        Nameable item = (Nameable) getItem(position);
        rowView.setText(item.getName());
        modifyView(rowView);
        return rowView;
    }

    public void setNamedItem(Nameable selected, Nameable stored, String customName) {
        if (selected.getName().equals(mCustomName)) {
            if (customName.length() > 0) {
                stored.setName(customName);
            } else {
                stored.setName(mCustomName);
            }
        } else {
            stored.setName(selected.getName());
        }
    }

    private void modifyView(View view) {
        TextView textView = (TextView) view;
        if (textView.getText().equals(mCustomName)) {
            textView.setTypeface(null, Typeface.ITALIC);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }
}
