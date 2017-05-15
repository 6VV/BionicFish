package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/2/14.
 */

public class TimeLengthTextWatcher implements TextWatcher {
    private EditText mEditText;

    TimeLengthTextWatcher(EditText editText){
        mEditText=editText;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String text=mEditText.getText().toString();
        if (text.length()!=0){
            double result= Double.parseDouble(text);
            if (result>25.5){
                mEditText.setText(String.valueOf(25.5));
            }else if (result<0){
                mEditText.setText(String.valueOf(0));
            }
        }else{
            mEditText.setText(String.valueOf(0));
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
