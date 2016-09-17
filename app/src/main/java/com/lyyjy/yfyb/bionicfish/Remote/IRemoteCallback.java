package com.lyyjy.yfyb.bionicfish.Remote;

/**
 * Created by Administrator on 2016/8/8.
 */
public interface IRemoteCallback {
    void onEnableChanged();
    void onConnectChanged();
//    void onElectricQuantityChanged(int power);
    void onReceiveData(byte[] data);
}
