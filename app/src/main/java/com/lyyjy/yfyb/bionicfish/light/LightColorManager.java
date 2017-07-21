package com.lyyjy.yfyb.bionicfish.light;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.remote.CommandManager;

/**
 * Created by Administrator on 2016/8/10.
 */
@SuppressWarnings("DefaultFileTemplate")
public class LightColorManager {
    private final Context mContext;

    public LightColorManager(Context context){
        mContext=context;
    }

    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("设置灯光颜色");
        LayoutInflater inflater=LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.dialog_choose_color, null);
        final LightColorSpinner colorSpinner= (LightColorSpinner) layout.findViewById(R.id.spinnerLightColor);
        builder.setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                byte color=colorSpinner.getColor(colorSpinner.getSelectedItemPosition()).getByteColor();
                CommandManager.sendColor(color);
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }
}
