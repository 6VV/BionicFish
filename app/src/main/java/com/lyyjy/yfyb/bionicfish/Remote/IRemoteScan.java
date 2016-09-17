package com.lyyjy.yfyb.bionicfish.Remote;

import com.lyyjy.yfyb.bionicfish.Device;

/**
 * Created by Administrator on 2016/8/8.
 */
public interface IRemoteScan {
    void scan(boolean enable);
    void onScan(final Device device);
}
