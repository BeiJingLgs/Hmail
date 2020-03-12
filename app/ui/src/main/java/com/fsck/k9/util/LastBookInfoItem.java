package com.fsck.k9.util;


public class LastBookInfoItem
{
	public static final String ID = "_id";
    public static final String FILEPATH = "fullpath";
    public static final String PAGEINDEX = "currentpageindex";
    public static final String TOTALPAGE = "totolpagecount";
    public static final String IMAGEPATH = "imagepath";
    public static final String NOTES = "notes";
	
    //最近阅读书目信息
	public int mId = 0;//
	public int mCurPageIndex = -1;//
	public int mTotalPageNum = 0;
	public String mStrBookFullPath = "";
	public String mStrFrontImgPath = "";
	public String mStrOtherInfo = "";
	public String mStrPackName = "";
	public String mStrActivityName = "";
	public boolean mIsVisibile = false; // checkBox是否显示
	public boolean mIsSelected = false; // checkBox是否选中
	
	public void clear(){
		mId = 0;
		mCurPageIndex = -1;
		mTotalPageNum = 0;
		mStrBookFullPath = "";
		mStrFrontImgPath = "";
		mStrOtherInfo = "";
		mStrPackName = "";
		mStrActivityName = "";
		mIsVisibile = false;
		mIsSelected = false;
	}
}