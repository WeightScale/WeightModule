package com.konst.module;


import java.util.concurrent.TimeUnit;

/*
 * Created by Kostya on 02.05.2015.
 */
public class MeasureBatteryTemperature extends Thread /*implements InterfaceScaleModule*/ { //поток получения батареи
    final HandlerBatteryTemperature h;
    private boolean start;
    private boolean cancelled;
    private int autoNull; //счётчик автообнуления
    public static int timeUpdate = 1;               /* Время обновления в секундах*/

    MeasureBatteryTemperature(HandlerBatteryTemperature handler) {
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
            timeUpdate = h.handlerBatteryTemperature(ScaleModule.getModuleBatteryCharge(), ScaleModule.getModuleTemperature());
            try {
                TimeUnit.SECONDS.sleep(timeUpdate);
            } catch (InterruptedException ignored) {
                cancelled = true;
            }
            if (Versions.weight != Integer.MIN_VALUE && Math.abs(Versions.weight) < ScaleModule.weightError) { //автоноль
                autoNull += 1;
                if (autoNull > ScaleModule.timerNull / InterfaceScaleModule.DIVIDER_AUTO_NULL) {
                    ScaleModule.setOffsetScale();
                    autoNull = 0;
                }
            } else {
                autoNull = 0;
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
