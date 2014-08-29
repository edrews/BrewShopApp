package com.brew.brewshop;

import android.graphics.Bitmap;

public class DrawerItem {
    private Bitmap mIcon;
    private String mName;

    public DrawerItem(Bitmap icon, String name) {
        mIcon = icon;
        mName = name;
    }

    public void setIcon(Bitmap value) { mIcon = value; }
    public Bitmap getIcon() { return mIcon; }

    public String getName() { return mName; }
    public void setName(String value) { mName = value; }
}
