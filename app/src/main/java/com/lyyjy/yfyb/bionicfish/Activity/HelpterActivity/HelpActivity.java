package com.lyyjy.yfyb.bionicfish.Activity.HelpterActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.lyyjy.yfyb.bionicfish.Activity.ParentActivity;
import com.lyyjy.yfyb.bionicfish.R;

public class HelpActivity extends ParentActivity  {
    private int[] mQuickTour ={R.mipmap.quick_tour_1,R.mipmap.quick_tour_2};
    private int[] mOperatorManual = {R.mipmap.operator_manual_1, R.mipmap.operator_manual_2,R.mipmap.operator_manual_3,
            R.mipmap.operator_manual_4,R.mipmap.operator_manual_5,R.mipmap.operator_manual_6};
    private int[] mSafetyInstruction = {R.mipmap.safety_instruction_1, R.mipmap.safety_instruction_2};
    private int[] mProductSpecification={R.mipmap.product_specification};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(new HelpManualLayout(this, mOperatorManual));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
            case R.id.action_quick_tour:{
                setContentView(new HelpManualLayout(this, mQuickTour));
            }break;
            case R.id.action_operating_instruction:{
                setContentView(new HelpManualLayout(this, mOperatorManual));
            }break;
            case R.id.action_safety_instruction:{
                setContentView(new HelpManualLayout(this, mSafetyInstruction));
            }break;
            case R.id.action_product_specification:{
                setContentView(new HelpManualLayout(this, mProductSpecification));
            }break;
        }
        return true;
    }
}
