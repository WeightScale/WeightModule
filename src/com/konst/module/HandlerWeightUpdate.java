package com.konst.module;

import android.os.Handler;

/*
 * Created by Kostya on 06.05.2015.
 */
public abstract class HandlerWeightUpdate extends Handler {

    public enum Result {
        RESULT_WEIGHT_ERROR,
        RESULT_WEIGHT_NORMAL,
        RESULT_WEIGHT_LIMIT,
        RESULT_WEIGHT_MARGIN
    }

    public abstract int handlerWeight(Result what, int weight, int sensor);

}
