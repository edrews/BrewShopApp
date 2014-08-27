package com.brew.brewshop;

public class Recipe {
    private String mName;
    private String mStyle;
    private double mGravity;
    private int mSrm;
    private int mIbu;

    public Recipe() {
        mName = "Name";
        mStyle = "Style";
        mGravity = 1.050;
        mSrm = 10;
        mIbu = 20;
    }

    public String getName() { return mName; }
    public void setName(String value) { mName = value; }

    public String getStyle() { return mStyle; }
    public void setStyle(String value) { mStyle = value; }

    public double getGravity() { return mGravity; }
    public void setGravity(double value) { mGravity = value; }

    public int getSrm() { return mSrm; }
    public void setSrm(int value) { mSrm = value; }

    public int getIbu() { return mIbu; }
    public void setIbu(int value) { mIbu = value; }
}
