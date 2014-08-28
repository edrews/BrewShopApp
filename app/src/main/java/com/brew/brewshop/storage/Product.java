package com.brew.brewshop.storage;

import android.graphics.Bitmap;

public class Product {
    private String mName;
    private ProductType mProductType;
    private Bitmap mIcon;

    public String getName() { return mName; }
    public void setName(String value) { mName = value; }

    public ProductType getProductType() { return mProductType; }
    public void setProductType(ProductType value) { mProductType = value; }

    public Bitmap getIcon() { return mIcon; }
    public void setIcon(Bitmap value) { mIcon = value; }
}
