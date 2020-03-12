package com.fsck.k9.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

public class hvFileUtils {
	
	/////////////////////////以下为 Flash 操作///////////////////////////////
	public static String FlashPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public static String HWSYS_PATH = FlashPath + "/hwsys/";//汉王system的路径
	public static String HWSYS_FRONT_IMAGE_ROOT_PATH = HWSYS_PATH + "FrontImageRoot/";//存储封面图片的根路径
	public static String HWSYS_TEMP_PATH = HWSYS_PATH + "temp/"; //临时文件目录
	public static String HWSYS_DATABASE_PATH = HWSYS_PATH + "database/";//共享数据库文件目录
	public static String HWSYS_SYSTEM_PATH = HWSYS_PATH + "android/"; //系统运行必需文件（不主动清除）
	public static String HWSYS_HWLIB_PATH = HWSYS_PATH + "hwlibrary/";
	public static String HWSYS_HWLIBIMG_PATH = HWSYS_HWLIB_PATH + "hwlibrary.bmp";
	public static String HWSYS_HWLIBTIP_PATH = HWSYS_HWLIB_PATH + "hwlibrary.txt";
	public static String HWSYS_DEBUG_FILE_PATH = HWSYS_PATH + "e930_debug.file";//调试状态文件
	public static String HWSYS_LOCKSCREEN_IMAGE_ROOT_PATH = HWSYS_PATH + "lockscreenImage/";//存储锁屏图片的根路径
	public static String HWSYS_LOCKSCREEN_IMAGE_INDEXFILE = HWSYS_LOCKSCREEN_IMAGE_ROOT_PATH + "index.jtl";
	public static String HWSYS_SHUTOFFSCREEN_IMAGE_ROOT_PATH = HWSYS_PATH + "shutoffscreenImage/";//存储关机图片的根路径
	
	// 会议系统
	public static String METTINGSYSTEM_FILETAG = HWSYS_PATH + "meeting.vip";
	public static String METTINGSYSTEM_FILEPATH = HWSYS_PATH + "Meeting";
	
	
	
	//判断Flash卡是否存在
	public static boolean hasFlash() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
//		return true;
	}
	
	public static boolean isFlashChecking(){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_CHECKING)) {
			return true;
		} else {
			return false;
		}
	}
	//判断flash卡是否作为 USB 大容量存储被共享，挂载被解除
	public static boolean flashIsShared(){  
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_SHARED)){
			return true;  
		}else{  
			return false;  
		}
	}
	
    public static long getFlashAvailableBytes(){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)){
			StatFs stat = new StatFs(hvFileUtils.FlashPath);
			return stat.getAvailableBytes();
		}
		return 0;
    }

	

	public static boolean FileIsExisted(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		else
			return true;
	}

    

	
    
	public static Bitmap getLoacalBitmap(String url) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
	     try {
	          fis = new FileInputStream(url);
	          bitmap = BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	     }
	     if (fis != null){
	    	 try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }
	     return bitmap;
	}
   

   
	public static boolean isAssetFileExists(Context context, String filename) {  
	    AssetManager assetManager = context.getAssets();  
	    try {  
	        String[] names = assetManager.list("");  
	        for (int i = 0; i < names.length; i++) {
	            if (names[i].equals(filename.trim())) {
	                return true;  
	            }  
	        }  
	    } catch (IOException e) {  
	        return false;  
	    }  
	    return false;
	}
	
	public static Bitmap getImageFromAssetsFile(Context context, String fileName)
	{ 
		Bitmap image = null;  
		AssetManager am = context.getResources().getAssets(); 
		try  
		{  
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is); 
			is.close();
		}  
		catch (IOException e)
		{  
			e.printStackTrace();
		}
      return image;
	  
	}
	
	public static long getFileOrFolderSize(String strPath){
		File file = new File(strPath);
		return getFileOrFolderSize(file);
	}
	
    public static long getFileOrFolderSize(File file){
        long size = 0;
        if (file.isDirectory()){
	        try {
	            File[] fileList = file.listFiles();
	            for (int i = 0; i < fileList.length; i++)
	            {
	                if (fileList[i].isDirectory())
	                {
	                    size = size + getFileOrFolderSize(fileList[i]);
	                }else{
	                    size = size + fileList[i].length();
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }else{
        	size = file.length();
        }
        return size;
    }
    
    public static String getNameFromPath(String strFilePath){
    	int index = strFilePath.lastIndexOf('/');
	    String name = strFilePath.substring(index + 1);
	    index = name.lastIndexOf('.');
	    if (index > 0){
	    	name = name.substring(0, index);
	    }
	    return name;
    }
    
    public static String getExtFromPath(String strFilePath){
    	int index = strFilePath.lastIndexOf('.');
	    String ext = strFilePath.substring(index + 1);
	    return ext;
    }
    
    public static String getFullPathFromDB(String strDBPath){
    	String strFullPath = strDBPath;
        if (strFullPath.indexOf(":") != -1){
        	String path[] = strFullPath.split(":");
        	if (path.length > 3){
        		strFullPath = path[0];
        		for (int i=1; i<path.length - 2; i++){
        			strFullPath += ":" + path[i];
        		}
        	}else{
        		strFullPath = path[0];
        	}
        }
        return strFullPath;
    }
}

