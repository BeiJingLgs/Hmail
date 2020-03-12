package com.fsck.k9.util;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Description:
 * <br/>Copyright (C), 2001-2012, hanvon
 * <br/>Program Name:
 * <br/>Date:
 * @author  zhantao
 * @version  1.0
 */
public class RecentReadingDB
{
	private static final String TAG = "~~~~RecentReadingDB~~~";
	private static final int MAX = 64;//存储的最大项数为：MAX + 1
	
    private SQLiteDatabase db;
    private RecentReadingDBHelper dbHelper;
    
    public RecentReadingDB(SQLiteDatabase db, RecentReadingDBHelper dbHelper) {
    	this.db = db;
    	this.dbHelper = dbHelper;
    }

    public void Close() {
          db.close();
          dbHelper.close();
    }

    // 获取recentreading表中的fullpath, currentpageindex, totolpagecount, imagepath, notes的记录
    public List<LastBookInfoItem> GetRecentReadingList() {
         List<LastBookInfoItem> recentReadingList = new ArrayList<LastBookInfoItem>();
         Cursor cursor = db.query(RecentReadingDBHelper.TB_NAME, null, null , null, null,
                   null, LastBookInfoItem.ID + " DESC");
         cursor.moveToFirst();
         while (!cursor.isAfterLast() && (cursor.getString(1) != null )) {
             LastBookInfoItem recentReading = new LastBookInfoItem();
             recentReading.mId = Integer.parseInt(cursor.getString(0));
             recentReading.mStrBookFullPath = cursor.getString(1);
             recentReading.mCurPageIndex = Integer.parseInt(cursor.getString(2));
             recentReading.mTotalPageNum = Integer.parseInt(cursor.getString(3));
             recentReading.mStrFrontImgPath = cursor.getString(4);
             recentReading.mStrOtherInfo = cursor.getString(5);
             if (!recentReading.mStrOtherInfo.isEmpty()){
            	 String tmp[] = recentReading.mStrOtherInfo.split(":");
            	 if (tmp.length == 2){
            		 recentReading.mStrPackName = tmp[0];
            		 recentReading.mStrActivityName = tmp[1];
            	 }
             }

             recentReadingList.add(recentReading);
             cursor.moveToNext();
         }
         cursor.close();
         return recentReadingList;
    }
    
