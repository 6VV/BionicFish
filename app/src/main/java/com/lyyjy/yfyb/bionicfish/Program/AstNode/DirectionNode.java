package com.lyyjy.yfyb.bionicfish.Program.AstNode;

import com.lyyjy.yfyb.bionicfish.Program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.ProgramCommand;
import com.lyyjy.yfyb.bionicfish.Program.Token;
import com.lyyjy.yfyb.bionicfish.Program.TokenParser;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/6/30.
 */
public class DirectionNode extends AstNode {
    private byte mSpeed;
    private byte mTime;
    private String mCommandCode;


    public DirectionNode(TokenParser parser) throws InterpreterException {
        Token tokenCommand=parser.getLastToken();
        Token tokenSpeed=parser.getToken();
        Token tokenTime=parser.getToken();

        mCommandCode=tokenCommand.getText();

        addSpeedToken(tokenSpeed);
        mTime=getTime(tokenTime);
    }

    private void addSpeedToken(Token tokenSpeed) throws InterpreterException {
        if (tokenSpeed.getText()=="\n"){
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
                result.put(CommandManager.STATUS_FISH_UP);
            }break;
            case ProgramCommand.COMMAND_LEFT:{
                result.put(CommandManager.STATUS_FISH_LEFT);
            }break;
            case ProgramCommand.COMMAND_RIGHT:{
                result.put(CommandManager.STATUS_FISH_RIGHT);
            }break;
        }

        result.put(mSpeed);
        result.put(mTime);

        return result.array();
    }


}
