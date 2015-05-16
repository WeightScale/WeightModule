package com.konst.module;

import android.os.Handler;

/*
 * Created by Kostya on 06.05.2015.
 */
public abstract class HandlerBatteryTemperature extends Handler {

    public abstract int handlerBatteryTemperature(int battery, int temperature);

    /*@Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }*/
}
