package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/9.
 */

@SuppressWarnings("DefaultFileTemplate")
public class LayoutDisplayView extends ScrollView {
    @SuppressWarnings("unused")
    private static final String TAG = LayoutDisplayView.class.getSimpleName();

    private Context mContext = null;
    private FrameLayout mLayout = null;

    private final ArrayList<ProgramBlock> mProgramBlocks = new ArrayList<>();  //程序块集合

    public LayoutDisplayView(Context context) {
        super(context);
        init(context);
    }

    public LayoutDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public String executeMoveText(){
        for (ProgramBlock block:mProgramBlocks){
            if (block.getViews().get(0) instanceof ProgramFishMovementStartView){
                return block.executeText();
            }
        }

        return "";
    }

    public String executeLightText(){
        for (ProgramBlock block:mProgramBlocks){
            if (block.getViews().get(0) instanceof ProgramFishLightStartView){
                return block.executeText();
            }
        }

        return "";
    }

    public boolean contain(View view) {
        for (ProgramBlock block : mProgramBlocks) {
            if (block.contains(view)) {
                return true;
            }
        }

        return false;
    }

    public void clearView(){
        for (ProgramBlock block:mProgramBlocks){
            for (View view:block.getViews()){
                mLayout.removeView(view);
            }
        }

        mProgramBlocks.clear();

        updateStartViewState();
    }

    private void updateStartViewState() {
        ProgramFishMovementStartView.setAlreadyExist(false);
        ProgramFishLightStartView.setAlreadyExist(false);
        for (int i=0;i<mProgramBlocks.size();++i){
            if (mProgramBlocks.get(i).getViews().get(0) instanceof ProgramFishLightStartView){
                ProgramFishLightStartView.setAlreadyExist(true);
                break;
            }
        }
        for (int i=0;i<mProgramBlocks.size();++i){
            if (mProgramBlocks.get(i).getViews().get(0) instanceof ProgramFishMovementStartView){
                ProgramFishMovementStartView.setAlreadyExist(true);
                break;
            }
        }
    }

    public void loadFile(String fileName){
        clearView();
        new ProgramXmlPersistence(mContext).loadFile(fileName,mProgramBlocks);
        for (int i=0;i<mProgramBlocks.size();++i){
            ArrayList<ProgramView> views=mProgramBlocks.get(i).getViews();
            for (int j=0;j<views.size();++j){
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                View view=views.get(j);
                params.setMargins(view.getLeft(),view.getTop(),0,0);
                mLayout.addView(view,params);
            }
        }
        updateStartViewState();
    }

    private void moveToTop(ProgramBlock originBlock, ProgramBlock desBlock) {
        Rect thisBottomArea = originBlock.getBottomConcaveArea();
        Rect desTopArea = desBlock.getTopRaisedArea();

        int dx = desTopArea.left - thisBottomArea.left;
        int dy = desTopArea.top - thisBottomArea.top + originBlock.getViews().get(0).getRadius();

        move(originBlock, dx, dy);
    }

    public ProgramBlock newBlock(Context context, PointF pointF) {
        PointF relativePointF = new PointF(pointF.x - getLeft() + getScrollX(), pointF.y - getTop() + getScrollY());
        for (ProgramBlock block : mProgramBlocks) {
            ProgramBlock blockTouched = block.blockTouched(relativePointF);
            if (blockTouched != null) {
                //若选取的为第一个元素，则删除整块
                if (blockTouched.getViews().get(0) == block.getViews().get(0)) {
                    mProgramBlocks.remove(block);
                }

                //若选取的为开始块，则设置其为可生成
                if (blockTouched.getViews().get(0) instanceof ProgramFishLightStartView){
                    ProgramFishLightStartView.setAlreadyExist(false);
                }else if (blockTouched.getViews().get(0) instanceof ProgramFishMovementStartView){
                    ProgramFishMovementStartView.setAlreadyExist(false);
                }

                block.removeBlock(blockTouched);

                //复制程序块中的所有内容
                ProgramBlock newBlock = blockTouched.clone(context);

                //重新布局
                for (int i = 0; i < newBlock.getViews().size(); ++i) {
                    View newView = newBlock.getViews().get(i);
                    View oldView = blockTouched.getViews().get(i);

                    newView.layout(oldView.getLeft() + getLeft() - getScrollX(), oldView.getTop() + getTop() - getScrollY(),
                            oldView.getRight() + getLeft() - getScrollX(), oldView.getBottom() + getTop() - getScrollY());

                    mLayout.removeView(oldView);
                }

                return newBlock;
            }
        }

        return null;
    }

