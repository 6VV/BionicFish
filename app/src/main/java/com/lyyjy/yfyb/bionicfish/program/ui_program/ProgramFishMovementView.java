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
 * Created by Administrator on 2017/2/6.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramFishMovementView extends ProgramView implements ProgramMoveInterface{
    @SuppressWarnings("unused")
    private static final String TAG = ProgramFishMovementView.class.getSimpleName();

    private Spinner mDirectionSpinner;
    private Spinner mSpeedSpinner;
    private EditText mTimeEditText;

    @Override
    public ProgramView clone(Context context) {
        ProgramFishMovementView result=new ProgramFishMovementView(context);
        result.mDirectionSpinner.setSelection(mDirectionSpinner.getSelectedItemPosition());
        result.mSpeedSpinner.setSelection(mSpeedSpinner.getSelectedItemPosition());
        result.mTimeEditText.setText(mTimeEditText.getText().toString());
        return result;
    }

    public ProgramFishMovementView(Context context) {
        super(context);
        init(context);
    }

    public ProgramFishMovementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void parseText(String executeText) {
        String[] texts=executeText.split(" ");
        setSpinnerText(mDirectionSpinner,texts[0]);
        setSpinnerText(mSpeedSpinner,texts[1]);
        mTimeEditText.setText(texts[2]);
    }

    @Override
    public String executeText() {
        return mDirectionSpinner.getSelectedItem().toString()+" "+mSpeedSpinner.getSelectedItem().toString()+" "+mTimeEditText.getText().toString();
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_program_fish_movement, this);
//        addView(view);

        mProgramViewBackground = (ProgramFishBackground) view.findViewById(R.id.custom_view);
        mDirectionSpinner= (Spinner) view.findViewById(R.id.direction_spinner);
        mSpeedSpinner= (Spinner) view.findViewById(R.id.speed_spinner);
        mTimeEditText= (EditText) view.findViewById(R.id.time_edit_text);
        mTimeEditText.addTextChangedListener(new TimeLengthTextWatcher(mTimeEditText));

        ArrayAdapter<String> adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,new String[]{
                getResources().getString(R.string.up),
                getResources().getString(R.string.right),
                getResources().getString(R.string.left),
        });
        mDirectionSpinner.setAdapter(adapter);

        ArrayAdapter<String> speedAdapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,new String[]{"1","2","3","4"});
        mSpeedSpinner.setAdapter(speedAdapter);

        mProgramViewBackground.setBackgroundShader(ProgramViewBackground.SHADER_MOVEMENT);

        LinearLayout layout= (LinearLayout) view.findViewById(R.id.widget_layout);
        for (int i=0;i<layout.getChildCount();++i){
            mChildViews.add(layout.getChildAt(i));
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        mDirectionSpinner.setFocusable(false);
        mSpeedSpinner.setFocusable(false);
        mTimeEditText.setFocusable(false);
    }
}
