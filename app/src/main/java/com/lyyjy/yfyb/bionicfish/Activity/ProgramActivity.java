package com.lyyjy.yfyb.bionicfish.Activity;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.CodeEditor;
import com.lyyjy.yfyb.bionicfish.DataPersistence.ProgramFileManager;
import com.lyyjy.yfyb.bionicfish.Light.LightColor;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.DirectionGrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.GrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.GrammarParser.LightGrammarParser;
import com.lyyjy.yfyb.bionicfish.Program.ProgramCommand;
import com.lyyjy.yfyb.bionicfish.Program.ProgramSender;
import com.lyyjy.yfyb.bionicfish.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ProgramActivity extends ParentActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView mTvNote;
    private LinearLayout mLayoutCommand;
    private Button mBtnConfrim;
    private Button mBtnSend;
    private CodeEditor mEtMoveProgram;
    private CodeEditor mEtLightProgram;

    private Vector<View> mCommandViewColl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        initControls();
        mCommandViewColl=new Vector<View>();
    }

    private void initControls() {
        initCommands();
        mEtLightProgram= (CodeEditor) findViewById(R.id.etLightProgram);
        mEtLightProgram.setGrammarParser(new LightGrammarParser());

        mEtMoveProgram = (CodeEditor) findViewById(R.id.etMoveProgram);
        mEtMoveProgram.setGrammarParser(new DirectionGrammarParser());

        mLayoutCommand = (LinearLayout) findViewById(R.id.layoutCommand);
        mTvNote= (TextView) findViewById(R.id.tvNote);
        mBtnConfrim= (Button) findViewById(R.id.btnConfirm);
        mBtnSend= (Button) findViewById(R.id.btnSendProgram);

        mBtnConfrim.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    private void initCommands() {
        List<String> commands=new ArrayList<String>();
        for (String command : ProgramCommand.COMMANDS.keySet()) {
            commands.add(command);
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,commands);
        Spinner spinner= (Spinner) findViewById(R.id.spinnerCommand);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                finish();
            }break;
            case R.id.action_save:{
                showSaveDialog();
            }break;
            case R.id.action_load:{
                showLoadDialog();
            }break;
            case R.id.action_remove:{
                showRemoveDialog();
            }break;
            case R.id.action_format:{
                format();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void format() {
        mEtMoveProgram.format();
        mEtLightProgram.format();
    }

    private void showRemoveDialog() {
        final String[] fileNames = ProgramFileManager.getFileNames();
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
                    Toast.makeText(ProgramActivity.this,"请选择一个文件",Toast.LENGTH_SHORT).show();
                }else {
                    ProgramFileManager.removeFile(fileNames[0]);
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void showLoadDialog() {
        final String[] fileNames = ProgramFileManager.getFileNames();
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
                    Toast.makeText(ProgramActivity.this,"请选择一个文件",Toast.LENGTH_SHORT).show();
                }else {
                    mEtMoveProgram.setText(ProgramFileManager.getFirstProgram(ProgramActivity.this,fileNames[0]));
                    mEtLightProgram.setText(ProgramFileManager.getSecondProgram(ProgramActivity.this,fileNames[0]));
                    format();
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
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
                    Toast.makeText(ProgramActivity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    String[] fileNames=ProgramFileManager.getFileNames();
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
                        saveFile(fileName);
                    }
                }
            }

            private void showCoverDialog(final String fileName) {
                AlertDialog.Builder coverDialog=new AlertDialog.Builder(ProgramActivity.this);
                coverDialog.setTitle("文件名重复");
                coverDialog.setMessage("已存在文件:"+fileName+"\n是否覆盖");
                coverDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveFile(fileName);
                    }
                });
                coverDialog.setNegativeButton("取消",null);
                coverDialog.show();
            }

            private void saveFile(String fileName) {
                ProgramFileManager.saveFile(ProgramActivity.this,fileName,mEtMoveProgram.getText().toString(),mEtLightProgram.getText().toString());
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinnerCommand:{
                String commandText=parent.getItemAtPosition(position).toString();
                mTvNote.setText(ProgramCommand.COMMANDS.get(commandText));

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
                CreateCommandView(commandText, layoutParams);

                switch (commandText){
                    case ProgramCommand.COMMAND_UP:case ProgramCommand.COMMAND_LEFT:
                    case ProgramCommand.COMMAND_RIGHT:{
                        addSpeedSpinner(layoutParams);
                        addTimeView(layoutParams);
                    }break;
                    case ProgramCommand.COMMAND_WAIT:
                    {
                        addTimeView(layoutParams);
                    }break;
                    case ProgramCommand.COMMAND_LIGHT:{
                        addLightSpinner(layoutParams);
                        addTimeView(layoutParams);
                    }break;
                }

                updateNode();
            }break;
        }
    }

    private void updateNode(){
        String command= ((TextView)mCommandViewColl.elementAt(0)).getText().toString();
        switch (command){
            case ProgramCommand.COMMAND_UP:case ProgramCommand.COMMAND_LEFT:
            case ProgramCommand.COMMAND_RIGHT:{
                String time=((EditText)mCommandViewColl.elementAt(2)).getText().toString();
                if (time.equals("0")){
                    time="无限";
                }
                mTvNote.setText(String.format(ProgramCommand.COMMANDS.get(command),
                        ((Spinner)mCommandViewColl.elementAt(1)).getSelectedItem().toString(),
                        time));
            }break;
            case ProgramCommand.COMMAND_WAIT:
            {
                mTvNote.setText(String.format(ProgramCommand.COMMANDS.get(command),
                        ((EditText)mCommandViewColl.elementAt(1)).getText().toString()));
            }break;
            case ProgramCommand.COMMAND_LIGHT:{
                String strTime=((EditText)mCommandViewColl.elementAt(2)).getText().toString();
                if (strTime.equals("0")){
                    strTime="无限";
                }
                else {
                    float time=0;
                    try{
                        strTime= String.valueOf((float) (Integer.parseInt(strTime)/10.0));
                    }catch (NumberFormatException e){
                        ((EditText)mCommandViewColl.elementAt(2)).setText(0);
                        strTime="无限";
                    }
                }

                mTvNote.setText(String.format(ProgramCommand.COMMANDS.get(command),
                        ((Spinner)mCommandViewColl.elementAt(1)).getSelectedItem().toString(),
                        strTime));
            }break;
        }
    }

    private void addSpeedSpinner(LinearLayout.LayoutParams layoutParams) {
        List<String> speedList=new ArrayList<String>();
        speedList.add("1");
        speedList.add("2");
        speedList.add("3");
        speedList.add("4");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,speedList);

        Spinner spinner=new Spinner(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(adapter);
        mLayoutCommand.addView(spinner,layoutParams);
        mCommandViewColl.add(spinner);
    }

    private void addTimeView(LinearLayout.LayoutParams layoutParams) {
        final EditText etTime=new EditText(this);
        etTime.setText(String.valueOf(GrammarParser.INIT_TIME));
        etTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        etTime.setInputType(InputType.TYPE_CLASS_NUMBER);

        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int num=0;
                if (s==null || s.length()==0){
                    etTime.setText("0");
                    return;
                }
                try{
                    num=Integer.parseInt(s.toString());
                }catch (NumberFormatException e){
                    Toast.makeText(ProgramActivity.this,"输入格式错误",Toast.LENGTH_SHORT).show();
                    etTime.setText(String.valueOf(GrammarParser.MIN_TIME));
                }
                if (num>GrammarParser.MAX_TIME){
                    etTime.setText(String.valueOf(GrammarParser.MAX_TIME));
                }else if (num<GrammarParser.MIN_TIME){
                    etTime.setText(String.valueOf(GrammarParser.MIN_TIME));
                }

                updateNode();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mLayoutCommand.addView(etTime,layoutParams);
        mCommandViewColl.add(etTime);
    }

    private void CreateCommandView(String commandText, LinearLayout.LayoutParams layoutParams) {
        mLayoutCommand.removeAllViews();
        mCommandViewColl.clear();

        TextView tvCommand = new TextView(this);
        tvCommand.setText(commandText);

        mLayoutCommand.addView(tvCommand,layoutParams);
        mCommandViewColl.add(tvCommand);
    }

    private void addLightSpinner(LinearLayout.LayoutParams layoutParams) {
        List<String> colors=new ArrayList<String>();
        for (String color :
                LightColor.COLOR_MAP.keySet()) {
            colors.add(color);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,colors);
        final Spinner spinner=new Spinner(this);
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(LightColor.COLOR_MAP.get(spinner.getSelectedItem().toString()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setBackgroundColor(LightColor.COLOR_MAP.get(spinner.getSelectedItem().toString()));
                updateNode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLayoutCommand.addView(spinner,layoutParams);
        mCommandViewColl.add(spinner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_program, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConfirm:{
                String commandLine="";
                for (View view : mCommandViewColl) {
                    if (view instanceof TextView){
                        commandLine+=((TextView)view).getText().toString()+" ";
                    } else if (view instanceof EditText){
                        commandLine+=((EditText)view).getText().toString()+" ";
                    }else if (view instanceof Spinner){
                        commandLine+=((Spinner)view).getSelectedItem().toString()+" ";
                    }
                }
                commandLine+="\n";

                insertCommand(commandLine);
            }break;
            case R.id.btnSendProgram:{
                ProgramSender programSender=new ProgramSender(this);
//                programSender.resetSendTimes();
                programSender.sendData(mEtMoveProgram.getText().toString(),mEtLightProgram.getText().toString());
            }break;
        }
    }

    private void insertCommand(String commandLine) {
        String command=commandLine.split(" ")[0];

        switch (command){
            case ProgramCommand.COMMAND_LIGHT:{
                int index= mEtLightProgram.getSelectionStart();
                Editable editable= mEtLightProgram.getEditableText();
                editable.insert(index,commandLine);
            }break;
            default:{
                int index= mEtMoveProgram.getSelectionStart();
                Editable editable= mEtMoveProgram.getEditableText();
                editable.insert(index,commandLine);
            }
        }
    }
}
