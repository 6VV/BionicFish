package com.lyyjy.yfyb.bionicfish.program.ast_node;

import com.lyyjy.yfyb.bionicfish.program.text_program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.program.text_program.ProgramCommand;
import com.lyyjy.yfyb.bionicfish.program.text_program.Token;
import com.lyyjy.yfyb.bionicfish.program.text_program.TokenParser;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public class DirectionNode extends AstNode {
    private byte mSpeed;
    private byte mTime;
    private final String mCommandCode;

    public DirectionNode(TokenParser parser) throws InterpreterException {
        Token tokenCommand=parser.getLastToken();
        Token tokenSpeed=parser.getToken();
        Token tokenTime=parser.getToken();

        mCommandCode=tokenCommand.getText();

        addSpeedToken(tokenSpeed);
        mTime=getTime(tokenTime);
    }

    private void addSpeedToken(Token tokenSpeed) throws InterpreterException {
        //noinspection StringEquality
        if (tokenSpeed.getText().equals("\n")){
            throw new InterpreterException(tokenSpeed, InterpreterException.ExceptionCode.SPEED_CAN_NOT_BE_NULL);
        }
        try{
            mSpeed= (byte) Integer.parseInt(tokenSpeed.getText());
        }catch (NumberFormatException e){
            throw new InterpreterException(tokenSpeed, InterpreterException.ExceptionCode.SPEED_SHOULD_BE_INTEGER);
        }
    }

    @Override
    public byte[] interpret() {
        ByteBuffer result=ByteBuffer.allocate(3);

        switch (mCommandCode){
            case ProgramCommand.COMMAND_UP:{
                result.put((byte)0x01);
            }break;
            case ProgramCommand.COMMAND_LEFT:{
                result.put((byte)0x02);
            }break;
            case ProgramCommand.COMMAND_RIGHT:{
                result.put((byte)0x03);
            }break;
        }

        result.put(mSpeed);
        result.put(mTime);

        return result.array();
    }


}
