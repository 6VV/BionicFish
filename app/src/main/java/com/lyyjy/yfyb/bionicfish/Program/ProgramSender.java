package com.lyyjy.yfyb.bionicfish.Program;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.DirectionGrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParserAbstract;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.LightGrammarParser;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;
import com.lyyjy.yfyb.bionicfish.Remote.IRemoteCallback;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteFactory;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/7/15.
 */
public class ProgramSender implements IRemoteCallback {
    private final int FRAME_LENGTH=17;

    private Context mContext;
    private ByteBuffer mByteBuffer;
//    private int mSendTimes = 0;
    private final int MAX_SENT_TIME=500;

//    public void resetSendTimes(){
//        mSendTimes=0;
//    }
//    private int mCurrentIndex=0;

    public ProgramSender(Context context) {
        mContext = context;
    }

    public void sendData(String directionText,String lightText){
        ByteBuffer directionBuffer=getDataBuffer(directionText, CommandManager.COMMAND_DERECTION_PROGRAM,new DirectionGrammarParser());
        ByteBuffer lightBuffer=getDataBuffer(lightText, CommandManager.COMMAND_LIGHT_PROGRAM,new LightGrammarParser());

        if (directionBuffer==null || lightBuffer==null){
            return;
        }

        mByteBuffer=ByteBuffer.allocate(directionBuffer.array().length+lightBuffer.array().length);
        mByteBuffer.put(directionBuffer.array());
        mByteBuffer.put(lightBuffer.array());

        mByteBuffer.flip();
        send();
    }

    private ByteBuffer getDataBuffer(String text,byte[] commandHead, GrammarParserAbstract grammarParser){
        TokenParser parser = new TokenParser(text.toUpperCase());
        try {
            grammarParser.parse(parser);
            byte[] dataText = grammarParser.getRootNode().interpret();

            String info=new String();
            for (byte var :dataText) {
                info=info+String.valueOf(var)+' ';
            }
            Log.e("Data", info);

            if (dataText.length>500){
                Toast.makeText(mContext, "文本长度过长", Toast.LENGTH_SHORT).show();
                return null;
            }
            byte length1 = (byte) (dataText.length >> 8);
            byte length2 = (byte) dataText.length;

            int textLength=dataText.length;
            int remainder=textLength%FRAME_LENGTH;
            textLength=remainder==0?textLength:textLength+FRAME_LENGTH-remainder;

            ByteBuffer result=ByteBuffer.allocate(textLength+FRAME_LENGTH);

//            result.put(commandHead);
//            result.put(length1);
//            result.put(length2);
//            result.put(new byte[FRAME_LENGTH-commandHead.length-2]);

            result.put(commandHead);
            result.put(length1);
            result.put(length2);
            result.put(new byte[FRAME_LENGTH-commandHead.length-2]);
            result.put(dataText);
            result.put(new byte[textLength-dataText.length]);

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
        if (mIsSending){
            send();
        }
    }


    private class HandlerSendData extends Handler{
        @Override
        public void handleMessage(Message msg) {
            byte[] data=msg.getData().getByteArray("data");

            String info=new String();
            for (byte var :data) {
                info=info+String.valueOf(var)+' ';
            }
            Log.e("Data", info);

            RemoteFactory.getRemote().send(data);
        }
    }
    HandlerSendData handlerSendData=new HandlerSendData();

    private void send(){
        handlerTimeOut.removeCallbacks(runnableTimeOut);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("Position", String.valueOf(mByteBuffer.position()));
        Log.e("Length", String.valueOf(mByteBuffer.limit()));
        if (mByteBuffer.position()>=mByteBuffer.limit()){
            return;
        }

        byte[] data=new byte[FRAME_LENGTH];
        mByteBuffer.get(data,0,FRAME_LENGTH);

        RemoteFactory.getRemote().registerRemoteCallback(this);

        Bundle bundle=new Bundle();
        bundle.putByteArray("data",data);
        Message msg=new Message();
        msg.setData(bundle);
        handlerSendData.sendMessage(msg);

//        BluetoothBleManager.GetInstance(mContext).writeDataToDevice(data);
        mIsSending=true;
        handlerTimeOut.postDelayed(runnableTimeOut,MAX_SENT_TIME);
    }

    private boolean mIsSending=false;
    Handler handlerTimeOut=new Handler();
    Runnable runnableTimeOut=new Runnable() {
        @Override
        public void run() {
//            if (mSendTimes<MAX_SENT_TIME){
//               mByteBuffer.rewind();
//                send();
//                ++mSendTimes;
//            }else{
                mIsSending=false;
                Toast.makeText(mContext,"发送失败\n请重新发送",Toast.LENGTH_SHORT).show();
//            }

        }
    };
}
