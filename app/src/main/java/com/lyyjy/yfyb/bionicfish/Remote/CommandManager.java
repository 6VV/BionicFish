package com.lyyjy.yfyb.bionicfish.Remote;

import android.util.Log;

/**
 * Created by Administrator on 2016/8/9.
 */
public class CommandManager {
    private static final String TAG="CommandManager";

    public enum CommandCode{
        FISH_UP,
        FISH_LEFT,
        FISH_RIGHT,
        AUTO_SWIM,
        MANUAL_SWIM,
    }

    public static final byte BACK_SUCCESS = (byte) 0x68;    //设置成功
    public static final byte BACK_RESET_NAME = (byte) 0x02;   //设置名字成功
    public static final byte BACK_HEART_HIT=(byte)0x04;     //心跳
    public static final byte BACK_PROGRAM=(byte)0x06;   //程序文本

    private static final byte STATUS_HEAD_1 = (byte) 0x55; //命令头
    private static final byte STATUS_HEAD_2 = (byte) 0xAA;
    private static final byte STATUS_HEAD_3 = (byte) 0x99;
    private static final byte STATUS_HEAD_4 = (byte) 0x11;
    private static final byte STATUS_FINAL = (byte) 0xFF;    //数据尾

    public static final byte STATUS_FISH_UP =(byte)0x00;      //小鱼前进指令
    public static final byte STATUS_FISH_RIGHT =(byte)0x03;   //小鱼右转指令
    public static final byte STATUS_FISH_LEFT =(byte)0x02;    //小鱼左转指令
    public static final byte STATUS_FISH_STOP =(byte)0x20;    //小鱼停止指令

    private static final byte STATUS_MODE_MANUAL =(byte)0x00;
    private static final byte STATUS_MODE_AUTO =(byte)0x01;

    private static final byte REQUEST_CONTROL = (byte) 0x00;  //控制指令
    private static final byte REQUEST_SET_NAME = (byte) 0x02; //设置名字
    private static final byte REQUEST_RESET = (byte) 0x04;      //重置设备
    public static final byte REQUEST_COLOR=(byte)0x05;      //灯光颜色
    private static final byte REQUEST_MODE=(byte)0x06;      //游动模式
    private static final byte REQUEST_DIRECTION_PROGRAM=(byte)0x07; //方向控制程序
    private static final byte REQUEST_LIGHT_PROGRAM=(byte)0x08; //灯光控制程序

    private static byte[] COMMAND_SET_COLOR={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4, REQUEST_COLOR,0x00, STATUS_FINAL}; //设置名字
    private static byte[] COMMAND_SET_MODE={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4,REQUEST_MODE,0x00, STATUS_FINAL};       //设置游动方式
    private static final byte[] COMMAND_RESET_DEVICE={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4, REQUEST_RESET, STATUS_FINAL};  //重置设备
    public static final byte[] COMMAND_DERECTION_PROGRAM={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3,STATUS_HEAD_4,REQUEST_DIRECTION_PROGRAM};
    public static final byte[] COMMAND_LIGHT_PROGRAM={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3,STATUS_HEAD_4,REQUEST_LIGHT_PROGRAM};
    public static final byte[] COMMAND_PROGRAM_FINAL={STATUS_FINAL,STATUS_FINAL};   //程序尾

    private static byte[] FISH_COMMAND={
            STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4,
            REQUEST_CONTROL,      //控制小鱼
            0x00,                   //游动方向
            (byte) 0x03,           //速度
            STATUS_FINAL};

    /*此段代码进行了修改，当向左转、向右转时，速度均保持为最低档，以防止仿生鱼转弯时卡死。
    * 小鱼调好时，应将其修改过来*/
    public static void sendSwimDirection(CommandCode commandCode){
        RemoteParent remote=RemoteFactory.getRemote();
        switch (commandCode){
            case FISH_LEFT:{
                setDirection(STATUS_FISH_LEFT);
                remote.send(FISH_COMMAND);


//                byte[] newCommand=FISH_COMMAND.clone();
//                newCommand[6]=(byte)1;
//                newCommand[5]=STATUS_FISH_LEFT;
//                remote.send(newCommand);
            }break;
            case FISH_RIGHT:{
                setDirection(STATUS_FISH_RIGHT);
                remote.send(FISH_COMMAND);

//                byte[] newCommand=FISH_COMMAND.clone();
//                newCommand[6]=(byte)1;
//                newCommand[5]=STATUS_FISH_RIGHT;
//                remote.send(newCommand);
            }break;
            case FISH_UP:{
                setDirection(STATUS_FISH_UP);
                remote.send(FISH_COMMAND);
//                Log.e(TAG,"up");
            }break;
        }
    }

    public static void sendColor(byte color){
        COMMAND_SET_COLOR[5]=color;
        RemoteFactory.getRemote().send(COMMAND_SET_COLOR);
    }

    public static void sendSwimMode(CommandCode commandCode){
        switch (commandCode){
            case MANUAL_SWIM:{
                COMMAND_SET_MODE[5]= STATUS_MODE_MANUAL;
                RemoteFactory.getRemote().send(COMMAND_SET_MODE);
            }break;
            case AUTO_SWIM:{
                COMMAND_SET_MODE[5]= STATUS_MODE_AUTO;
                RemoteFactory.getRemote().send(COMMAND_SET_MODE);
            }break;
            default:return;
        }
    }

    public static void resetName(String name){
        RemoteFactory.getRemote().send(getNameCommand(name));
    }

    public static void resetDevice(){
        RemoteFactory.getRemote().send(COMMAND_RESET_DEVICE);
    }

    public static void setSpeed(byte speed){
        FISH_COMMAND[6]=speed;
    }

    private static void setDirection(byte direction){
        FISH_COMMAND[5]=direction;
    }

    private static byte[] getNameCommand(String name){
        byte[] nameBytes=name.getBytes();
        int nameLength=nameBytes.length;
        byte[] bytesSetName = new byte[7 + nameLength];
        bytesSetName[0] = STATUS_HEAD_1;
        bytesSetName[1] = STATUS_HEAD_2;
        bytesSetName[2] = STATUS_HEAD_3;
        bytesSetName[3] = STATUS_HEAD_4;
        bytesSetName[4] = REQUEST_SET_NAME;
        bytesSetName[5] = (byte) nameLength;
        for (int i = 0; i < nameLength; i++) {
            bytesSetName[6 + i] = nameBytes[i];
        }
        bytesSetName[6 + nameLength] = STATUS_FINAL;

        return bytesSetName;
    }
}
