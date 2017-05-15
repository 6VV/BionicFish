package com.lyyjy.yfyb.bionicfish.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.DataPersistence.ProgramFileManager;
import com.lyyjy.yfyb.bionicfish.Light.LightColor;
import com.lyyjy.yfyb.bionicfish.Program.ProgramSender;
import com.lyyjy.yfyb.bionicfish.Program.UiProgram.DragLayout;
import com.lyyjy.yfyb.bionicfish.Program.UiProgram.LayoutCreateView;
import com.lyyjy.yfyb.bionicfish.Program.UiProgram.LayoutDisplayView;
import com.lyyjy.yfyb.bionicfish.Program.UiProgram.ProgramXmlPersistence;
import com.lyyjy.yfyb.bionicfish.R;

import java.util.HashMap;

public class UiProgramActivity extends ParentActivity {

    private static final String TAG=UiProgramActivity.class.getSimpleName();
    private HashMap<String,String> mCommandMap;

    private LayoutDisplayView mLayoutDisplayView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_program);
        initCommandMap();

        LayoutCreateView layoutCreateView= (LayoutCreateView) findViewById(R.id.layout_create_view);
        mLayoutDisplayView= (LayoutDisplayView) findViewById(R.id.layout_display_view);
        ((DragLayout) findViewById(R.id.layout_drag_view)).setLayoutCreateView(layoutCreateView);
        ((DragLayout) findViewById(R.id.layout_drag_view)).setLayoutDisplayView(mLayoutDisplayView);
    }

    private void initCommandMap() {
        mCommandMap=new HashMap<String,String>(){
            {
                put(getResources().getString(R.string.up),"UP");
                put(getResources().getString(R.string.left),"LEFT");
                put(getResources().getString(R.string.right),"RIGHT");
                put(getResources().getString(R.string.wait),"WAIT");
                put(getResources().getString(R.string.black), LightColor.COLOR_BLACK_NAME);
                put(getResources().getString(R.string.blue),LightColor.COLOR_BLUE_NAME);
                put(getResources().getString(R.string.green),LightColor.COLOR_GREEN_NAME);
                put(getResources().getString(R.string.cyan),LightColor.COLOR_CYAN_NAME);
                put(getResources().getString(R.string.red),LightColor.COLOR_RED_NAME);
                put(getResources().getString(R.string.magenta),LightColor.COLOR_MAGENTA_NAME);
                put(getResources().getString(R.string.yellow),LightColor.COLOR_YELLOW_NAME);
                put(getResources().getString(R.string.white),LightColor.COLOR_WHITE_NAME);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ui_program,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
            case R.id.menu_execute:{
                String movementText=translateText(mLayoutDisplayView.executeMoveText());
                String lightText=translateText(mLayoutDisplayView.executeLightText());

                ProgramSender programSender=new ProgramSender(this);
                programSender.sendData(movementText,lightText);
            }break;
            case R.id.menu_simulation:{
                startActivity(SimulationActivity.newIntent(UiProgramActivity.this,mLayoutDisplayView.executeMoveText(),mLayoutDisplayView.executeLightText()));
            }break;
            case R.id.menu_clear:{
                mLayoutDisplayView.clearView();
            }break;
            case R.id.menu_save:{
                showSaveDialog();
            }break;
            case R.id.menu_load:{
                showLoadDialog();
            }break;
            case R.id.menu_remove:{
                showRemoveDialog();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSaveDialog() {
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        LinearLayout layout= (LinearLayout) layoutInflater.inflate(R.layout.dialog_save_file,null);
        final EditText etFile=(EditText) layout.findViewById(R.id.etFileName);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("保存文件");
        builder.setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName=etFile.getText().toString();
                if (fileName.length()==0){
                    Toast.makeText(UiProgramActivity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    ProgramXmlPersistence xmlPersistence=new ProgramXmlPersistence(UiProgramActivity.this);
                    String[] fileNames=xmlPersistence.getFileNames();
                    boolean isFindFile=false;
                    if (fileNames!=null) {
                        for (String name : fileNames) {
                            if (name.equals(fileName)) {
                                isFindFile = true;
                                break;
                            }
                        }
                    }
                    if (isFindFile){
                        showCoverDialog(fileName);
                    }else{
                        mLayoutDisplayView.saveFile(fileName);
                    }
                }
            }

            private void showCoverDialog(final String fileName) {
                AlertDialog.Builder coverDialog=new AlertDialog.Builder(UiProgramActivity.this);
                coverDialog.setTitle("文件名重复");
                coverDialog.setMessage("已存在文件:"+fileName+"\n是否覆盖");
                coverDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLayoutDisplayView.saveFile(fileName);
                    }
                });
                coverDialog.setNegativeButton("取消",null);
                coverDialog.show();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void showLoadDialog() {
        final ProgramXmlPersistence fileManager=new ProgramXmlPersistence(this);
        final String[] fileNames = fileManager.getFileNames();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("加载文件");
        builder.setSingleChoiceItems(fileNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileNames[0] =fileNames[which];
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (fileNames==null || fileNames.length==0){
                    Toast.makeText(UiProgramActivity.this,"请选择一个文件",Toast.LENGTH_SHORT).show();
                }else {
                    mLayoutDisplayView.loadFile(fileNames[0]);
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void showRemoveDialog() {
        final ProgramXmlPersistence fileManager=new ProgramXmlPersistence(this);
        final String[] fileNames = fileManager.getFileNames();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("删除文件");
        builder.setSingleChoiceItems(fileNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileNames[0] =fileNames[which];
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (fileNames==null || fileNames.length==0){
                    Toast.makeText(UiProgramActivity.this,"请选择一个文件",Toast.LENGTH_SHORT).show();
                }else {
                    fileManager.removeFile(fileNames[0]);
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private String translateText(String text){
        StringBuilder stringBuilder=new StringBuilder();

        String[] lineTexts = text.split("\n");

        //从第2行开始
        for (int i=1;i<lineTexts.length;++i){
            String[] commands = lineTexts[i].split(" ");
            StringBuilder newLineText=new StringBuilder();

            //更新一行的命令
            for (int j=0;j<commands.length;++j){
                String newCommand = mCommandMap.get(commands[j]);
                if (newCommand!=null){
                    newLineText.append(newCommand);
                }else{
                    newLineText.append(commands[j]);
                }
                newLineText.append(" ");
            }

            stringBuilder.append(newLineText.toString()+"\n");
        }

        return stringBuilder.toString();
    }
}
