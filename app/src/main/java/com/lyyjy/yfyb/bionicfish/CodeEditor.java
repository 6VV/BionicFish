package com.lyyjy.yfyb.bionicfish;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParserAbstract;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.InterpreterException;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.ProgramCommand;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.Token;
import com.lyyjy.yfyb.bionicfish.Program.TextProgram.TokenParser;


/**
 * Created by Administrator on 2016/7/7.
 */
@SuppressWarnings("DefaultFileTemplate")
public class CodeEditor extends android.support.v7.widget.AppCompatEditText {
    private GrammarParserAbstract mGrammarParser=new GrammarParser();

    public CodeEditor(Context context) {
        super(context);
        init();
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setGrammarParser(GrammarParserAbstract grammarParser){
        mGrammarParser=grammarParser;
    }
    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        addTextChangedListener(getTextWatcher());
    }

    public void format(){
        setStringSpan(getText().toString());
    }

    private int mStartIndex=0;
    @NonNull
    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getText().length() == 0) {
                    return;
                }

                setStringSpan(getSpanString(s, start, before, count));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void setStringSpan(String newString) {
        if (newString.length()==0){
            return;
        }
        TokenParser parser = new TokenParser(newString.toUpperCase());
        for (Token token = parser.getToken(); token != null; token = parser.getToken()) {
            if (ProgramCommand.COMMANDS.get(token.getText()) != null) {
                updateSpan(new ForegroundColorSpan(Color.BLUE), token);
            }else if (ProgramCommand.KEYWORDS.contains(token.getText())){
                updateSpan(new ForegroundColorSpan(Color.rgb(221,160,221)),token);
            }else{
                updateSpan(new ForegroundColorSpan(Color.DKGRAY),token);
            }
        }

        try {
            mGrammarParser.parse(parser);
        } catch (InterpreterException e) {
            updateSpan(new ForegroundColorSpan(Color.RED), e.getToken());
        }
    }

    @NonNull
    private String getSpanString(CharSequence s, int start, int before, int count) {
        mStartIndex = start;
        int endIndex = start + count - before;
        endIndex=endIndex<0?0:endIndex;

        if (mStartIndex > endIndex) {
            int tem = mStartIndex;
            mStartIndex = endIndex;
            endIndex = tem;
        }

        //noinspection StatementWithEmptyBody
        for (; mStartIndex > 0 && s.charAt(mStartIndex) != '\n'; --mStartIndex);
        //noinspection StatementWithEmptyBody
        for (; endIndex < s.length() - 1 && s.charAt(endIndex) != '\n'; ++endIndex);

        return getText().toString().substring(mStartIndex, endIndex);
    }

    private void updateSpan(Object what, Token token) {
        if (token.getStartIndex()==token.getEndIndex()){
            return;
        }
        getEditableText().setSpan(what,
                mStartIndex+token.getStartIndex(),
                mStartIndex+token.getEndIndex(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineNumberWidth = drawLineNumber(canvas);
        drawDividingLine(canvas, lineNumberWidth);

        super.onDraw(canvas);
    }

    private void drawDividingLine(Canvas canvas, int lineNumberWidth) {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(2);

        canvas.drawLine(lineNumberWidth + 10, 0, lineNumberWidth + 10, getLineHeight() * getLineCount() + getHeight(), linePaint);
        canvas.save();
        canvas.restore();
    }

    private int drawLineNumber(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(getTextSize());

        for (int i = 0; i < getLineCount(); ++i) {
            float y = (i + 1) * getLineHeight() - (getLineHeight() - getBaseline());
            canvas.drawText(String.valueOf(i + 1), 0, y, paint);
            canvas.save();
        }

        int lineNumberWidth = (int) paint.measureText(String.valueOf(getLineCount() + 1));
        setPadding(lineNumberWidth + 20, 0, 0, 0);

        return lineNumberWidth;
    }
}
