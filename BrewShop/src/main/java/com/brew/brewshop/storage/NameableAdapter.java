package com.brew.brewshop.storage;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brew.brewshop.R;

import java.util.List;

public class NameableAdapter<T> extends ArrayAdapter<T> {
    private String mCustomName;
    private Context mContext;

    public NameableAdapter(Context context, List<T> nameables) {
        this(context, nameables, null);
    }

    public NameableAdapter(Context context, List<T> nameables, String customName) {
        super(context, R.layout.spinner_item);
        setDropDownViewResource(R.layout.spinner_dropdown_item);
        mCustomName = customName;
        mContext = context;
        if (mCustomName != null) {
            add((T) new CustomNameable(customName));
        }
        for (T nameable : nameables) {
            add(nameable);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View rowView = super.getDropDownView(position, convertView, parent);
        modifyView(rowView);
        return rowView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = super.getView(position, convertView, parent);
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

        //Fixes a style bug with HTC Rezound
        //textView.setTextColor(mContext.getResources().getColor(R.color.text_dark_primary));

        if (textView.getText().equals(mCustomName)) {
            textView.setTypeface(null, Typeface.ITALIC);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }
}
