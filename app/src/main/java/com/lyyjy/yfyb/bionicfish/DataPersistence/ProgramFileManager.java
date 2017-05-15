package com.lyyjy.yfyb.bionicfish.DataPersistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2016/7/8.
 */
public class ProgramFileManager {
    private static final String TAG="ProgramFileManager";

    private static final String FIRST_PROGRAM_NAME="firstProgram";
    private static final String SECOND_PROGRAM_NAME="secondProgram";
//    private static final String FILE_PATH="//data//data//com.lyyjy.zdhyjs.bluetoothfish//shared_prefs//";

    private Context mContext;

    public ProgramFileManager(Context context){
        mContext=context;
    }

    public void saveFile(Context context, String fileName, String directionText, String lightText){
        SharedPreferences.Editor editor=context.getSharedPreferences(fileName,Context.MODE_PRIVATE).edit();
        editor.putString(FIRST_PROGRAM_NAME,directionText);
        editor.putString(SECOND_PROGRAM_NAME,lightText);
        editor.commit();
    }

    public void removeFile(String fileName){
        new File(getFilePath()+fileName+".xml").delete();
    }

    public String[] getFileNames(){
        File[] files=new File(getFilePath()).listFiles();
        if (files==null){
            return null;
        }
        final String[] fileNames=new String[files.length];
        for (int i=0;i<files.length;++i){
            fileNames[i]=getStringNoEx(files[i].getName());
        }

        return fileNames;
    }

    public String getFilePath(){
        String path="//data//data//";
        path+=mContext.getPackageName();
        path+="//shared_prefs//";

        return path;
    }

    public String getFirstProgram(Context context,String fileName){
        return context.getSharedPreferences(fileName,Context.MODE_PRIVATE).getString(FIRST_PROGRAM_NAME,"");
    }

    public String getSecondProgram(Context context,String fileName){
        return context.getSharedPreferences(fileName,Context.MODE_PRIVATE).getString(SECOND_PROGRAM_NAME,"");
    }

    private String getStringNoEx(String fileName){
        if (fileName!=null && fileName.length()>0){
            int index=fileName.lastIndexOf('.');
            if (index>-1&&index<fileName.length()){
                return fileName.substring(0,index);
            }
        }

        return fileName;
    }
}
