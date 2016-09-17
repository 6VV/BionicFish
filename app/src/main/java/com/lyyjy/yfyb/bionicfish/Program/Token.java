package com.lyyjy.yfyb.bionicfish.Program;

/**
 * Created by Administrator on 2016/6/30.
 */
public class Token {
    private int mLineNumber=0;
    private String mText="";
    private int mStartIndex=0;
    private int mEndIndex=0;

    public Token(String text, int lineNumber, int startIndex, int endIndex){
        mText=text;
        mLineNumber=lineNumber;
        mStartIndex=startIndex;
        mEndIndex=endIndex;
    }

    public int getLineNumber(){
        return mLineNumber;
    }

    public String getText(){
        return mText;
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public int getEndIndex() {
        return mEndIndex;
    }
}
