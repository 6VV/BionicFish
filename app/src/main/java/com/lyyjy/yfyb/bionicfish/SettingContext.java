package com.lyyjy.yfyb.bionicfish;

import com.lyyjy.yfyb.bionicfish.DataPersistence.DatabaseManager;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public class SettingContext {
    public final String SETTINGS_AUTO_CLOSE_WIRELESS="SettingCloseWireless";
    public final String SETTINGS_AUTO_RECONNECT = "SettingReconnect";

    public enum ReconnectState{
        AUTO_CONNECT,
        MANUAL_CONNECT,
    }

    public enum CloseWirelessState{
        AUTO_CLOSE_WIRELESS,
        MANUAL_CLOSE_WIRELESS,
    }

    private DatabaseManager mDatabaseManager=null;
    private Map<String,Integer> mSettingValues;
    private static SettingContext mInstance=null;

    public static SettingContext getInstance(){
        if (mInstance==null){
            mInstance= new SettingContext();
        }
        return mInstance;
    }

    private SettingContext(){
        mDatabaseManager=DatabaseManager.getInstance();
        mSettingValues=mDatabaseManager.selectSettingValues();

        if (mSettingValues.get(SETTINGS_AUTO_CLOSE_WIRELESS)==null){
            mSettingValues.put(SETTINGS_AUTO_CLOSE_WIRELESS,CloseWirelessState.MANUAL_CLOSE_WIRELESS.ordinal());
        }
        if (mSettingValues.get(SETTINGS_AUTO_RECONNECT)==null){
            mSettingValues.put(SETTINGS_AUTO_RECONNECT, ReconnectState.MANUAL_CONNECT.ordinal());
        }
    }

    public boolean isAutoConnect(){
        return mSettingValues.get(SETTINGS_AUTO_RECONNECT)==ReconnectState.AUTO_CONNECT.ordinal();
    }

    public boolean isAutoCloseWireless(){
        return mSettingValues.get(SETTINGS_AUTO_CLOSE_WIRELESS)==CloseWirelessState.AUTO_CLOSE_WIRELESS.ordinal();
    }

    public void saveSettings(ReconnectState reconnectState,CloseWirelessState closeWirelessState){
        mSettingValues.put(SETTINGS_AUTO_RECONNECT, reconnectState.ordinal());
        mSettingValues.put(SETTINGS_AUTO_CLOSE_WIRELESS, closeWirelessState.ordinal());

        mDatabaseManager.replaceSettingValues(SETTINGS_AUTO_RECONNECT, reconnectState.ordinal());
        mDatabaseManager.replaceSettingValues(SETTINGS_AUTO_CLOSE_WIRELESS, closeWirelessState.ordinal());
    }
}
