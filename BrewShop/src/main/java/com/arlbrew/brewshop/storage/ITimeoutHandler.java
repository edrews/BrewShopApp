package com.arlbrew.brewshop.storage;

public interface ITimeoutHandler {
    public void onTimeout();
    public void onTimeoutMainThread();
}
