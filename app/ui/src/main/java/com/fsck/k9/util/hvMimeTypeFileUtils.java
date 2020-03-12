package com.fsck.k9.util;



import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.fsck.k9.ui.R;


public class hvMimeTypeFileUtils {
	
	/**
	 * 得到所有支持的文件的类型，包括保险箱文件
	 */
    public static MimeTypes getAllMimeTypes(Context context) {
    	if (context == null){
    		return null;
    	}
    	
        MimeTypeParser mtp = new MimeTypeParser();
        XmlResourceParser in = null;
		if (hvMemoryUtils.isTiben()){
			in = context.getResources().getXml(R.xml.mimetypes_all_tiben);
		}
        else{
        	in = context.getResources().getXml(R.xml.hanvon_mimetypes_all);
        }
        
        MimeTypes mMimeTypes = null;
        try {
            mMimeTypes = mtp.fromXmlResource(in);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(
                "PreselectedChannelsActivity: XmlPullParserException");
        } catch (IOException e) {
        	throw new RuntimeException(
                "PreselectedChannelsActivity: IOException");
        }
        return mMimeTypes;
    }
    
    /**
     * 得到所有支持的文件的类型，不包括保险箱文件
     * @return
     */
    public static MimeTypes getNormalFileListMimeType(Context context, String currentRootPath, boolean bMettingFiles) {
		// TODO Auto-generated method stub
        MimeTypeParser mtp = new MimeTypeParser();

        XmlResourceParser in = null;
        if (bMettingFiles){
        	in = context.getResources().getXml(R.xml.hanvon_mimetypes_normal_mettingfiles);
        }else if(currentRootPath.equals(hvFileUtils.FlashPath)
  				|| currentRootPath.equals(hvFileCommonUtils.getSdcardPath(context))
  				|| currentRootPath.equals(hvFileCommonUtils.getUDiskPath(context))){
			if (hvMemoryUtils.isTiben()){
				in = context.getResources().getXml(R.xml.mimetypes_all_tiben);
			}else{
				in = context.getResources().getXml(R.xml.hanvon_mimetypes_all);
			}
		}else{
			if (hvMemoryUtils.isTiben()){
				in = context.getResources().getXml(R.xml.hanvon_mimetypes_normal_tiben);
			}else{
				in = context.getResources().getXml(R.xml.hanvon_mimetypes_normal);
			}
    	}
        
       
        MimeTypes types = null;
        try {
        	types = mtp.fromXmlResource(in);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(
                "getNormalFileListMimeType PreselectedChannelsActivity: XmlPullParserException");
        } catch (IOException e) {
        	throw new RuntimeException(
                "PreselectedChannelsActivity: IOException");
        }
        return types;
	}
    
}

