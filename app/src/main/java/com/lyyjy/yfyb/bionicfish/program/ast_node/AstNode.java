package com.lyyjy.yfyb.bionicfish.program.ast_node;

import com.lyyjy.yfyb.bionicfish.program.grammar_parser.GrammarParser;
import com.lyyjy.yfyb.bionicfish.program.text_program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.program.text_program.Token;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public abstract class AstNode {
    public abstract byte[] interpret();

    byte getTime(Token timeToken) throws InterpreterException {
        //noinspection StringEquality
        if (timeToken.getText().equals("\n")){
            throw new InterpreterException(timeToken, InterpreterException.ExceptionCode.TIME_CAN_NOT_BE_NULL);
        }

        int time;
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
