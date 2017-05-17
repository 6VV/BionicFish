package com.lyyjy.yfyb.bionicfish.Remote;

/**
 * Created by Administrator on 2016/8/8.
 */
@SuppressWarnings("DefaultFileTemplate")
public class RemoteFactory {
    private static RemoteParent mRemote=null;

    public static RemoteParent getRemote(){
        if (mRemote==null){
            mRemote=new RemoteBluetooth();
        }
        return mRemote;
    }

}
