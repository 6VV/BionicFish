package com.lyyjy.yfyb.bionicfish.activity.helper_activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.lyyjy.yfyb.bionicfish.activity.ParentActivity;
import com.lyyjy.yfyb.bionicfish.R;

public class HelpActivity extends ParentActivity {
    //    private final int[] mQuickTour ={R.drawable.quick_tour_1,R.drawable.quick_tour_2};
//    private final int[] mOperatorManual = {R.drawable.operator_manual_1, R.drawable.operator_manual_2,R.drawable.operator_manual_3,
//            R.drawable.operator_manual_4,R.drawable.operator_manual_5,R.drawable.operator_manual_6};
//    private final int[] mSafetyInstruction = {R.drawable.safety_instruction_1, R.drawable.safety_instruction_2};
//    private final int[] mProductSpecification={R.drawable.product_specification};
    private final int[] mQuickTour = {};
    private final int[] mOperatorManual = {};
    private final int[] mSafetyInstruction = {};
    private final int[] mProductSpecification = {};

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
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.action_quick_tour: {
                setContentView(new HelpManualLayout(this, mQuickTour));
            }
            break;
            case R.id.action_operating_instruction: {
                setContentView(new HelpManualLayout(this, mOperatorManual));
            }
            break;
            case R.id.action_safety_instruction: {
                setContentView(new HelpManualLayout(this, mSafetyInstruction));
            }
            break;
            case R.id.action_product_specification: {
                setContentView(new HelpManualLayout(this, mProductSpecification));
            }
            break;
        }
        return true;
    }
}
