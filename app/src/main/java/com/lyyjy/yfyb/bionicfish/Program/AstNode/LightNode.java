package com.lyyjy.yfyb.bionicfish.Program.AstNode;


import com.lyyjy.yfyb.bionicfish.Light.LightColor;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.Token;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.TokenParser;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;

/**
 * Created by Administrator on 2016/6/30.
 */
public class LightNode extends AstNode {
    private byte mColor=0;
    private byte mTime=0;

    public LightNode(TokenParser parser) throws InterpreterException {
        Token colorToken=parser.getToken();
        Token timeToken=parser.getToken();

        addColorToken(colorToken);

        mTime= getTime(timeToken);
    }

    private void addColorToken(Token colorToken) throws InterpreterException {
        if (colorToken.getText()=="\n"){
            throw new InterpreterException(colorToken, InterpreterException.ExceptionCode.COLOR_CAN_NOT_BE_NULL);
        }

        Integer color= LightColor.COLOR_MAP.get(colorToken.getText());
        if (color==null){
            throw new InterpreterException(colorToken, InterpreterException.ExceptionCode.WRONG_COLOR_NAME);
        }else{
            mColor=new LightColor(color).getByteColor();
        }
    }

    @Override
    public byte[] interpret() {
        byte[] result=new byte[3];
        result[0]= CommandManager.REQUEST_COLOR;
        result[1]=mColor;
        result[2]=mTime;
        return result;
    }
}
