package com.brew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BeerStyleAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mStyles;

    public BeerStyleAdapter(Context context, List<String> styles) {
        super(context, R.layout.list_item_beer_style, styles);
        mContext = context;
        mStyles = styles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_beer_style, parent, false);

        TextView nameView = (TextView) rowView.findViewById(R.id.name);

        String style = mStyles.get(position);
        nameView.setText(style);
        return rowView;
    }
}
