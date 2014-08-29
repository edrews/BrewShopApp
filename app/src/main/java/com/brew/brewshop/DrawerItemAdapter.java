package com.brew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.storage.models.Product;

import java.util.List;


public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {
    private Context mContext;
    private List<DrawerItem> mItems;

    public DrawerItemAdapter(Context context, List<DrawerItem> item) {
        super(context, R.layout.list_item_drawer, item);
        mContext = context;
        mItems = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_drawer, parent, false);

        ImageView iconView = (ImageView) rowView.findViewById(R.id.icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);

        DrawerItem product = mItems.get(position);
        iconView.setImageBitmap(product.getIcon());
        nameView.setText(product.getName());
        return rowView;
    }
}
