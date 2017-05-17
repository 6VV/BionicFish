package com.lyyjy.yfyb.bionicfish.Program.TextProgram;

import java.util.Vector;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public class TokenParser {
    private final Vector<Token> mTokenList=new Vector<>();
    private int mCharIndex =0;
    private int mTokenIndex=0;
    @SuppressWarnings("FieldCanBeLocal")
    private int mLineNumber=1;

    public TokenParser(String text) {
        while (mCharIndex <text.length()){
            skipBlankWithoutLR(text);
            if (mCharIndex>=text.length()){
                break;
            }
            if (text.charAt(mCharIndex)=='\n'){
                mTokenList.add(new Token("\n",mLineNumber,mCharIndex,mCharIndex));
                ++mLineNumber;
                ++mCharIndex;
            }else{
                String tokenText="";
                while (mCharIndex<text.length() && !isBlank(text.charAt(mCharIndex))){
                    tokenText+=text.charAt(mCharIndex);
                    ++mCharIndex;
                }
                mTokenList.add(new Token(tokenText,mLineNumber,mCharIndex-tokenText.length(),mCharIndex));
            }
        }
        mTokenList.add(new Token("\n",mLineNumber,mCharIndex,mCharIndex));
    }

    public void resetTokenIndex(){
        mTokenIndex=0;
    }
    public Token getToken(){
        if (mTokenIndex >=mTokenList.size()){
            return null;
        }else{
            return mTokenList.elementAt(mTokenIndex++);
        }
    }

    public Token getLastToken(){
        if (mTokenIndex-1 <0){
            return null;
        }else{
            return mTokenList.elementAt(mTokenIndex-1);
        }
    }

    private boolean isBlank(char c){
        return c<=' ';
    }

    private boolean isBlankWithoutLR(char c){
        return isBlank(c) && c!='\n';
    }

    private void skipBlankWithoutLR(String text){
        while (mCharIndex<text.length()&&isBlankWithoutLR(text.charAt(mCharIndex))){
            ++mCharIndex;
        }
    }

}
