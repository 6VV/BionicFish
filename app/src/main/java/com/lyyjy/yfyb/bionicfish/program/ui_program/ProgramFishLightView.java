package com.lyyjy.yfyb.bionicfish.program.ui_program;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/8.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramFishLightView extends ProgramView implements ProgramLightInterface{
    private Spinner mLightSpinner;
    private EditText mTimeEditText;

    @Override
    public ProgramView clone(Context context) {
        ProgramFishLightView result=new ProgramFishLightView(context);

        result.mLightSpinner.setSelection(mLightSpinner.getSelectedItemPosition());
        result.mTimeEditText.setText(mTimeEditText.getText().toString());

        return result;
    }

    public ProgramFishLightView(Context context) {
        super(context);
        init(context);
    }

    public ProgramFishLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void parseText(String executeText) {
        String[] texts=executeText.split(" ");
        setSpinnerText(mLightSpinner, texts[1]);
        mTimeEditText.setText(texts[2]);
    }

    @Override
    public String executeText() {
        int time= (int) (Double.parseDouble(mTimeEditText.getText().toString())*10);
        return "LIGHT " + mLightSpinner.getSelectedItem().toString() + " " + String.valueOf(time);
//        return "LIGHT "+mLightSpinner.getSelectedItem().toString()+" "+mTimeEditText.getText().toString();
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_program_fish_light, this);
//        addView(view);

        mProgramViewBackground = (ProgramFishBackground) view.findViewById(R.id.custom_view);
        mLightSpinner= (Spinner) view.findViewById(R.id.light_spinner);
        mTimeEditText= (EditText) view.findViewById(R.id.time_edit_text);
        mTimeEditText.addTextChangedListener(new TimeLengthTextWatcher(mTimeEditText));

        ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,new String[]{
                getResources().getString(R.string.black),
                getResources().getString(R.string.blue),
                getResources().getString(R.string.green),
                getResources().getString(R.string.cyan),
                getResources().getString(R.string.red),
                getResources().getString(R.string.magenta),
                getResources().getString(R.string.yellow),
                getResources().getString(R.string.white),
        });
//        Shader shader = new LinearGradient(0, 0, 40, 60, new int[] {
//                Color.argb(200,100,100,255), Color.argb(200,100,255,100), Color.argb(100,100,255,255),
//                Color.argb(200,255,100,100), Color.argb(200,255,100,255), Color.argb(100,255,255,100),
//                Color.argb(200,255,255,255), Color.argb(200,100,100,100)}, null,
//                Shader.TileMode.REPEAT);
//        mProgramViewBackground.setBackgroundShader(shader);
        mLightSpinner.setAdapter(adapter);

        LinearLayout layout= (LinearLayout) view.findViewById(R.id.widget_layout);
        for (int i=0;i<layout.getChildCount();++i){
            mChildViews.add(layout.getChildAt(i));
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        mLightSpinner.setFocusable(false);
        mTimeEditText.setFocusable(false);
    }
}
