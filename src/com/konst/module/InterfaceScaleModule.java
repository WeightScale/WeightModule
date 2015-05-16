package com.konst.module;

/*
 * Created by Kostya on 02.05.2015.
 */
public interface InterfaceScaleModule {


    int DIVIDER_AUTO_NULL = 3;                          //делитель для авто ноль

    String CMD_VERSION = "VRS";                         //получить версию весов
    String CMD_FILTER = "FAD";                          //получить/установить АЦП-фильтр
    String CMD_TIMER = "TOF";                           //получить/установить таймер выключения весов
    String CMD_SPEED = "BST";                           //получить/установить скорость передачи данных
    String CMD_GET_OFFSET = "GCO";
    String CMD_SET_OFFSET = "SCO";                      //установить offset
    String CMD_BATTERY = "GBT";                         //получить передать заряд батареи
    String CMD_DATA_TEMP = "DTM";                       //считать/записать данные температуры
    String CMD_HARDWARE = "HRW";                        //получить версию hardware
    String CMD_NAME = "SNA";                            //установить имя весов
    String CMD_CALL_BATTERY = "CBT";                    //каллибровать процент батареи

    //String getFilterADC();
    //boolean setFilterADC(int filterADC);
    //String getTimeScaleOff();
    //boolean setTimeScaleOff(int timeOff);
    //String getSpeedModule();
    //boolean setSpeedModule(int speed);
    //String getOffsetSensor();
    //boolean setOffsetSensor();
    //String getSensor();
    //int getBatteryCharge();
    //int getTemperatureModule();

    //String getName();
    //String getAddress();
    //int getNumVersion();

    //String getHardware();

    //int getMarginTenzo();
    //boolean setNameModule(String name);
    //boolean doCalibrateBattery(int percent);


}
