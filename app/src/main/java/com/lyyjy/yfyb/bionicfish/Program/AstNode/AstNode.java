package com.lyyjy.yfyb.bionicfish.Program.AstNode;

import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.Token;

/**
 * Created by Administrator on 2016/6/30.
 */
public abstract class AstNode {
    public abstract byte[] interpret();

    protected byte getTime(Token timeToken) throws InterpreterException {
        if (timeToken.getText()=="\n"){
            throw new InterpreterException(timeToken, InterpreterException.ExceptionCode.TIME_CAN_NOT_BE_NULL);
        }

        int time=0;
        try {
            time= Integer.parseInt(timeToken.getText());
            if (time< GrammarParser.MIN_TIME || time>GrammarParser.MAX_TIME){
                throw new InterpreterException(timeToken, InterpreterException.ExceptionCode.TIME_SHOULD_BE_WITHIN_LIMITS);
            }
        }catch (NumberFormatException e){
            throw new InterpreterException(timeToken, InterpreterException.ExceptionCode.TIME_SHOULD_BE_INTEGER);
        }

        return (byte)time;
    }
//    public static byte[] intToByte4(int i) {
//        byte[] targets = new byte[4];
//        targets[3] = (byte) (i & 0xFF);
//        targets[2] = (byte) (i >> 8 & 0xFF);
//        targets[1] = (byte) (i >> 16 & 0xFF);
//        targets[0] = (byte) (i >> 24 & 0xFF);
//        return targets;
//    }
}
