package com.lyyjy.yfyb.bionicfish.remote;

/**
 * Created by Administrator on 2016/8/8.
 */
@SuppressWarnings("DefaultFileTemplate")
public interface IRemoteCallback {
    void onEnableChanged();
    void onConnectChanged();
//    void onElectricQuantityChanged(int power);
    void onReceiveData(byte[] data);
}
