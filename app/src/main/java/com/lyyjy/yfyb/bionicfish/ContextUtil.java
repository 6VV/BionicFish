package com.lyyjy.yfyb.bionicfish;

import android.app.Application;

/**
 * Created by Administrator on 2016/8/9.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ContextUtil extends Application {
    private static ContextUtil instance;

    public static ContextUtil getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
