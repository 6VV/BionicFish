package com.lyyjy.yfyb.bionicfish.remote;

import com.lyyjy.yfyb.bionicfish.VersionControl;

/**
 * Created by Administrator on 2016/8/8.
 */
@SuppressWarnings("DefaultFileTemplate")
public class RemoteFactory {
    private static RemoteParent mRemote=null;

    public static RemoteParent getRemote(){
        if (mRemote==null){
            if (VersionControl.isWifi()){
                mRemote=new RemoteWifi();
            }else{
                mRemote=new RemoteBluetooth();
            }
        }
        return mRemote;
    }

}
