package com.lyyjy.yfyb.bionicfish.Program.TextProgram;

import com.lyyjy.yfyb.bionicfish.Light.LightColor;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Administrator on 2016/6/27.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ProgramCommand {
    public static final String COMMAND_UP = "UP";
    public static final String COMMAND_LEFT = "LEFT";
    public static final String COMMAND_RIGHT = "RIGHT";
    public static final String COMMAND_WAIT = "WAIT";
    public static final String COMMAND_LIGHT = "LIGHT";

    public static final HashMap<String, String> COMMANDS = new HashMap<String, String>() {
        {
            put(COMMAND_UP, "以速度%s前进%s秒\n时间单位：秒");
            put(COMMAND_LEFT, "以速度%s左转%s秒\n时间单位：秒");
            put(COMMAND_RIGHT, "以速度%s右转%s秒\n时间单位：秒");
            put(COMMAND_WAIT, "原地等待%s秒\n时间单位：秒");
            put(COMMAND_LIGHT, "灯光保持%s颜色%s秒\n时间单位：0.1秒");
        }
    };

    public static final HashSet<String> KEYWORDS=new HashSet<String>(){
        {
            addAll(LightColor.COLOR_MAP.keySet());
        }
    };

}