    public List<LastBookInfoItem> GetRecentBookmarkList() {
    	List<LastBookInfoItem> recentReadingList = new ArrayList<LastBookInfoItem>();
        Cursor cursor = db.query(RecentReadingDBHelper.TB_NAME, null, null , null, null,
                  null, LastBookInfoItem.ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null )) {
            LastBookInfoItem recentReading = new LastBookInfoItem();
            recentReading.mId = Integer.parseInt(cursor.getString(0));
            recentReading.mStrBookFullPath = cursor.getString(1);
            recentReading.mCurPageIndex = Integer.parseInt(cursor.getString(2));
            recentReading.mTotalPageNum = Integer.parseInt(cursor.getString(3));
            recentReading.mStrFrontImgPath = cursor.getString(4);
            recentReading.mStrOtherInfo = cursor.getString(5);

            recentReadingList.add(recentReading);
            cursor.moveToNext();
        }
        cursor.close();
        return recentReadingList;
    }

    // 判断recentreading表中的是否包含某个reading book的记录
    private Boolean HaveThisReadingInfo(String Fullpath) {
         Boolean b = false;
         Cursor cursor = db.query(RecentReadingDBHelper.TB_NAME, null, LastBookInfoItem.FILEPATH
                  + "=?", new String[]{Fullpath}, null, null, null );
         b = cursor.moveToFirst();
         cursor.close();
         return b;
    }

    
    // 添加recentreading表的记录
    public Long SaveRecentReadingInfo(LastBookInfoItem recentReading) {
    	if(HaveThisReadingInfo(recentReading.mStrBookFullPath))//存在记录，则先删除该记录
    	{
    		DelRecentReadingInfo(recentReading.mStrBookFullPath);//删除原有记录		
    	}
    	int currentCount = GetRecentReadingList().size();
    	if(currentCount > MAX)//超过最大项数设置
    	{
    		DelRecentReadingInfo(GetRecentReadingList().get(currentCount - 1).mStrBookFullPath);//删除第一项	
    		//DelRecentReadingInfo(1);//删除第一项	
    	}
    	ContentValues values = new ContentValues();
    	values.put(LastBookInfoItem.FILEPATH, recentReading.mStrBookFullPath);
    	values.put(LastBookInfoItem.PAGEINDEX, recentReading.mCurPageIndex);
    	values.put(LastBookInfoItem.TOTALPAGE, recentReading.mTotalPageNum);
    	values.put(LastBookInfoItem.IMAGEPATH, recentReading.mStrFrontImgPath);
    	values.put(LastBookInfoItem.NOTES, recentReading.mStrOtherInfo);
    	Long uid = db.insert(RecentReadingDBHelper.TB_NAME, LastBookInfoItem.ID, values);
    	return uid;
    }

    // 添加recentreading表的记录
    public Long SaveRecentBookmarkInfo(LastBookInfoItem recentReading) {
    	DelRecentBookmarkInfo(recentReading);
    	ContentValues values = new ContentValues();
    	values.put(LastBookInfoItem.FILEPATH, recentReading.mStrBookFullPath);
    	values.put(LastBookInfoItem.PAGEINDEX, recentReading.mCurPageIndex);
    	values.put(LastBookInfoItem.TOTALPAGE, recentReading.mTotalPageNum);
    	values.put(LastBookInfoItem.IMAGEPATH, recentReading.mStrFrontImgPath);
    	values.put(LastBookInfoItem.NOTES, recentReading.mStrOtherInfo);
    	Long uid = db.insert(RecentReadingDBHelper.TB_NAME, LastBookInfoItem.ID, values);
    	return uid;
    }

    // 删除recentreading表的记录
    public void DelRecentBookmarkInfo(LastBookInfoItem recentReading) {
    	String Fullpath = recentReading.mStrBookFullPath;
    	int curPageIndex = recentReading.mCurPageIndex;
    	String sql = "DELETE FROM " + RecentReadingDBHelper.TB_NAME + " WHERE " + LastBookInfoItem.FILEPATH
    			+ "='" + Fullpath + "' AND " + LastBookInfoItem.PAGEINDEX + "='" + Integer.toString(curPageIndex)+ "'";
        db.execSQL(sql);
    }

    // 删除recentreading表的记录
    public int DelRecentReadingInfo(String Fullpath) {
        int id = db.delete(RecentReadingDBHelper.TB_NAME,
                  LastBookInfoItem.FILEPATH + "=?", new String[]{Fullpath});
         return id;
    }
    
    public static void addOfficeRecentReadingInfo(Context context, String path){
		if (hvFileUtils.FileIsExisted(RecentReadingDBHelper.DB_PATH)) 
		{
			RecentReadingDBHelper dbHelper = new RecentReadingDBHelper(context,
					RecentReadingDBHelper.DB_PATH
							+ RecentReadingDBHelper.DB_NAME, null,
					RecentReadingDBHelper.DB_VERSION);
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			RecentReadingDB recentdb = new RecentReadingDB(db, dbHelper);
			LastBookInfoItem recentReading = new LastBookInfoItem();
			recentReading.mStrBookFullPath = path;
			recentReading.mStrOtherInfo = "com.yozo.office.read:com.yozo.AppFrameActivity";
            recentReading.mStrPackName = "com.yozo.office.read";
            recentReading.mStrActivityName = "com.yozo.AppFrameActivity";
			recentdb.SaveRecentReadingInfo(recentReading);
			recentdb.Close();// 关闭数据库
		}
	}
}
