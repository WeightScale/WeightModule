package com.konst.module;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.io.*;
import java.util.UUID;

/*
 * Created by Kostya on 30.04.2015.
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public abstract class ScaleModule extends HandlerScaleConnect {

    private static BluetoothDevice device;                          //чужое устройство
    //private static HandlerScaleMessage handler;
    private static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothSocket socket;                          //соединение
    private static OutputStream os;                                 //поток отправки
    private static InputStream is;                                  //поток получения
    public static Versions Version;                                 //Интерфейс для разных версий весов
    private static MeasureBatteryTemperature measureBatteryTemperature;
    private static MeasureWeightUpdate measureWeightUpdate;
    //static ConnectedThread connectedThread;

    static final int TIMEOUT_GET_BYTE = 2000;                       //время задержки для получения байта

    private static String version;
    public static int numVersion;

    public static int battery;                                      //процент батареи (0-100%)
    public static int weightError;                                  //погрешность веса
    public static int timerNull;                                    // время срабатывания авто нуля

    public void init(String moduleVersion, BluetoothDevice btDevice/*, HandlerScaleMessage msg*/) /*throws Throwable*/ {
        version = moduleVersion;
        device = btDevice;
        //handler = msg;
        attach();
    }

    public void init(String moduleVersion, String btDevice/*, HandlerScaleMessage msg*/) /*throws Throwable*/ {
        version = moduleVersion;
        device = bluetoothAdapter.getRemoteDevice(btDevice);
        //handler = msg;
        attach();
    }

    public void initBoot(String moduleVersion, String btDevice) /*throws Throwable*/ {
        version = moduleVersion;
        device = bluetoothAdapter.getRemoteDevice(btDevice);
        attachBoot();
    }

    protected static synchronized void connect() throws IOException { //соединиться с весами
        disconnect();
        // Get a BluetoothSocket for a connection with the given BluetoothDevice
        socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        bluetoothAdapter.cancelDiscovery();
        socket.connect();
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    protected static void disconnect() { //рассоединиться
        try {
            if (socket != null)
                socket.close();
            if (is != null)
                is.close();
            if (os != null)
                os.close();
        } catch (IOException ioe) {
            socket = null;
            //return;
        }
        is = null;
        os = null;
        socket = null;
    }

    protected static synchronized String cmd(String cmd) { //послать команду и получить ответ
        //synchronized (ScaleModule.class){
        try {
            synchronized (ScaleModule.class) {
                int t = is.available();
                if (t > 0) {
                    is.read(new byte[t]);
                }

                sendCommand(cmd);
                StringBuilder response = new StringBuilder();

                for (int i = 0; i < 400 && response.length() < 129; ++i) {
                    Thread.sleep(1L);
                    if (is.available() > 0) {
                        i = 0;
                        char ch = (char) is.read();
                        if (ch == '\uffff') {
                            connect();
                            break;
                        }
                        if (ch == '\r')
                            continue;
                        if (ch == '\n')
                            if (response.toString().startsWith(cmd.substring(0, 3)))
                                return response.replace(0, 3, "").toString().isEmpty() ? cmd.substring(0, 3) : response.toString();
                            else
                                return "";

                        response.append(ch);
                    }
                }
            }

        } catch (IOException | InterruptedException ioe) {
        }

        try {
            connect();
        } catch (IOException e) {
        }
        return "";
        //}
    }

    private static synchronized void sendCommand(String cmd) throws IOException {
        os.write(cmd.getBytes());
        os.write((byte) 0x0D);
        os.write((byte) 0x0A);
        os.flush(); //что этот метод делает?
    }

    public static synchronized boolean sendByte(byte ch) {
        try {
            int t = is.available();
            if (t > 0) {
                is.read(new byte[t]);
            }
            os.write(ch);
            os.flush(); //что этот метод делает?
            return true;
        } catch (IOException ioe) {
        }
        try {
            connect();
        } catch (IOException e) {
        }
        return false;
    }

    public static synchronized int getByte() {

        try {
            for (int i = 0; i < TIMEOUT_GET_BYTE; i++) {
                if (is.available() > 0) {
                    return is.read(); //временный символ (байт)
                }
                Thread.sleep(1);
            }
            return 0;
        } catch (IOException | InterruptedException ioe) {
        }

        try {
            connect();
        } catch (IOException e) {
        }
        return 0;
    }

    private void attachBoot() /*throws Throwable*/ {
        //final Throwable[] initException = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                    //obtainMessage(Result.STATUS_LOAD_OK.ordinal()).sendToTarget();
                    handleModuleConnect(Result.STATUS_LOAD_OK);
                } catch (IOException e) {
                    //obtainMessage(Result.STATUS_CONNECT_ERROR.ordinal(), e.getMessage()).sendToTarget();
                    handleModuleConnectError(Result.STATUS_CONNECT_ERROR, e.getMessage());
                    //throw new RuntimeException(e);
                }
                //obtainMessage(Result.STATUS_ATTACH_FINISH.ordinal()).sendToTarget();
                handleModuleConnect(Result.STATUS_ATTACH_FINISH);
                //handleConnectFinish();
            }
        });
       /* t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                initException[0] = ex.getCause();
            }
        });*/
        handleModuleConnect(Result.STATUS_ATTACH_START);
        //obtainMessage(Result.STATUS_ATTACH_START.ordinal(), device.getName()).sendToTarget();
        t.start();
        //t.join();
        //if (initException[0] != null)
        //    throw initException[0];
    }

    private void attach() /*throws Throwable*/ {
        //final Throwable[] initException = {null};
        Thread t = new ConnectScaleThread(this);
        /*t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                //initException[0] = ex.getCause();
                handleModuleConnectError(HandlerScaleConnect.Result.STATUS_CONNECT_ERROR, ex.getCause().getMessage());
                handleConnectFinish();
            }
        });*/
        handleModuleConnect(Result.STATUS_ATTACH_START);
        //obtainMessage(Result.STATUS_ATTACH_START.ordinal(), device.getName()).sendToTarget();
        t.start();
        //t.join();
        //if (initException[0] != null)
        //    throw initException[0];

    }

    public void dettach() {
        if (isAttach()) {
            try {
                if (measureBatteryTemperature != null) {
                    measureBatteryTemperature.execute(false);
                    while (measureBatteryTemperature.isStart()) ;
                }
                if (measureWeightUpdate != null) {
                    measureWeightUpdate.execute(false);
                    while (measureWeightUpdate.isStart()) ;
                }
            } catch (Exception e) {
            }
        }

        removeCallbacksAndMessages(null);
        disconnect();
    }

    public static boolean isAttach() {
        return Version != null;
    }

    public static boolean isScales() { //Является ли весами и какой версии
        String vrs = cmd(InterfaceScaleModule.CMD_VERSION); //Получаем версию весов
        if (vrs.startsWith(version)) {
            try {
                numVersion = Integer.valueOf(vrs.replace(version, ""));
                Version = selectVersion(numVersion);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private static Versions selectVersion(int version) throws Exception {
        switch (version) {
            case 1:
                return new V1();
            case 4:
                return new V4();
            default:
                throw new Exception("illegal version");
        }
    }

    /*****************************************************************************************/
    /*                                                                                       */
    /*                 Методы для использовани команд весового модуля                        */
    /*                                                                                       */
    /*****************************************************************************************/

    public static String getModuleVersion() {
        return cmd(InterfaceScaleModule.CMD_VERSION);
    }

    public static boolean setModuleFilterADC(int filterADC) {
        return cmd(InterfaceScaleModule.CMD_FILTER + filterADC).equals(InterfaceScaleModule.CMD_FILTER);
    }

    public static String getModuleTimeOff() {
        return cmd(InterfaceScaleModule.CMD_TIMER);
    }

    public static boolean setModuleTimeOff(int timeOff) {
        return cmd(InterfaceScaleModule.CMD_TIMER + timeOff).equals(InterfaceScaleModule.CMD_TIMER);
    }

    public static String getModuleSpeedPort() {
        return cmd(InterfaceScaleModule.CMD_SPEED);
    }

    public static boolean setModuleSpeedPort(int speed) {
        return cmd(InterfaceScaleModule.CMD_SPEED + speed).equals(InterfaceScaleModule.CMD_SPEED);
    }

    public static String getModuleOffsetSensor() {
        return cmd(InterfaceScaleModule.CMD_GET_OFFSET);
    }

    public static boolean setModuleOffsetSensor() {
        return cmd(InterfaceScaleModule.CMD_SET_OFFSET).equals(InterfaceScaleModule.CMD_SET_OFFSET);
    }

    public static String getModuleSensor() {
        return cmd(InterfaceVersions.CMD_SENSOR);
    }

    public static int getModuleBatteryCharge() {
        try {
            battery = Integer.valueOf(cmd(InterfaceScaleModule.CMD_BATTERY));
        } catch (Exception e) {
            battery = -0;
        }
        return battery;
    }

    public static boolean setModuleBatteryCharge(int charge) {
        return cmd(InterfaceScaleModule.CMD_CALL_BATTERY + charge).equals(InterfaceScaleModule.CMD_CALL_BATTERY);
    }

    public static int getModuleTemperature() {
        try {
            return (int) ((float) ((Integer.valueOf(cmd(InterfaceScaleModule.CMD_DATA_TEMP)) - 0x800000) / 7169) / 0.81) - 273;
        } catch (Exception e) {
            return -273;
        }
    }

    public static String getModuleHardware() {
        return cmd(InterfaceScaleModule.CMD_HARDWARE);
    }

    public static boolean setModuleName(String name) {
        return cmd(InterfaceScaleModule.CMD_NAME + name).equals(InterfaceScaleModule.CMD_NAME);
    }

    public static boolean setModuleCalibrateBattery(int percent) {
        return cmd(InterfaceScaleModule.CMD_CALL_BATTERY + percent).equals(InterfaceScaleModule.CMD_CALL_BATTERY);
    }
    /*****************************************************************************************/
    /*                                                                                       */
    /*                                    Общие методы                                       */
    /*                                                                                       */
    /*****************************************************************************************/

    public static int getNumVersion() {
        return numVersion;
    }

    public static void setNumVersion(int version) {
        numVersion = version;
    }

    public static String getName() {
        return device.getName();
    }

    public static String getAddress() {
        return device.getAddress();
    }

    public static int getSensorTenzo() {
        return Version.getSensorTenzo();
    }

    public static int getLimitTenzo() {
        return Version.getLimitTenzo();
    }

    public static int getMarginTenzo() {
        return Versions.getMarginTenzo();
    }

    public static boolean load() throws Error {
        return Version.load();
    }

    public static boolean setOffsetScale() {
        return Version.setOffsetScale();
    }

    public static boolean isLimit() {
        return Version.isLimit();
    }

    public static boolean isMargin() { return Version.isMargin(); }

    public static int updateWeight() { return Version.updateWeight(); }

    public static boolean setScaleNull() {
        return Version.setScaleNull();
    }

    public static boolean writeData() {
        return Version.writeData();
    }

    public static boolean setSpreadsheet(String sheet) {
        return Version.setSpreadsheet(sheet);
    }

    public static boolean setUsername(String username) {
        return Version.setUsername(username);
    }

    public static boolean setPassword(String password) {
        return Version.setPassword(password);
    }

    public static boolean setPhone(String phone) {
        return Version.setPhone(phone);
    }

    public static void processBattery(final boolean process, final HandlerBatteryTemperature msg) {
        try {
            if (isAttach()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (measureBatteryTemperature != null) {
                            measureBatteryTemperature.execute(false);
                            while (measureBatteryTemperature.isStart()) ;
                        }
                        measureBatteryTemperature = new MeasureBatteryTemperature(msg);
                        measureBatteryTemperature.execute(process);
                    }
                }).start();
            }

        } catch (Exception e) {
        }
    }

    public static void processUpdate(final boolean process, final HandlerWeightUpdate msg) {
        try {
            if (isAttach()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (measureWeightUpdate != null) {
                            measureWeightUpdate.execute(false);
                            while (measureWeightUpdate.isStart()) ;
                        }
                        measureWeightUpdate = new MeasureWeightUpdate(msg);
                        measureWeightUpdate.execute(process);
                    }
                }).start();
            }

        } catch (Exception e) {
        }
    }
}
