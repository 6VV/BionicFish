package com.lyyjy.yfyb.bionicfish.program.ui_program;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/7.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramBlock {
    @SuppressWarnings("unused")
    private static final String TAG = ProgramBlock.class.getSimpleName();

    //控件当前状态
    public enum State {
        CAPTURED,   //被拖动
        NORMAL, //常态
        INSERTED,  //被插入
    }

    private final ArrayList<ProgramView> mViews = new ArrayList<>();

    private boolean mIsInserting = false;    //正在被插入

    private ProgramBlock mPreBlock = null;      //将要连接的前一个目标块
    private ProgramBlock mNextBlock = null;    //将要连接的后一个目标块

    public ProgramBlock(){
    }

    public ProgramBlock(ProgramView view) {
        add(view);
    }

    public boolean isInserting() {
        return mIsInserting;
    }

    public void setInserting(boolean inserting) {
        mIsInserting = inserting;
    }

    public ProgramBlock getPreBlock() {
        return mPreBlock;
    }

    public void setPreBlock(ProgramBlock preBlock) {
        mPreBlock = preBlock;
    }

    public ProgramBlock getNextBlock() {
        return mNextBlock;
    }

    public void setNextBlock(ProgramBlock nextBlock) {
        mNextBlock = nextBlock;
    }

    public void add(ProgramBlock block) {
        mViews.addAll(block.getViews());
        for (ProgramView view : block.getViews()) {
            view.setProgramBlock(this);
        }
    }

    public void add(ProgramView view){
        mViews.add(view);
        view.setProgramBlock(this);
    }

    public ProgramBlock clone(Context context){
        ProgramBlock newBlock=new ProgramBlock();
        for (ProgramView view:mViews){
            newBlock.add(view.clone(context));
        }
        newBlock.mIsInserting=mIsInserting;
        newBlock.mPreBlock=mPreBlock;
        newBlock.mNextBlock=mNextBlock;

        return newBlock;
    }

    public String executeText(){
        StringBuilder stringBuilder=new StringBuilder();
        for (ProgramView view:mViews){
            stringBuilder.append(view.executeText()).append("\n");
        }

        return stringBuilder.toString();
    }

    public void pushHead(ProgramBlock block){
        mViews.addAll(0,block.getViews());
        for (ProgramView view : block.getViews()) {
            view.setProgramBlock(this);
        }
        block.clearView();
    }

    private void clearView(){
        mViews.clear();
    }

    public boolean contains(View view) {
        //noinspection SuspiciousMethodCalls
        return mViews.contains(view);
    }

    public void changeState(ProgramBlock.State state) {
        for (ProgramView view : mViews) {
            view.changeState(state);
        }
    }

    public ArrayList<ProgramView> getViews() {
        return mViews;
    }

    public Rect getTopRaisedArea() {
    return mViews.get(0).getTopRaisedArea();
}

    public Rect getBottomConcaveArea() {
        return mViews.get(mViews.size() - 1).getBottomConcaveArea();
    }

    public boolean haveDesBlock(){
        return mNextBlock!=null || mPreBlock!=null;
    }

    public ProgramBlock blockTouched(PointF pointF){
        for (ProgramView view:mViews){
            if (view.isBoundingTouched(pointF)){
                ProgramBlock result=new ProgramBlock();
                int index=mViews.indexOf(view);
                for (int i=index;i<mViews.size();++i){
                    result.add(mViews.get(i));
                }

                return result;
            }
        }

        return null;
    }

    public void removeBlock(ProgramBlock block){
        mViews.removeAll(block.getViews());
    }

}
