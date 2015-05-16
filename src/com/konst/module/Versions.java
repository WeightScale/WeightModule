package com.konst.module;

/*
 * Created by Kostya on 29.03.2015.
 */
public abstract class Versions {

    public static int timeOff;                      //таймер выключения весов
    protected int sensor;                      //показание датчика веса
    public static float coefficientA;              //калибровочный коэффициент a
    public static float coefficientB;              //калибровочный коэффициент b
    public static int weightMax;                   //максимальный вес
    public static int filterADC;                   //АЦП-фильтр (0-15)
    public static int weight;                      //реальный вес
    public static int weightMargin;                //предельный вес
    public static int limitTenzo;                   // максимальное показание датчика
    static int marginTenzo;                    // предельное показани датчика
    protected int speed;                       //скорость передачи данных
    public static int sensorTenzo;                 // показание датчика веса


    protected static int offset;                      // offset
    protected float coefficientTemp;           // калибровочный коэффициент температуры
    protected static int sensorTenzoOffset;           // показание датчика веса минус offset

    public static String spreadsheet = "";
    public static String username = "";
    public static String password = "";
    public static String phone = "";

    public static String getFilterADC() {
        return ScaleModule.cmd(InterfaceScaleModule.CMD_FILTER);
    }

    public abstract boolean load() throws Error;

    public abstract boolean setOffsetScale();

    public abstract boolean isLimit();

    public abstract boolean isMargin();

    public abstract int updateWeight();

    public abstract boolean setScaleNull();

    public abstract boolean writeData();

    public abstract int getSensorTenzo();

    public abstract boolean setSpreadsheet(String sheet);

    public abstract boolean setUsername(String username);

    public abstract boolean setPassword(String password);

    public abstract boolean setPhone(String phone);
    //public abstract boolean restorePreferences();

    public void loadFilterADC() throws Error {
        filterADC = Integer.valueOf(getFilterADC());
        if (filterADC < 0 || filterADC > InterfaceVersions.MAX_ADC_FILTER) {
            if (!ScaleModule.setModuleFilterADC(InterfaceVersions.DEFAULT_ADC_FILTER))
                throw new Error("Фильтер АЦП не установлен в настройках");
            filterADC = InterfaceVersions.DEFAULT_ADC_FILTER;
        }
    }

    public void loadTimeOff() throws Error {
        timeOff = Integer.valueOf(ScaleModule.getModuleTimeOff());
        if (timeOff < InterfaceVersions.MIN_TIME_OFF || timeOff > InterfaceVersions.MAX_TIME_OFF) {
            if (!ScaleModule.setModuleTimeOff(InterfaceVersions.MIN_TIME_OFF))
                throw new Error("Таймер выключения не установлен в настройках");
            timeOff = InterfaceVersions.MIN_TIME_OFF;
        }
    }

    public void loadSpeedModule() throws Error {
        speed = Integer.valueOf(ScaleModule.getModuleSpeedPort());
        if (speed < 1 || speed > 5) {
            if (!ScaleModule.setModuleSpeedPort(5))
                throw new Error("Скорость передачи не установлена в настройках");
            speed = 5;
        }
    }

    public int getLimitTenzo() {
        return limitTenzo;
    }

    public static int getMarginTenzo() {
        return marginTenzo;
    }
}
