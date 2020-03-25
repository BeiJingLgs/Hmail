package com.fsck.k9.util;

import android.util.Log;

public class LogUtils {
	private static final boolean ISDEBUG = true;
	public static void printLog(String tag, String log) {
		if (ISDEBUG)
			Log.i(tag, log);
	}
	
	public static void printErrorLog(String tag, String log) {
		if (ISDEBUG)
			Log.e(tag, log);
	}


}
