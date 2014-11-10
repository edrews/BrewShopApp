package com.brew.brewshop.storage;

import android.os.AsyncTask;

public class TimeoutTask extends AsyncTask<Integer, Void, Void> {
    private ITimeoutHandler mHandler;

    public TimeoutTask(ITimeoutHandler handler) {
        mHandler = handler;
    }

    @Override
    protected Void doInBackground(Integer[] args) {
        int millis = args[0];
        try {
            Thread.sleep(millis, 0);
            mHandler.onTimeout();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mHandler.onTimeoutMainThread();
    }
}
