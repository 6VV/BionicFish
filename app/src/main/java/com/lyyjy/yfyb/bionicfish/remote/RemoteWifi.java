package com.lyyjy.yfyb.bionicfish.remote;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.lyyjy.yfyb.bionicfish.Device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/10/10.
 */

class RemoteWifi extends RemoteParent {
    private final int PORT=2000;
    private InetAddress mAddress=null;

    private DatagramSocket mSocket=null;

    public RemoteWifi(){
        try {
            mSocket=new DatagramSocket(PORT);
            try {
                mAddress= InetAddress.getByName("192.168.43.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerRemoteCallback(IRemoteCallback iRemoteCallback) {

    }

    @Override
    public void unregisterRemoteCallback() {

    }

    @Override
    public BroadcastReceiver registerEnableReceiver(Context context, IRemoteCallback iRemoteCallback) {
        return null;
    }

    @Override
    public void unregisterEnableReceiver(Context context, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    public boolean changeEnabled() {
        return false;
    }

    @Override
    public void connect(Device device) {
        try {
            mAddress= InetAddress.getByName(device.getAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public ConnectState getConnectState() {
        return null;
    }

    @Override
    public Device getDeviceConnected() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isScanning() {
        return false;
    }

    @Override
    public void tryScanDevice(boolean enable, IRemoteScan iRemoteScan) {

    }

    @Override
    public void send(final byte[] data) {
        if (mAddress==null){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length,mAddress,PORT);
                try {
                    mSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
