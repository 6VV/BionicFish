package com.lyyjy.yfyb.bionicfish.Program.TextProgram;

import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParser;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public class InterpreterException extends Exception{
    public enum ExceptionCode{
        WRONG_GRAMMAR,
        WRONG_COLOR_NAME,
        SPEED_SHOULD_BE_INTEGER,
        TIME_SHOULD_BE_INTEGER,
        UNKNOWN_COMMAND,
        COMMAND_LINEBREAK,
        COLOR_CAN_NOT_BE_NULL,
        TIME_CAN_NOT_BE_NULL,
        SPEED_CAN_NOT_BE_NULL,
        TIME_SHOULD_BE_WITHIN_LIMITS,
    }

    private final HashMap<ExceptionCode,String> CODE_MAP=new HashMap<ExceptionCode,String>(){
        {
            put(ExceptionCode.WRONG_GRAMMAR,"语法错误");
            put(ExceptionCode.SPEED_SHOULD_BE_INTEGER,"速度值需为整数");
            put(ExceptionCode.TIME_SHOULD_BE_INTEGER,"时间值需为整数");
            put(ExceptionCode.WRONG_COLOR_NAME,"错误的颜色值");
            put(ExceptionCode.UNKNOWN_COMMAND,"未知的命令");
            put(ExceptionCode.COMMAND_LINEBREAK,"命令需以换行符结尾");
            put(ExceptionCode.COLOR_CAN_NOT_BE_NULL,"颜色不能为空");
            put(ExceptionCode.TIME_CAN_NOT_BE_NULL,"时间不能为空");
            put(ExceptionCode.SPEED_CAN_NOT_BE_NULL,"速度不能为空");
            put(ExceptionCode.TIME_SHOULD_BE_WITHIN_LIMITS,"时间值需在"+ GrammarParser.MIN_TIME+"和"+GrammarParser.MAX_TIME+"范围内");
        }
    };

    private final Token mToken;
    private final ExceptionCode mCode;

    public InterpreterException(Token token, ExceptionCode code){
        mToken =token;
        mCode=code;
    }

    public String getInformation(){
        if (mToken==null){
            return "信息："+CODE_MAP.get(mCode);
        }

        return "行数："+String.valueOf(mToken.getLineNumber())+"\n"
                +"文本："+mToken.getText()+"\n"
                +"信息："+CODE_MAP.get(mCode);
    }

    public Token getToken(){
        return mToken;
    }
}
