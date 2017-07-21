package com.lyyjy.yfyb.bionicfish.program.grammar_parser;


import com.lyyjy.yfyb.bionicfish.program.ast_node.AstNode;
import com.lyyjy.yfyb.bionicfish.program.ast_node.ProgramNode;
import com.lyyjy.yfyb.bionicfish.program.text_program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.program.text_program.TokenParser;

/**
 * Created by Administrator on 2016/7/13.
 */
@SuppressWarnings("DefaultFileTemplate")
public abstract class GrammarParserAbstract {
    public static final int MAX_TIME=255;
    public static final int MIN_TIME=0;
    public static final int INIT_TIME=30;

    final ProgramNode mRootNode=new ProgramNode();

    public abstract void parse(TokenParser parser) throws InterpreterException;

    public AstNode getRootNode(){
        return mRootNode;
    }
}
