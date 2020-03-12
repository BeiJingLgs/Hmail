package com.fsck.k9.util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Description:
 * <br/>Copyright (C), 2001-2012, hanvon
 * <br/>Program Name:
 * <br/>Date:
 * @author  zhantao
 * @version  1.0
 */
public class RecentReadingDBHelper extends SQLiteOpenHelper
{
	private static final String TAG = "~~~~RecentReadingDBHelper~~~"; 	
	// 数据库表位置
	public static String DB_PATH = hvFileUtils.HWSYS_DATABASE_PATH; 
	// 数据库名称
    public static String DB_NAME = "recentreading.db";
    public static String DB_NAME_BOOKMARK = "recent_bookmark.db";
    public static String TB_NAME= "recentreading";
    //public static String TB_NAME_BOOKMARK= "recent_bookmark";
    // 数据库版本
    public static final int DB_VERSION = 1;
    
/*	final String CREATE_TABLE_SQL =
		"create table recentreading (_readingtime integer primary key order by desc , " +
		"fullpath varchar, currentpageindex integer, totolpagecount integer, imagepath varchar, notes varchar)";*/

	/**
	 * @param context
	 * @param name
	 * @param version
	 */
	public RecentReadingDBHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}
	
    public RecentReadingDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// 第一个使用数据库时自动建表
		//db.execSQL(CREATE_TABLE_SQL);
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                TB_NAME+ "("+
                LastBookInfoItem.ID + " integer primary key autoincrement," +
                LastBookInfoItem.FILEPATH + " varchar," +
                LastBookInfoItem.PAGEINDEX + " integer," +
                LastBookInfoItem.TOTALPAGE + " integer," +
                LastBookInfoItem.IMAGEPATH + " varchar," +
                LastBookInfoItem.NOTES + " varchar" +
                ")"
                );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		System.out.println("--------onUpdate Called--------" 
			+ oldVersion + "--->" + newVersion);
        db.execSQL( "DROP TABLE IF EXISTS " + TB_NAME );
        onCreate(db);
	}
	
	//更新列
    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL( "ALTER TABLE " +
                    TB_NAME + " CHANGE " +
                    oldColumn + " "+ newColumn +
                    " " + typeColumn
            );
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
