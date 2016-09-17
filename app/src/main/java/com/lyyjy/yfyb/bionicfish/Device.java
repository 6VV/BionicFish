package com.lyyjy.yfyb.bionicfish;

/**
 * Created by Administrator on 2016/8/8.
 */
public class Device {
    private String mName;
    private String mAddress;

    public Device(String name,String address){
        mName=name;
        mAddress=address;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }
}
