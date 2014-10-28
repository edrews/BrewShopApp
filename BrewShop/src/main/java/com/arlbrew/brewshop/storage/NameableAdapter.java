package com.arlbrew.brewshop.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NameableAdapter<T extends Nameable> extends ArrayAdapter<T> {
    private Context mContext;
    private List<T> mNameables;

    public NameableAdapter(Context context, List<T> styles) {
        super(context, android.R.layout.simple_spinner_item, styles);
        mContext = context;
        mNameables = styles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView rowView = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        rowView.setText(mNameables.get(position).getName());
        return rowView;
    }
}
