package com.lyyjy.yfyb.bionicfish.Program.GrammarParser;


import com.lyyjy.yfyb.bionicfish.Program.AstNode.LightNode;
import com.lyyjy.yfyb.bionicfish.Program.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.ProgramCommand;
import com.lyyjy.yfyb.bionicfish.Program.Token;
import com.lyyjy.yfyb.bionicfish.Program.TokenParser;

/**
 * Created by Administrator on 2016/7/13.
 */
public class LightGrammarParser extends GrammarParserAbstract {
    @Override
    public void parse(TokenParser parser) throws InterpreterException {
        parser.resetTokenIndex();
        Token currentToken;
        while ((currentToken=parser.getToken())!=null) {
            switch (currentToken.getText()) {
                case ProgramCommand.COMMAND_LIGHT: {
                    mRootNode.AddChild(new LightNode(parser));
                }
                break;
                case "\n":continue;
                default:{
                    throw new InterpreterException(currentToken, InterpreterException.ExceptionCode.UNKNOWN_COMMAND);
                }
            }
            if (parser.getToken().getText()!="\n"){
                throw new InterpreterException(currentToken,InterpreterException.ExceptionCode.COMMAND_LINEBREAK);
            }
        }
    }
}
