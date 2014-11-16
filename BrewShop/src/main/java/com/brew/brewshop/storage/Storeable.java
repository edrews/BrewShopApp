package com.brew.brewshop.storage;

import android.os.Parcelable;

public interface Storeable extends Parcelable {
    public int getId();
    public void setId(int id);
}
