package com.brew.brewshop.storage.models;

import android.graphics.Bitmap;

import com.brew.brewshop.storage.ProductType;

public class Product {
    private String mName;
    private String mDescription;
    private String mManufacturer;
    private ProductType mProductType;
    private Bitmap mIcon;
    private boolean mInStock;

    private double mPrice;
    private String mPriceUnit;

    public String getName() { return mName; }
    public void setName(String value) { mName = value; }

    public String getDescription() { return mDescription; }
    public void setDescription(String value) { mDescription = value; }

    public String getManufacturer() { return mManufacturer; }
    public void setManufacturer(String value) { mManufacturer = value; }

    public ProductType getProductType() { return mProductType; }
    public void setProductType(ProductType value) { mProductType = value; }

    public Bitmap getIcon() { return mIcon; }
    public void setIcon(Bitmap value) { mIcon = value; }

    public boolean isInStock() { return mInStock; }
    public void setInStock(boolean value) { mInStock = value; }

    public double getPrice() { return mPrice; }
    public void setPrice(double value) { mPrice = value; }
    public String getPriceString() {
        return String.format("$%.2f", mPrice);
    }

    public String getPriceUnit() { return mPriceUnit; }
    public void setPriceUnit(String value) { mPriceUnit = value; }
}
