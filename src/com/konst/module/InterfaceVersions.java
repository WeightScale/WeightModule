package com.konst.module;

/*
 * Created by Kostya on 02.05.2015.
 */
public interface InterfaceVersions {

    int MAX_ADC_FILTER = 15;                //максимальное значение фильтра ацп
    int DEFAULT_ADC_FILTER = 8;             //максимальное значение фильтра ацп
    int MAX_TIME_OFF = 60;                  //максимальное время бездействия весов в минутах
    int MIN_TIME_OFF = 10;                  //минимальное время бездействия весов в минутах

    String CMD_DATA = "DAT";                //считать/записать данные весов
    String CMD_SENSOR = "DCH";              //получить показание датчика веса
    String CMD_SPREADSHEET = "SGD";         //считать/записать имя таблици созданой в google disc
    String CMD_G_USER = "UGD";              //считать/записать account google disc
    String CMD_G_PASS = "PGD";              //считать/записать password google disc
    String CMD_PHONE = "PHN";               //считать/записать phone for sms boss
    String CMD_SENSOR_OFFSET = "DCO";       //получить показание датчика веса минус офсет

    String CMD_DATA_CFA = "cfa";            //коэфициэнт А
    String CMD_DATA_CFB = "cfb";            //коэфициэнт Б
    String CMD_DATA_WGM = "wgm";            //вес максимальный
    String CMD_DATA_LMT = "lmt";            //лимит тензодатчика


    //int getLimitTenzo();
    //int getMarginTenzo();

    //boolean load() throws Exception;
    //boolean setOffsetScale();
    //boolean isLimit();
    //boolean isMargin();
    //int updateWeight();
    //boolean setScaleNull();
    //boolean writeData();

}
