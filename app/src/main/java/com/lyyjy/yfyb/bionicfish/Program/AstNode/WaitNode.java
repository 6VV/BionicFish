package com.lyyjy.yfyb.bionicfish.Program.AstNode;

import com.lyyjy.yfyb.bionicfish.Program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.Token;
import com.lyyjy.yfyb.bionicfish.Program.TokenParser;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/6/30.
 */
public class WaitNode extends AstNode {
    private byte mTime=0;

    public WaitNode(TokenParser parser) throws InterpreterException {
        Token timeToken=parser.getToken();

        mTime=getTime(timeToken);
    }

    @Override
    public byte[] interpret() {
        ByteBuffer result=ByteBuffer.allocate(3);

        result.put(CommandManager.STATUS_FISH_STOP);
        result.put(new byte[1]);
        result.put(mTime);

        return result.array();
    }
}
