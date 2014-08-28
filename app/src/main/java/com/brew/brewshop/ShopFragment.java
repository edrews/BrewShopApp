package com.brew.brewshop;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brew.brewshop.storage.ProductListAdapter;
import com.brew.brewshop.storage.ProductStorage;
import com.brew.brewshop.storage.ProductType;

public class ShopFragment extends ListFragment {
    private static final String TAG = ShopFragment.class.getName();
    public static String PRODUCT_TYPE = "ProductType";

    private ProductStorage mProductStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mProductStorage = new ProductStorage(this);

        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);

        String typeString = getArguments().getString(PRODUCT_TYPE);
        ProductType type = ProductType.valueOf(typeString);

        Context context = getActivity();
        ProductListAdapter adapter = new ProductListAdapter(context, mProductStorage.getProducts(type));
        setListAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopping, menu);
    }
}
