package com.brew.brewshop.storage;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductStorage {
    private Fragment mContext;

    public ProductStorage(Fragment context) {
        mContext = context;
    }

    public List<Product> getProducts(ProductType type) {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo);

        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < 20; i++) {
            Product product = new Product();
            product.setProductType(type);

            String name = capitalize(type.toString().toLowerCase());
            product.setName(name + " " + (i+1));

            product.setIcon(icon);
            product.setManufacturer("Manufacturer");
            product.setDescription("Description goes here. Description goes here. Description goes here. Description goes here. Description goes here. ");
            product.setPrice(15);
            product.setPriceUnit("12 oz.");
            products.add(product);
        }
        return products;
    }

    private String capitalize(String line)
    {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