    public void onViewReleased(View releasedChild) {
        ProgramBlock programBlock = ((ProgramView) releasedChild).getProgramBlock();

        //将程序块放置在本布局中的正确位置
        alignBlock(programBlock, getLeft() - getScrollX(), getTop() - getScrollY());

        //复制程序块中的所有内容
        ProgramBlock newBlock = programBlock.clone(mContext);

        //重新布局
        for (int i = 0; i < newBlock.getViews().size(); ++i) {
            View newView = newBlock.getViews().get(i);
            View oldView = programBlock.getViews().get(i);

            mLayout.addView(newView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            newView.layout(oldView.getLeft() - getLeft() + getScrollX(), oldView.getTop() - getTop() + getScrollY(),
                    oldView.getRight() - getLeft() + getScrollX(), oldView.getBottom() - getTop() + getScrollY());
        }

        //若当前程序块未放置于任何程序块上，则添加当前程序块
        if (!newBlock.haveDesBlock()) {
            mProgramBlocks.add(newBlock);
        }

        //若选取的为开始块，则设置其为不可生成
        if (newBlock.getViews().get(0) instanceof ProgramFishLightStartView){
            ProgramFishLightStartView.setAlreadyExist(true);
        }else if (newBlock.getViews().get(0) instanceof ProgramFishMovementStartView){
            ProgramFishMovementStartView.setAlreadyExist(true);
        }

        //放下程序块
        releaseBlock(newBlock);

        //更新程序块位置
        for (ProgramBlock block : mProgramBlocks) {
            for (View v : block.getViews()) {
                LayoutParams params = (LayoutParams) v.getLayoutParams();
                params.setMargins(v.getLeft(), v.getTop(), 0, 0);
                v.setLayoutParams(params);
            }
        }
    }

    public void onViewPositionChanged(View changedView, int dx, int dy) {
        ProgramView view = (ProgramView) changedView;    //获取当前view

        //改变每个程序块的状态
        for (ProgramBlock v : mProgramBlocks) {
            if (v == view.getProgramBlock()) {
                continue;
            }
            if (changeBlockState(view.getProgramBlock(), v)) {
                break;
            }
        }

        //移动当前拖动控件所在程序块中的所有控件
        for (ProgramView v : view.getProgramBlock().getViews()) {
            if (v == view) {
                continue;
            }
            v.layout(v.getLeft() + dx, v.getTop() + dy, v.getRight() + dx, v.getBottom() + dy);
        }
    }

    public void saveFile(String fileName){
        new ProgramXmlPersistence(mContext).saveFile(fileName,mProgramBlocks);
    }

    private boolean changeBlockState(ProgramBlock capturedBlock,ProgramBlock desBlock){
        //若捕捉块与目标块不属于同一类，则返回false
        if ((capturedBlock.getViews().get(0) instanceof ProgramLightInterface && !(desBlock.getViews().get(0) instanceof ProgramLightInterface))
                ||(capturedBlock.getViews().get(0) instanceof ProgramMoveInterface && !(desBlock.getViews().get(0) instanceof ProgramMoveInterface))){
            return false;
        }

        //若目标块之前未处于插入状态
        if (!desBlock.isInserting()) {
            //若目标块可插入到捕捉块中
            if (haveOverlap(desBlock.getTopRaisedArea(), globalToNative(capturedBlock.getBottomConcaveArea()))) {
                capturedBlock.setNextBlock(desBlock);
            }
            //若捕捉块可插入到目标块中
            else if (haveOverlap(desBlock.getBottomConcaveArea(), globalToNative(capturedBlock.getTopRaisedArea()))) {
                capturedBlock.setPreBlock(desBlock);
            } else {
                return desBlock.isInserting();
            }
            desBlock.changeState(ProgramBlock.State.INSERTED);
            desBlock.setInserting(true);
            return true;
        } else {
            if (!haveOverlap(desBlock.getTopRaisedArea(), globalToNative(capturedBlock.getBottomConcaveArea()))
                    && !haveOverlap(desBlock.getBottomConcaveArea(), globalToNative(capturedBlock.getTopRaisedArea()))) {
                desBlock.changeState(ProgramBlock.State.NORMAL);
                capturedBlock.setPreBlock(null);
                capturedBlock.setNextBlock(null);
                desBlock.setInserting(false);
            }
        }
        return desBlock.isInserting();
    }

    //判断区域是否有重叠点
    private boolean haveOverlap(Rect area1, Rect area2) {
        return !(area1.left > area2.right
                || area1.right < area2.left
                || area1.top > area2.bottom
                || area1.bottom < area2.top);

    }

    private void alignBlock(ProgramBlock block, int dx, int dy) {
        if (block.getPreBlock() != null) {
            moveToBottom(block, block.getPreBlock());
            move(block, dx, dy);
        } else if (block.getNextBlock() != null) {
            moveToTop(block, block.getNextBlock());
            move(block, dx, dy);
        }
    }

    private void releaseBlock(ProgramBlock block) {
        ProgramBlock preBlock = block.getPreBlock();
        ProgramBlock nextBlock = block.getNextBlock();

        if (preBlock != null) {
            preBlock.add(block);
            preBlock.changeState(ProgramBlock.State.NORMAL);
            preBlock.setInserting(false);
        } else if (nextBlock != null) {
            nextBlock.pushHead(block);
            nextBlock.changeState(ProgramBlock.State.NORMAL);
            nextBlock.setInserting(false);
        } else {
            block.changeState(ProgramBlock.State.NORMAL);
        }
    }

    private void moveToBottom(ProgramBlock originBlock, ProgramBlock desBlock) {
        Rect area1 = originBlock.getTopRaisedArea();
        Rect area2 = desBlock.getBottomConcaveArea();

        int dx = area2.left - area1.left;
        int dy = area2.top - area1.top - originBlock.getViews().get(0).getRadius();

        move(originBlock, dx, dy);
    }

    private void move(ProgramBlock block, int dx, int dy) {
        for (ProgramView view : block.getViews()) {
            view.layout(view.getLeft() + dx, view.getTop() + dy, view.getRight() + dx, view.getBottom() + dy);
        }
    }

    private void init(Context context) {
        mContext = context;
        mLayout = new FrameLayout(context);
        addView(mLayout);
    }

    private Rect globalToNative(Rect rect) {
        Rect result = new Rect();
        result.left = rect.left - getLeft() + getScrollX();
        result.top = rect.top - getTop() + getScrollY();
        result.right = rect.right - getLeft() + getScrollX();
        result.bottom = rect.bottom - getTop() + getScrollY();

        return result;
    }
}
