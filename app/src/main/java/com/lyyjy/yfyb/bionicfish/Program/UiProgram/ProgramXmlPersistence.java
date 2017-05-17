package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/15.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramXmlPersistence {
    @SuppressWarnings("unused")
    private static final String TAG = ProgramXmlPersistence.class.getSimpleName();
    private static final String ROOT_ITEM_TAG = "blocks";

    private final Context mContext;

    public ProgramXmlPersistence(Context context){
        mContext=context;
    }

//    private String getFilePath(){
//        String path="//data//data//";
//        path+=mContext.getPackageName();
//        path+="//shared_prefs//";
//
//        return path;
//    }

    public String[] getFileNames(){
        File[] files=mContext.getFilesDir().listFiles();
        if (files==null){
            return null;
        }
        ArrayList<String> fileNames=new ArrayList<>();
        for (File file:files){
            String[] fileNameInfo=splitFileName(file.getName());
            if (fileNameInfo==null){
                continue;
            }
            if (fileNameInfo[1].equals("xml")){
                fileNames.add(fileNameInfo[0]);
            }
        }

        String[] result = new String[fileNames.size()];
        return fileNames.toArray(result);
    }

//    private String getStringNoEx(String fileName){
//        if (fileName!=null && fileName.length()>0){
//            int index=fileName.lastIndexOf('.');
//            if (index>-1&&index<fileName.length()){
//                return fileName.substring(0,index);
//            }
//        }
//
//        return fileName;
//    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean removeFile(String fileName){
       return new File(mContext.getFilesDir().getPath()+"/"+fileName+".xml").delete();
    }

    private String[] splitFileName(String fileName){
        String[] result = new String[2];

        if (fileName!=null && fileName.length()>0){
            int index=fileName.lastIndexOf('.');
            if (index>-1&&index<fileName.length()){
                result[0]= fileName.substring(0,index);
                if (fileName.length()>result[0].length()+1){
                    result[1] = fileName.substring(result[0].length() + 1, fileName.length());
                }
            }
        }

        if (result[0]==null || result[1]==null){
            return null;
        }

        return result;
    }

    public void saveFile(String fileName, ArrayList<ProgramBlock> blocks){
        try {
            FileOutputStream fileOutputStream=mContext.openFileOutput(fileName + ".xml", Context.MODE_PRIVATE);
            XmlSerializer xmlSerializer= Xml.newSerializer();
            xmlSerializer.setOutput(fileOutputStream,"utf-8");

            xmlSerializer.startDocument("utf-8",true);
            xmlSerializer.startTag(null, ROOT_ITEM_TAG);
            for (int i = 0; i < blocks.size(); ++i) {
                ProgramBlock block = blocks.get(i);
                xmlSerializer.startTag(null, block.getClass().getName());

                //写入视图信息
                for (int j=0;j<block.getViews().size();++j){
                    ProgramView programView=block.getViews().get(j);
                    xmlSerializer.startTag(null,programView.getClass().getName());
                    xmlSerializer.attribute(null,"left", String.valueOf(programView.getLeft()));
                    xmlSerializer.attribute(null, "top", String.valueOf(programView.getTop()));

                    //视图执行信息
                    xmlSerializer.startTag(null,"data");
                    xmlSerializer.text(programView.executeText());
                    xmlSerializer.endTag(null,"data");

                    xmlSerializer.endTag(null,programView.getClass().getName());
                }

                xmlSerializer.endTag(null, block.getClass().getName());
            }
            xmlSerializer.endTag(null, ROOT_ITEM_TAG);
            xmlSerializer.endDocument();

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile(String fileName, ArrayList<ProgramBlock> desBlocks){
        try {
            FileInputStream fileInputStream=mContext.openFileInput(fileName + ".xml");
            XmlPullParser parser= Xml.newPullParser();
            parser.setInput(fileInputStream,"utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            ProgramBlock block=null;
            ProgramView programView=null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if (ProgramBlock.class.getName().equals(tagName)) {
                            block = new ProgramBlock();
                            desBlocks.add(block);
                        } else if ("data".equals(tagName)) {
                            if (programView!=null){
                                programView.parseText(parser.nextText());
                            }else{
                                throw new NullPointerException();
                            }
                        }else{
                            try {
                                if (Class.forName(tagName).getSuperclass().getName().equals(ProgramView.class.getName())) {
                                    programView = (ProgramView) Class.forName(tagName).getConstructor(Context.class).newInstance(mContext);

                                    if (block!=null){
                                        block.add(programView);
                                    }else{
                                        throw new NullPointerException();
                                    }

                                    int left= Integer.parseInt(parser.getAttributeValue(null,"left"));
                                    int top= Integer.parseInt(parser.getAttributeValue(null,"top"));
                                    programView.setLeft(left);
                                    programView.setTop(top);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }

            fileInputStream.close();
        }  catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
}
