package com.konst.module;

import java.io.IOException;

/*
 * Created by Kostya on 30.04.2015.
 */
public class ConnectScaleThread extends Thread {
    final ScaleModule mHandler;

    ConnectScaleThread(ScaleModule handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            ScaleModule.connect();
            if (ScaleModule.isScales()) {
                try {
                    if (ScaleModule.load()) {
                        //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_LOAD_OK.ordinal()).sendToTarget();
                        mHandler.handleModuleConnect(HandlerScaleConnect.Result.STATUS_LOAD_OK);
                    } else {
                        //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_SETTINGS_UNCORRECTED.ordinal()).sendToTarget();
                        mHandler.handleModuleConnect(HandlerScaleConnect.Result.STATUS_SETTINGS_UNCORRECTED);
                    }
                } catch (Error e) {
                    //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_TERMINAL_ERROR.ordinal(), e.getMessage()).sendToTarget();
                    mHandler.handleModuleConnectError(HandlerScaleConnect.Result.STATUS_TERMINAL_ERROR, e.getMessage());
                }
            } else {
                ScaleModule.disconnect();
                //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_SCALE_UNKNOWN.ordinal()).sendToTarget();
                mHandler.handleModuleConnect(HandlerScaleConnect.Result.STATUS_SCALE_UNKNOWN);
                //throw new RuntimeException(new IllegalArgumentException("Версия весов не определена"));
            }
        } catch (IOException e) {
            //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_CONNECT_ERROR.ordinal(), e.getMessage()).sendToTarget();
            mHandler.handleModuleConnectError(HandlerScaleConnect.Result.STATUS_CONNECT_ERROR, e.getMessage());
            //throw new RuntimeException(e);
        }
        mHandler.handleModuleConnect(HandlerScaleConnect.Result.STATUS_ATTACH_FINISH);
        //mHandler.handleConnectFinish();
        //mHandler.obtainMessage(HandlerScaleConnect.Result.STATUS_ATTACH_FINISH.ordinal()).sendToTarget();
        //mHandler.sendEmptyMessage(0);

    }

}
