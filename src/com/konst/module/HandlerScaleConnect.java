package com.konst.module;

import android.os.Handler;

/*
 * Created by Kostya on 06.05.2015.
 */
public abstract class HandlerScaleConnect extends Handler {

    public enum Result {
        STATUS_LOAD_OK,
        STATUS_CONNECT_ERROR,
        STATUS_SETTINGS_UNCORRECTED,
        STATUS_SCALE_UNKNOWN,
        STATUS_TERMINAL_ERROR,
        STATUS_ATTACH_FINISH,
        STATUS_ATTACH_START
    }

    public abstract void handleModuleConnect(Result what);

    //public void handleConnectFinish(){};

    public abstract void handleModuleConnectError(Result what, String error);

}
