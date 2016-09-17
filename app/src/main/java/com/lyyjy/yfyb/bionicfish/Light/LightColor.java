package com.lyyjy.yfyb.bionicfish.Light;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/5.
 */
public class LightColor {
    public static final String COLOR_WHITE_NAME="WHITE";
    public static final String COLOR_YELLOW_NAME="YELLOW";
    public static final String COLOR_VIOLET_NAME="VIOLET";
    public static final String COLOR_RED_NAME="RED";
    public static final String COLOR_CYAN_NAME="CYAN";
    public static final String COLOR_GREEN_NAME="GREEN";
    public static final String COLOR_BLUE_NAME="BLUE";
    public static final String COLOR_BLACK_NAME="BLACK";

    public static final HashMap<String,Integer> COLOR_MAP=new HashMap<String,Integer>(){
        {
            put(COLOR_WHITE_NAME,0xFFFFFFFF);
            put(COLOR_YELLOW_NAME,0xFFFFFF00);
            put(COLOR_VIOLET_NAME,0xFFFF00FF);
            put(COLOR_RED_NAME,0xFFFF0000);
            put(COLOR_CYAN_NAME,0xFF00FFFF);
            put(COLOR_GREEN_NAME,0xFF00FF00);
            put(COLOR_BLUE_NAME,0xFF0000FF);
            put(COLOR_BLACK_NAME,0xFF000000);
        }
    };

    private int mRgbColor;

    public LightColor(String color){
        mRgbColor=COLOR_MAP.get(color);
    }
    public LightColor(int color){
        mRgbColor=color;
    }

    public int getRgbColor(){
        return mRgbColor;
    }

    public byte getByteColor(){
        return toColorByte(mRgbColor);
    }

    private byte toColorByte(int color){
        byte result=0;
        if ((color&0x000000FF)!=0){
            result+=1;
        }
        if ((color&0x0000FF00)!=0){
            result+=2;
        }
        if ((color&0x00FF0000)!=0){
            result+=4;
        }
        return result;
    }
}
