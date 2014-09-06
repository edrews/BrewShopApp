package com.brew.brewshop.storage;

public interface ITimeoutHandler {
    public void onTimeout();
    public void onTimeoutMainThread();
}
