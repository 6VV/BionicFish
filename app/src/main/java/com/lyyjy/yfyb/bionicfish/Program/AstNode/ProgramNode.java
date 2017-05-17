package com.lyyjy.yfyb.bionicfish.Program.AstNode;

import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by Administrator on 2016/6/30.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ProgramNode extends AstNode {
    private final Vector<AstNode> mChildNodes=new Vector<>();

    @Override
    public byte[] interpret() {
        Vector<byte[]> data=new Vector<>();
        int length=0;
        for (AstNode childNode : mChildNodes) {
            byte[] bytes=childNode.interpret();
            data.add(bytes);
            length+=bytes.length;
        }

        ByteBuffer result=ByteBuffer.allocate(length);
        for (byte[] childBytes :data) {
            result.put(childBytes);
        }

        return result.array();
    }

    public void AddChild(AstNode childNode){
        mChildNodes.add(childNode);
    }

}
