package com.brew.brewshop.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.models.Product;

import java.util.List;


public class ProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    private List<Product> mProducts;

    public ProductListAdapter(Context context, List<Product> products) {
        super(context, R.layout.list_item_product, products);
        mContext = context;
        mProducts = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_product, parent, false);

        ImageView iconView = (ImageView) rowView.findViewById(R.id.product_icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.product_name);
        TextView manufacturerView = (TextView) rowView.findViewById(R.id.product_manufacturer);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.product_description);
        TextView priceView = (TextView) rowView.findViewById(R.id.product_price);
        TextView priceUnitView = (TextView) rowView.findViewById(R.id.product_price_unit);

        Product product = mProducts.get(position);
        iconView.setImageBitmap(product.getIcon());
        nameView.setText(product.getName());
        manufacturerView.setText(product.getManufacturer());
        descriptionView.setText(product.getDescription());
        priceView.setText(product.getPriceString());
        priceUnitView.setText(product.getPriceUnit());
        return rowView;
    }
}
