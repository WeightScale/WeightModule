package com.konst.module;

import java.util.Iterator;

/*
 * Created by Kostya on 30.04.2015.
 */
public class V4 extends Versions {

    @Override
    public boolean load() throws Error { //загрузить данные
        //======================================================================
        loadFilterADC();
        //======================================================================
        loadTimeOff();
        //======================================================================
        try {
            offset = Integer.valueOf(ScaleModule.getModuleOffsetSensor());
        } catch (Exception e) {
            throw new Error("Сделать обнуление в настройках");
        }
        //======================================================================
        spreadsheet = ScaleModule.cmd(InterfaceVersions.CMD_SPREADSHEET);
        username = ScaleModule.cmd(InterfaceVersions.CMD_G_USER);
        password = ScaleModule.cmd(InterfaceVersions.CMD_G_PASS);
        phone = ScaleModule.cmd(InterfaceVersions.CMD_PHONE);
        //======================================================================
        if (!isDataValid(ScaleModule.cmd(InterfaceVersions.CMD_DATA))) {
            return false;
        }
        weightMargin = (int) (weightMax * 1.2);
        marginTenzo = (int) ((weightMax / coefficientA) * 1.2);

        return true;
    }

    @Override
    public synchronized int updateWeight() {
        try {
            sensorTenzoOffset = Integer.valueOf(ScaleModule.cmd(InterfaceVersions.CMD_SENSOR_OFFSET));
            return weight = (int) (coefficientA * sensorTenzoOffset);
        } catch (Exception e) {
            return sensorTenzoOffset = weight = Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean isLimit() {
        return Math.abs(sensorTenzoOffset + offset) > limitTenzo;
    }

    @Override
    public synchronized boolean setOffsetScale() { //обнуление
        return ScaleModule.setModuleOffsetSensor();
    }

    @Override
    public boolean writeData() {
        return ScaleModule.cmd(InterfaceVersions.CMD_DATA +
                InterfaceVersions.CMD_DATA_CFA + '=' + coefficientA + ' ' +
                InterfaceVersions.CMD_DATA_WGM + '=' + weightMax + ' ' +
                InterfaceVersions.CMD_DATA_LMT + '=' + limitTenzo).equals(InterfaceVersions.CMD_DATA);
    }

    @Override
    public int getSensorTenzo() {
        return sensorTenzoOffset + offset;
    }

    @Override
    public boolean isMargin() {
        return Math.abs(sensorTenzoOffset + offset) > marginTenzo;
    }

    @Override
    public boolean setScaleNull() {
        return setOffsetScale();
    }

    public synchronized boolean isDataValid(String d) {
        String[] parts = d.split(" ", 0);
        SimpleCommandLineParser data = new SimpleCommandLineParser(parts, "=");
        Iterator<String> iteratorData = data.getKeyIterator();
        try {
            while (iteratorData.hasNext()) {
                switch (iteratorData.next()) {
                    case InterfaceVersions.CMD_DATA_CFA:
                        coefficientA = Float.valueOf(data.getValue(InterfaceVersions.CMD_DATA_CFA));//получаем коэфициент
                        if (coefficientA == 0)
                            return false;
                        break;
                    case InterfaceVersions.CMD_DATA_CFB:
                        coefficientB = Float.valueOf(data.getValue(InterfaceVersions.CMD_DATA_CFB));//получить offset
                        break;
                    case InterfaceVersions.CMD_DATA_WGM:
                        weightMax = Integer.parseInt(data.getValue(InterfaceVersions.CMD_DATA_WGM));//получаем макимальнай вес
                        if (weightMax <= 0)
                            return false;
                        break;
                    case InterfaceVersions.CMD_DATA_LMT:
                        limitTenzo = Integer.parseInt(data.getValue(InterfaceVersions.CMD_DATA_LMT));//получаем макимальнай показание перегруза
                        break;
                    default:
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean setSpreadsheet(String sheet) {
        return ScaleModule.cmd(InterfaceVersions.CMD_SPREADSHEET + sheet).equals(InterfaceVersions.CMD_SPREADSHEET);
    }

    @Override
    public boolean setUsername(String username) {
        return ScaleModule.cmd(InterfaceVersions.CMD_G_USER + username).equals(InterfaceVersions.CMD_G_USER);
    }

    @Override
    public boolean setPassword(String password) {
        return ScaleModule.cmd(InterfaceVersions.CMD_G_PASS + password).equals(InterfaceVersions.CMD_G_PASS);
    }

    @Override
    public boolean setPhone(String phone) {
        return ScaleModule.cmd(InterfaceVersions.CMD_PHONE + phone).equals(InterfaceVersions.CMD_PHONE);
    }


}