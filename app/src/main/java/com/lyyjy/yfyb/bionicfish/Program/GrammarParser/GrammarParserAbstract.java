package com.lyyjy.yfyb.bionicfish.Program.GrammarParser;


import com.lyyjy.yfyb.bionicfish.Program.AstNode.AstNode;
import com.lyyjy.yfyb.bionicfish.Program.AstNode.ProgramNode;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.TokenParser;

/**
 * Created by Administrator on 2016/7/13.
 */
public abstract class GrammarParserAbstract {
    public static final int MAX_TIME=255;
    public static final int MIN_TIME=0;
    public static final int INIT_TIME=30;

    protected ProgramNode mRootNode=new ProgramNode();

    public abstract void parse(TokenParser parser) throws InterpreterException;

    public AstNode getRootNode(){
        return mRootNode;
    }
}
