package com.fsck.k9.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class hvMemoryUtils {
	public static final String TAG = "hvMemoryUtils";
	
	public static boolean bOnDevice = true;//是否在设备上
		
	public static boolean haswifiFuction(){//北京部开放app定制设置为false
		return true;
	}
	
	public static boolean isOldAndroidSDK(){
		int version = android.os.Build.VERSION.SDK_INT;
		if (version <= 19){//Android4.4
			return true;
		}
		return false; 
		// 老版本 SD的路径和状态，使用getExtSdStorageDirectory; true, android8.1增加了getExtSdStorageDirectory(context)
	}
	
	// 是不是android8.1
	public static boolean isAndroid8_1(){
		int version = android.os.Build.VERSION.SDK_INT;
		return version == 27;
	}
	
	public static boolean hasMusicFuction(){//奥义没有Music
		return true;
	}
	
	public static boolean hasA2(){
		return false;
	}
	
	// 可安装第三方app版本
	public static boolean isExtendApp(){
		if (isDangxiao() || !is1GMemory())
			return false;
		return true;
	}
	
	// 会议系统定制：新的大屏尺寸 1920*1080：暂时机器不休眠
	public static boolean isMettingSystem_more(){
		return false;
	}
	
	// 会议系统定制：图片浏览替换成会议系统
	public static boolean isMettingSystem(){
		if (isMettingSystem_more()){
			return true;
		}
		return false;
	}
	

	
	// 党建学习定制
	public static boolean isHWLibraryDangjian(){
		return false;
	}
	
	// 党建学习定制:审阅记事
	public static boolean isHWLibraryDangjian_SYJS(){
		return false;
	}
	
	
	// 党校定制版本
	public static boolean isDangxiao(){
		return false;
	}
	
	
	// 汉王数字图书馆:青岛项目
	public static boolean isHWLibrary(){
		return false;
	}
	
	
	// 青岛项目定制:汉王数字图书馆改名智慧阅读，增加有声教材,去掉图片浏览
	// 去掉汉王书城，当当书城
	// 主界面改变
	// 去掉APK安装和卸载
	// 增加拷贝有声教材的功能
	public static boolean isHWLibraryQingD(){
		if (isHWLibaryAndDangDang() || isHWLibraryWhjs()){
			return true;
		}
		return false;
	}
	
	//智慧阅读+汉王书城+当当云阅读
	public static boolean isHWLibaryAndDangDang(){
		return false;
	}
	
	// 汉王智慧阅读+文件建设目录:
	// 系统设置：1.不要英文版本；2.增加拷贝文件建设书籍的功能
	public static boolean isHWLibraryWhjs(){
		return false;
	}
	
	// 9宫格布局,和会议系统一致:10.3和13.3寸使用
	public static boolean isHWLibraryQingDNew(){
		return false;
	}
	
	// 智慧阅读和电纸作业的版本：
	// 智慧阅读放作上角，电纸作业放右上角
	public static boolean isHWLibraryAndSarkBook(){
		return false;
	}
	
	// 天海书屋定制:汉王数字图书馆改名为天海书屋，增加有声教材,去掉图片浏览
	// 系统设置：
	//	1.去掉日历，当当阅读，wifi传书
	//  2.去掉APK安装和卸载
	public static boolean isHWLibraryThsw(){
		return false;
	}
	
	// 天海书屋新版-排版跟青岛一致
	public static boolean isHWLibraryThsw_New(){
		return false;
	}
	
	// 宁夏银川定制:
	// 青岛定制的界面里歌曲播放替换成今日保证，放到实用工具里
	public static boolean isHWLibraryNxyc(){
		return false;
	}
	
	// 锐学堂定制：2个锐学堂apk，一个数字图书馆apk
	/*
		1.增加了锐学堂学生和小锐作业和智慧阅读的入口
		2.去掉了当当阅读和汉王书城
		3.图片浏览放到实用工具中
	 * */
	public static boolean isHWLibraryRxt(){
		return false;
	}
	
	/* 奥义刘静亭定制
	 * 去掉了当当阅读，用生词本替换
	 * 
	 * */
	public static boolean isAoyiLiujinting(){
		return false;
	}
	
	// 藏文定制：用第三方apk打开epub
	public static boolean isTiben(){
		return false;
	}
	
	public static boolean isSGJCY(){ // 寿光检查院版本
		if (isPJJCY_10_3()){
			return true;
		}
		return false;
	}
	
	//10.3寸浙江浦江县:带当当阅读和汉王书城
	public static boolean isPJJCY_10_3(){
		return false;
	}
	
	// 边检定制
	public static boolean isBianJian(){
		// 搜索 dj_xiugai
		return false;
	}
	
	//滨州检察院：寿光基础上在常用工具里加上汉王书城和今日报纸
	//预装书库增加法律类
	public static boolean isBZJCY(){ 
		return false;
	}
	
	// 好之好定制:主界面修改
	public static boolean isHaozhihao() {
		return false;
	}
	
	// 开机启动第三方应用
	public static boolean isRun3rdAppWhenBootCompleted(){
		return false;
	}
	
	// 是否是经典界面
	// 非经典界面访问了手写笔记的数据库，需要手写笔记的APK的AndroidManifest.xml中的
	// hanvon.aebr.hvnote.database.HvNoteContentProvider 添加属性android:exported="true"
	//例如：        <provider android:name="hanvon.aebr.hvLauncher.UserOPdb.HvUserOPContentProvider"
    //				android:authorities="hanvon.aebr.hvLauncher.UserOPdb.HvUserOP"
    //				android:exported="true"/>
	public static boolean isClassicalSurface(){
		if (isNinePalaceSurfaceNew() || isNinePalaceSurface() || is13_3inch_2200x1650){
			return false;
		}
		return true;
	}
	
	// 是否是九宫格界面
	public static boolean isNinePalaceSurface(){
		if (isHWLibraryDangjian() || isHWLibraryDangjian_SYJS()
				|| isHWLibraryQingD() || isHWLibraryThsw()
				|| isHWLibraryNxyc() || isHWLibraryRxt()
				|| isHWLibrary()
				|| isHWLibraryAndSarkBook()){
			return false;
		}
		if (isNinePalaceSurfaceNew()){
			return false;
		}
		return false;
	}
	
	// 是否是九宫格界面新界面
	public static boolean isNinePalaceSurfaceNew(){
		if (isHWLibraryDangjian() || isHWLibraryDangjian_SYJS() 
				|| isHWLibraryQingD() || isHWLibraryThsw()
				|| isHWLibraryNxyc() || isHWLibraryRxt()
				||isHWLibrary()
				|| isHWLibraryAndSarkBook()){
			return false;
		}
		if (is10_3inch_1872x1404 || is13_3inch_2200x1650 || isMettingSystem()){
			return true;
		}
		return false;
	}
	
	public static boolean needForceUpdate(){
		return false;
	}
	
	public static boolean hasSafeBox(){
		if (isSGJCY()){
			return true;
		}
		return false;
	}
	
	/**
	 * 卷宗中是否txt
	 * @return
	 */
	public static boolean hasSafeBoxTxt(){
		if (hasSafeBox()){
			return false;
		}
		return true;
	}
	
	// 文件复制删除移动重命名操作时是否对psl， note，以及数据库进行同步更改
	public static boolean useFileOpData(){
		if (isSGJCY() || isAndroid8_1()){
			return false;
		}
		return true;
	}
	
	private static boolean is6inch_1024x758 = false;
	public static boolean is6inch_1024x758(){
		return is6inch_1024x758;
	}
	public static void set6inch_1024x758(Context context, boolean is6_1024x758){
		is6inch_1024x758 = is6_1024x758;
		if (is6inch_1024x758){
			hvApkUtils.putPackageTypeAndName(0);
		}
	}
	
	// 是否是6inch1448x1072版本
	private static boolean is6inch_1448x1072 = false;
	public static boolean is6inch_1448x1072 (){
		return is6inch_1448x1072 ;
	}
	public static void set6inch_1448x1072 (Context context, boolean is6_1448x1072){
		is6inch_1448x1072  = is6_1448x1072;
		if (is6inch_1448x1072){
			hvApkUtils.putPackageTypeAndName(1);
		}
	}
	
	// 是否是9.7inch版本
	private static boolean is9_7inch_1200x825 = false;
	public static boolean is9_7inch_1200x825(){
		return is9_7inch_1200x825;
	}
	public static void set9_7inch_1200x825(Context context, boolean is9_7_1200x825){
		is9_7inch_1200x825 = is9_7_1200x825;
		if (is9_7inch_1200x825){
			hvApkUtils.putPackageTypeAndName(2);
		}
	}
	
	
	// 是否是10.3inch版本
	private static boolean is10_3inch_1872x1404 = false;
	public static boolean is10_3inch_1872x1404(){
		return is10_3inch_1872x1404;
	}
	
	public static void set10_3inch_1872x1404(Context context, boolean is10_3_1872x1404){
		is10_3inch_1872x1404 = is10_3_1872x1404;
		if (isMettingSystem() && isMettingSystem_more())
			is10_3inch_1872x1404 = true;

		if (is10_3inch_1872x1404){
			hvApkUtils.putPackageTypeAndName(3);
		}
	}
	

	// 是否是13.3inch版本
	private static boolean is13_3inch_2200x1650 = false;
	public static boolean is13_3inch_2200x1650(){
		return is13_3inch_2200x1650;
	}
	public static void set13_3inch_2200x1650(Context context, boolean is13_3_2200x1650){
		is13_3inch_2200x1650 = is13_3_2200x1650;
		if (is13_3inch_2200x1650){
			hvApkUtils.putPackageTypeAndName(4);
		}
	}
	



	
	private static boolean is1GMemory(){
	    String str1 = "/proc/meminfo";// 系统内存信息文件
	    String str2;
	    String[] arrayOfString;
	    long initial_memory = 0;
	    try { 
	      FileReader localFileReader = new FileReader(str1);
	      BufferedReader localBufferedReader = new BufferedReader(
	          localFileReader, 8192);
	      str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
	      localBufferedReader.close();
	      
	      arrayOfString = str2.split("\\s+");
	      initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
	      if (initial_memory <= 512 * 1024 * 1024){
	    	  return false;
	      }else if (initial_memory <= 1024 * 1024 * 1024){
	    	  return true;
	      }
	    } catch (IOException e) {
	    }
	    return true;
	}
	

    
    static private boolean isPasteMenuNeedShow = false;
    
    public static boolean isPasteMenuNeedShow(){return isPasteMenuNeedShow;}
    public static void changePasteMenuShowStatus(boolean isShow){
    	isPasteMenuNeedShow = isShow;
    }
    
    
    static private List<File> mListMoveFile = new ArrayList<File>();
    static private List<File> mListCopyFile = new ArrayList<File>();
    public static List<File> getMoveFileList(){return mListMoveFile;}
    public static void clearMoveFileList(){
    	mListMoveFile.clear();
    }
    public static int getMoveFileSize(){return mListMoveFile.size();}
    public static File getMoveFileItem(int index){
    	if (index >= getMoveFileSize()){
    		return null;
    	}
    	return mListMoveFile.get(index);
    }
    public static void addMoveFileToList(File file){
    	mListMoveFile.add(file);
//    	if (file.isFile()){
//    		String filepath = file.getAbsolutePath();
//    		String filepathPsl = filepath + ".psl";
//    		if (common.isFileExist(filepathPsl))
//    			mListMoveFile.add(new File(filepathPsl));
//    		
//    		String filepathNote = filepath + ".note";
//    		if (common.isFileExist(filepathNote))
//    			mListMoveFile.add(new File(filepathNote));
//    	}
    }
    
    public static List<File> getCopyFileList(){return mListCopyFile;}
    public static void clearCopyFileList(){
    	mListCopyFile.clear();
    }
    public static int getCopyFileSize(){return mListCopyFile.size();}
    public static File getCopyFileItem(int index){
    	if (index >= getCopyFileSize()){
    		return null;
    	}
    	return mListCopyFile.get(index);
    }
    public static void addCopyFileToList(File file){
    	mListCopyFile.add(file);
    	
//    	if (file.isFile()){
//    		String filepath = file.getAbsolutePath();
//    		String filepathPsl = filepath + ".psl";
//    		if (common.isFileExist(filepathPsl))
//    			mListCopyFile.add(new File(filepathPsl));
//    		
//    		String filepathNote = filepath + ".note";
//    		if (common.isFileExist(filepathNote))
//    			mListCopyFile.add(new File(filepathNote));
//    	}
    }
    public static void allMoveFileToCopyFile(){
    	mListCopyFile.addAll(mListMoveFile);
    }
    
	// 隐藏输入法:
	public static void hideSoftInput(Activity activity){
		InputMethodManager im = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (im != null){
			im.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
 
}

