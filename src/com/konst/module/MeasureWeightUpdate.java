package com.konst.module;


/*
 * Created by Kostya on 02.05.2015.
 */
public class MeasureWeightUpdate extends Thread /*implements InterfaceScaleModule*/ { //поток получения батареи
    final HandlerWeightUpdate h;
    private boolean start;
    private boolean cancelled;
    public static int timeUpdate = 50;

    MeasureWeightUpdate(HandlerWeightUpdate handler) {
        h = handler;
    }

    @Override
    public synchronized void start() {
        //setPriority(Thread.MIN_PRIORITY);
        super.start();
        start = true;
    }

    @Override
    public void run() {
        while (!cancelled) {
            ScaleModule.updateWeight();
            HandlerWeightUpdate.Result msg;
            if (Versions.weight == Integer.MIN_VALUE) {
                msg = HandlerWeightUpdate.Result.RESULT_WEIGHT_ERROR;
            } else {
                if (ScaleModule.isLimit())
                    msg = ScaleModule.isMargin() ? HandlerWeightUpdate.Result.RESULT_WEIGHT_MARGIN : HandlerWeightUpdate.Result.RESULT_WEIGHT_LIMIT;
                else {
                    msg = HandlerWeightUpdate.Result.RESULT_WEIGHT_NORMAL;
                }
            }
            timeUpdate = h.handlerWeight(msg, Versions.weight, ScaleModule.getSensorTenzo());
            try {
                Thread.sleep(timeUpdate);
            } catch (InterruptedException ignored) {
                cancelled = true;
            }
        }
        start = false;
    }

    private void cancel() {
        cancelled = true;
    }

    public void execute(boolean exe) {
        if (exe) {
            if (!start)
                start();
        } else
            cancel();
    }

    public boolean isStart() {
        return start;
    }
}
