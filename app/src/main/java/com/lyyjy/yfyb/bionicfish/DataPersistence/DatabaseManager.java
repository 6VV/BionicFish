package com.lyyjy.yfyb.bionicfish.DataPersistence;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;

import com.lyyjy.yfyb.bionicfish.ContextUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public class DatabaseManager extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "BluetoothDevice";
    private static final int DATABASE_VERSION = 2;

    private final String SETTINGS_TABLE_NAME="SettingsTable";
    private final String SETTINGS_COLUMN_NAME="SettingName";
    private final String SETTINGS_COLUMN_VALUE="SettingValue";
    private final String CREATE_SETTINGS_TABLE="create table "+SETTINGS_TABLE_NAME+"("
            + SETTINGS_COLUMN_NAME+" text,"
            + SETTINGS_COLUMN_VALUE+" integer)";

    private final String WIDGET_TABLE_NAME="WidgetTable";
    private final String WIDGET_COLUMN_ID="WidgetId";
    private final String WIDGET_LEFT="WidgetLeft";
    private final String WIDGET_TOP="WidgetTop";
    private final String WIDGET_RIGHT="WidgetRight";
    private final String WIDGET_BOTTOM="WidgetBottom";
    private final String CREATE_WIDGET_TABLE="create table "+WIDGET_TABLE_NAME+"("
            +WIDGET_COLUMN_ID+" integer,"
            +WIDGET_LEFT+" integer,"
            +WIDGET_TOP+" integer,"
            +WIDGET_RIGHT+" integer,"
            +WIDGET_BOTTOM+" integer)";

    public static DatabaseManager mInstance=null;
    public static DatabaseManager getInstance(){
        if (mInstance==null){
            mInstance = new DatabaseManager(ContextUtil.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
        }
        return mInstance;
    }

    private DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(CREATE_SETTINGS_TABLE);
        db.execSQL(CREATE_WIDGET_TABLE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+SETTINGS_TABLE_NAME);
        onCreate(db);
    }

    public Map<String,Integer> selectSettingValues(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + SETTINGS_TABLE_NAME,null);

        Map map=new HashMap();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                map.put(cursor.getString(cursor.getColumnIndex(SETTINGS_COLUMN_NAME)), cursor.getInt(cursor.getColumnIndex(SETTINGS_COLUMN_VALUE)));
            }
        }

        cursor.close();
        db.close();

        return map;
    }

    public void replaceSettingValues(String name,int value){
        SQLiteDatabase db=getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(SETTINGS_COLUMN_NAME,name);
        values.put(SETTINGS_COLUMN_VALUE,value);

//        db.rawQuery("insert or replace into " + SETTINGS_TABLE_NAME+" where "+SETTINGS_COLUMN_NAME+"=?",new String[]{name});
        db.replace(SETTINGS_TABLE_NAME, null, values);
        db.close();
    }

    public void repalceWidgetLayout(Map<Integer,Rect> map){
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        for (int viewId : map.keySet()) {
            Rect rect = map.get(viewId);
            ContentValues values=new ContentValues();
            values.put(WIDGET_COLUMN_ID,viewId);
            values.put(WIDGET_LEFT,rect.left);
            values.put(WIDGET_TOP,rect.top);
            values.put(WIDGET_RIGHT,rect.right);
            values.put(WIDGET_BOTTOM,rect.bottom);

            db.replace(WIDGET_TABLE_NAME, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public Map<Integer,Rect> selectWidgetLayout(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + WIDGET_TABLE_NAME,null);

        Map map=new HashMap<Integer,Rect>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    map.put(cursor.getInt(cursor.getColumnIndex(WIDGET_COLUMN_ID)),
                            new Rect(cursor.getInt(cursor.getColumnIndex(WIDGET_LEFT)),
                                    cursor.getInt(cursor.getColumnIndex(WIDGET_TOP)),
                                    cursor.getInt(cursor.getColumnIndex(WIDGET_RIGHT)),
                                    cursor.getInt(cursor.getColumnIndex(WIDGET_BOTTOM))));
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();

        return map;
    }
}
