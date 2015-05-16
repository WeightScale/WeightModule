package com.konst.module;

/*
 * Created by Kostya on 30.04.2015.
 */
public class V1 extends Versions {

    @Override
    public boolean load() throws Error { //загрузить данные
        loadFilterADC();
        //==============================================================================================================
        loadTimeOff();
        //==============================================================================================================
        if (!isDataValid(ScaleModule.cmd(InterfaceVersions.CMD_DATA)))
            return false;
        weightMargin = (int) (weightMax * 1.2);
        return true;
    }

    @Override
    public synchronized int updateWeight() {
        try {
            sensorTenzo = Integer.valueOf(ScaleModule.cmd(InterfaceVersions.CMD_SENSOR));
            return weight = (int) (coefficientA * sensorTenzo + coefficientB);
        } catch (Exception e) {
            return sensorTenzo = weight = Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean isLimit() {
        return Math.abs(weight) > weightMax;
    }

    @Override
    public synchronized boolean setOffsetScale() { //обнуление
        try {
            coefficientB = -coefficientA * Integer.parseInt(ScaleModule.cmd(InterfaceVersions.CMD_SENSOR));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean writeData() {
        return ScaleModule.cmd(InterfaceVersions.CMD_DATA + 'S' + coefficientA + ' ' + coefficientB + ' ' + weightMax).equals(InterfaceVersions.CMD_DATA);
    }

    @Override
    public int getSensorTenzo() {
        return sensorTenzoOffset;
    }

    @Override
    public boolean setSpreadsheet(String sheet) {
        return false;
    }

    @Override
    public boolean setUsername(String username) {
        return false;
    }

    @Override
    public boolean setPassword(String password) {
        return false;
    }

    @Override
    public boolean setPhone(String phone) {
        return false;
    }

    /*@Override
    public boolean restorePreferences() {
        return false;
    }*/

    @Override
    public boolean isMargin() {
        return Math.abs(weight) < weightMargin;
    }

    @Override
    public boolean setScaleNull() {
        String str = ScaleModule.cmd(InterfaceVersions.CMD_SENSOR);
        if (str.isEmpty()) {
            return false;
        }

        if (setOffsetScale()) {
            if (writeData()) {
                sensorTenzo = Integer.valueOf(str);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isDataValid(String d) {
        StringBuilder dataBuffer = new StringBuilder(d);
        try {
            dataBuffer.deleteCharAt(0);
            String str = dataBuffer.substring(0, dataBuffer.indexOf(" "));
            coefficientA = Float.valueOf(str);
            dataBuffer.delete(0, dataBuffer.indexOf(" ") + 1);
            str = dataBuffer.substring(0, dataBuffer.indexOf(" "));
            coefficientB = Float.valueOf(str);
            dataBuffer.delete(0, dataBuffer.indexOf(" ") + 1);
            weightMax = Integer.valueOf(dataBuffer.toString());
            if (weightMax <= 0) {
                weightMax = 1000;
                //writeData();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*@Override
    public boolean backupPreference() {
        //SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_UPDATE, Context.MODE_PRIVATE).edit();

            *//*Main.preferencesUpdate.write(CMD_FILTER, String.valueOf(filter));
            Main.preferencesUpdate.write(CMD_TIMER, String.valueOf(timer));
            Main.preferencesUpdate.write(CMD_BATTERY, String.valueOf(battery));
            Main.preferencesUpdate.write(CMD_DATA_CFA, String.valueOf(coefficientA));
            Main.preferencesUpdate.write(CMD_DATA_CFB, String.valueOf(coefficientB));
            Main.preferencesUpdate.write(CMD_DATA_WGM, String.valueOf(weightMax));*//*

        //editor.apply();
        return true;
    }*/

}