package com.lyyjy.yfyb.bionicfish.Remote;

import android.content.Context;

import com.lyyjy.yfyb.bionicfish.ContextUtil;

/**
 * Created by Administrator on 2016/8/8.
 */
public class RemoteFactory {
    private static RemoteParent mRemote=null;

    public static RemoteParent getRemote(){
        if (mRemote==null){
            mRemote=new RemoteBluetooth();
        }

        return mRemote;
    }

}
