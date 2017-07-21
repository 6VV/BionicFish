package com.lyyjy.yfyb.bionicfish;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class WifiAp {
    private static final String TAG = "WifiAp";

    private WifiManager mWifiManager;

    private String mWifiApName = "my_fish";
    private String mWifiPassword = "123456789";

    public WifiAp(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static ArrayList<String> getConnectedHotIP() {
        ArrayList<String> connectedIP = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);

                    for (String text:splitted){
                        Log.d(TAG, "getConnectedHotIP: "+text);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    public static boolean ping(String ip) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -t 2 -w 1 " + ip);

            int status = p.waitFor();
            if (status == 0) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setWifiApName(String name) {
        mWifiApName = name;
    }

    public void setWifiPassword(String password) {
        mWifiPassword = password;
    }

    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            mWifiManager.setWifiEnabled(false);
        }

        try {
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method.setAccessible(true);

            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager, createWifiInfo(mWifiApName, mWifiPassword, PasswordType.TYPE_WPA), enabled);
        } catch (Exception e) {
            return false;
        }
    }

    enum PasswordType {
        TYPE_NO_PASSWD,
        TYPE_WEP,
        TYPE_WPA,
    }


    public WifiConfiguration createWifiInfo(String SSID, String password, PasswordType type) {

        Log.v(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;

//        WifiConfiguration tempConfig = this.isExsits(SSID);
//        if (tempConfig != null) {
//            mWifiManager.removeNetwork(tempConfig.networkId);
//        }

        boolean passwordVisible = false;
        // 分为三种情况：1没有密码2用wep加密3用wpa加密
        switch (type) {
            case TYPE_NO_PASSWD: {
                config.hiddenSSID = passwordVisible;
//                config.preSharedKey="12122112";
//                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                config.wepTxKeyIndex = 0;
            }
            break;
            case TYPE_WEP: {
                config.hiddenSSID = passwordVisible;
                config.wepKeys[0] = password;
                config.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            }
            break;
            case TYPE_WPA: {
                config.preSharedKey = password;
                config.hiddenSSID = passwordVisible;
                config.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
            break;
        }

        return config;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {
                return existingConfig;
            }
        }
        return null;
    }

    private String localIp() {
        DhcpInfo wifiInfo = mWifiManager.getDhcpInfo();
        int ipAddress = wifiInfo.serverAddress;
        return (ipAddress & 0xFF) + "."
                + ((ipAddress >> 8) & 0xFF) + "."
                + ((ipAddress >> 16) & 0xFF) + "."
                + "255";
    }

    private enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    /**
     * 判断热点开启状态
     */
    public boolean isWifiApEnabled() {
        WIFI_AP_STATE state = getWifiApState();
//        Log.e(TAG,String.valueOf(state));
        return state == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED || state == WIFI_AP_STATE.WIFI_AP_STATE_ENABLING;
    }

    private WIFI_AP_STATE getWifiApState() {
        int tmp;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(mWifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }
}
