package com.arlbrew.brewshop.storage.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.arlbrew.brewshop.storage.ProductType;

import java.util.Locale;

@SuppressWarnings("unused")
public class Product implements Parcelable {
    private String mName;
    private String mDescription;
    private String mManufacturer;
    private ProductType mProductType;
    private Bitmap mIcon;

    private double mPrice;
    private String mPriceUnit;

    public Product() {
        mName = "";
        mDescription = "";
        mManufacturer = "";
        mProductType = ProductType.NONE;
    }

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

    public double getPrice() { return mPrice; }
    public void setPrice(double value) { mPrice = value; }
    public String getPriceString() {
        return String.format(Locale.US, "$%.2f", mPrice);
    }

    public String getPriceUnit() { return mPriceUnit; }
    public void setPriceUnit(String value) { mPriceUnit = value; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mDescription);
        out.writeString(mManufacturer);
        out.writeString(mProductType.toString());
        out.writeParcelable(mIcon, 0);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        mName = in.readString();
        mDescription = in.readString();
        mManufacturer = in.readString();
        mProductType = ProductType.valueOf(in.readString());
        mIcon = in.readParcelable(Bitmap.class.getClassLoader());
    }
}
