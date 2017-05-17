package com.lyyjy.yfyb.bionicfish.Program;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.DirectionGrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParserAbstract;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.LightGrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.TokenParser;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;
import com.lyyjy.yfyb.bionicfish.Remote.IRemoteCallback;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteFactory;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/7/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ProgramSender implements IRemoteCallback {
    @SuppressWarnings("unused")
    private static final String TAG = "ProgramSender";
    private static final int FRAME_LENGTH=17;

    private final Context mContext;
    private ByteBuffer mByteBuffer;
//    private int mSendTimes = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_SENT_TIME=500;


    public ProgramSender(Context context) {
        mContext = context;
    }

    public void sendData(String directionText,String lightText){
        ByteBuffer directionBuffer=getDataBuffer(directionText, CommandManager.COMMAND_DIRECTION_PROGRAM,new DirectionGrammarParser());
        ByteBuffer lightBuffer=getDataBuffer(lightText, CommandManager.COMMAND_LIGHT_PROGRAM,new LightGrammarParser());

        if (directionBuffer==null && lightBuffer==null){
            return;
        }
        if (directionBuffer==null) {
            mByteBuffer=ByteBuffer.allocate(lightBuffer.array().length);
            mByteBuffer.put(lightBuffer.array());
        }else if (lightBuffer==null){
            mByteBuffer=ByteBuffer.allocate(directionBuffer.array().length);
            mByteBuffer.put(directionBuffer.array());
        }else {
            mByteBuffer=ByteBuffer.allocate(directionBuffer.array().length+lightBuffer.array().length);
            mByteBuffer.put(directionBuffer.array());
            mByteBuffer.put(lightBuffer.array());
        }
        mByteBuffer.flip();
        send();
    }

    private ByteBuffer getDataBuffer(String text,byte[] commandHead, GrammarParserAbstract grammarParser){
        TokenParser parser = new TokenParser(text.toUpperCase());
        try {
            grammarParser.parse(parser);
            byte[] dataText = grammarParser.getRootNode().interpret();

            if(dataText.length==0){
                return null;
            }
            if (dataText.length>111){
                Toast.makeText(mContext, "文本长度过长", Toast.LENGTH_SHORT).show();
                return null;
            }
            byte length1 = (byte) (dataText.length >> 8);
            byte length2 = (byte) dataText.length;

            int realLength=dataText.length+commandHead.length+2;    //指令实际字节数
            int remainder=realLength%FRAME_LENGTH;  //余数
            int textLength=remainder==0?realLength:realLength+FRAME_LENGTH-remainder;   //帧的整数倍

            ByteBuffer result=ByteBuffer.allocate(textLength);

            result.put(commandHead);
            result.put(length1);
            result.put(length2);
            result.put(dataText);
            result.put(new byte[textLength-realLength]);
            return result;
        } catch (InterpreterException e) {
            Toast.makeText(mContext, e.getInformation(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onEnableChanged() {

    }

    @Override
    public void onConnectChanged() {

    }

    @Override
    public void onReceiveData(byte[] data) {
        if (data==null || data.length<1)
        {
            return;
        }
        if (data[0]==CommandManager.BACK_PROGRAM)
        {
            if (mIsSending){
                send();
            }
        }
    }

    private static final int HANDLER_COMMAND=1;
    private static final int HANDLER_AUTO_SWIM=2;

    private static class HandlerSendData extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ProgramSender.HANDLER_COMMAND:{
                    byte[] data=msg.getData().getByteArray("data");

                    RemoteFactory.getRemote().send(data);
                }break;
                case ProgramSender.HANDLER_AUTO_SWIM:{
                    CommandManager.sendSwimMode(CommandManager.CommandCode.AUTO_SWIM);
                }break;
                default:
            }

        }
    }

    @SuppressWarnings("unused")
    @NonNull
    private String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String tmp ;
        for (byte b : data)
        {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp).append(" ");
        }
        return sb.toString();
    }

    private final HandlerSendData handlerSendData=new HandlerSendData();

    private void send(){
        handlerTimeOut.removeCallbacks(runnableTimeOut);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //发送指令完毕后，发送自动游指令
        if (mByteBuffer.position()>=mByteBuffer.limit()){
            Message msg=new Message();
            msg.what=HANDLER_AUTO_SWIM;
            handlerSendData.sendMessage(msg);

            return;
        }

        byte[] data=new byte[FRAME_LENGTH];
        mByteBuffer.get(data,0,FRAME_LENGTH);

        RemoteFactory.getRemote().registerRemoteCallback(this);

        Bundle bundle=new Bundle();
        bundle.putByteArray("data",data);
        Message msg=new Message();
        msg.setData(bundle);
        msg.what=HANDLER_COMMAND;
        handlerSendData.sendMessage(msg);

//        BluetoothBleManager.GetInstance(mContext).writeDataToDevice(data);
        mIsSending=true;
        handlerTimeOut.postDelayed(runnableTimeOut,MAX_SENT_TIME);
    }

    private boolean mIsSending=false;
    private final Handler handlerTimeOut=new Handler();
    private final Runnable runnableTimeOut=new Runnable() {
        @Override
        public void run() {

                mIsSending=false;
                Toast.makeText(mContext,"发送失败\n请重新发送",Toast.LENGTH_SHORT).show();
//            }

        }
    };
}
