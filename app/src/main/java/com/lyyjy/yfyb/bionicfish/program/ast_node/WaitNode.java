package com.lyyjy.yfyb.bionicfish.program.ast_node;

import com.lyyjy.yfyb.bionicfish.program.text_program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.program.text_program.Token;
import com.lyyjy.yfyb.bionicfish.program.text_program.TokenParser;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public class WaitNode extends AstNode {
    private byte mTime=0;

    public WaitNode(TokenParser parser) throws InterpreterException {
        Token timeToken=parser.getToken();

        mTime=getTime(timeToken);
    }

    @Override
    public byte[] interpret() {
        ByteBuffer result=ByteBuffer.allocate(3);

        result.put((byte)0x00);
        result.put(new byte[1]);
        result.put(mTime);

        return result.array();
    }
}
