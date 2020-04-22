package com.fsck.k9.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

public class hvApkUtils {

    public static final Map<Integer, String> mPackageType = new HashMap<Integer, String>();
    public static final Map<String, String> mPackageApkName = new HashMap<String, String>();

    public static void updateApk(File selectedFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, "com.fsck.k9.ui.fileprovider", selectedFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
//            Toast.makeText(context, "进来了" + uri.toString() + "vvvvv..." + selectedFile.toString(), Toast.LENGTH_SHORT).show();
            Log.i("tag", "mmmmmmmmmmmm");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            Log.i("tag", "nnnnnnnnnnnnnnnnm");
//            Toast.makeText(context,"进来了1111"+selectedFile.toString(), Toast.LENGTH_SHORT).show();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(selectedFile), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    public static void updateApk(String selectedFilePath, Context context) {
        File selectedFile = new File(selectedFilePath);
        if (selectedFile.exists()) {
            updateApk(selectedFile, context);
        }
    }

    public static boolean startActivityFromIntent(Context context, Intent intent) {
        boolean bOK = true;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            bOK = false;
            Toast.makeText(context, "未找到应用程序", Toast.LENGTH_SHORT).show();
        }
        ;
        return bOK;
    }


    public static boolean startActivityFromeApk(Context context, String pkg, String cls) {
        return startMyActivity(context, pkg, cls, null, null);
    }

    public static boolean startMyActivity(Context mContext, String pkg, String cls, String name, String value) {
        ComponentName cn = new ComponentName(pkg, cls);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(cn);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (name != null && value != null)
            intent.putExtra(name, value);
        return hvApkUtils.startActivityFromIntent(mContext, intent);

    }

    public static void startMyActivity2(Context context, String pkg, String cls, String name, String value, String name1, String value1) {
        ComponentName cn = new ComponentName(pkg, cls);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(cn);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (name != null && value != null)
            intent.putExtra(name, value);
        if (name1 != null && value1 != null)
            intent.putExtra(name1, value1);
        context.startActivity(intent);
    }


    public static void sendStaticBroadcast(Context context, String action, String packagename, String receivername) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        //myIntent.setComponent(new ComponentName("hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier"));
        myIntent.setComponent(new ComponentName(packagename, receivername));
        context.sendBroadcast(myIntent);
    }

    public static void sendStaticBroacast_hvSetting(Context context, String action) {
        sendStaticBroadcast(context, action, "hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier");
    }


    public static void sendDynamicBroadcast(Context context, String action) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        context.sendBroadcast(myIntent);
    }

    public static void sendDynamicBroadcastAndStr(Context context, String action, String name, String value) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        myIntent.putExtra(name, value);
        context.sendBroadcast(myIntent);
    }

    public static void startMyActivity(Context context, Class<?> cls) {
        final Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startMyActivityForPic(Context context, Class<?> cls, boolean useassets, String strPath) {
        final Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra("useassets", useassets);
        intent.putExtra("path", strPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void apkOperateData(Context context, String strPackage, String appName, int nType, boolean hasTip) {
        Intent intent = new Intent("hanvon.intent.action.installedAppOperate");
        intent.putExtra("operate_type", nType);
        intent.putExtra("package_name", strPackage);
        intent.putExtra("app_name", appName);
        intent.putExtra("hastip", hasTip);
        intent.setPackage("hanvon.aebr.hvsettings");
        intent.setComponent(new ComponentName("hanvon.aebr.hvsettings", "hanvon.aebr.hvsettings.ScreencapBroadCastRecevier"));
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }

    public static void putPackageTypeAndName(int screen_size) {
        mPackageType.clear();
        mPackageApkName.clear();
        if (screen_size <= 1) { // 6寸
            mPackageType.put(0, "hanvon.aebr.hvLauncher");
            mPackageType.put(1, "hanvon.aebr.hvreader");
            mPackageType.put(2, "com.yozo.office.read");
            mPackageType.put(3, "hanvon.aebr.hvimageshow");
            mPackageType.put(4, "hanvon.aebr.hvbookstore");
            mPackageType.put(5, "hanvon.aebr.hvddreader");
            mPackageType.put(6, "hanvon.aebr.wifitransfer");
            mPackageType.put(7, "hanvon.aebr.hvaudio");
            mPackageType.put(8, "hanvon.aebr.hvDict");
            mPackageType.put(9, "hanvon.aebr.hvWordList");
            mPackageType.put(10, "com.hanvon.inputmethod.hanvonime.pad");


            mPackageApkName.put("hanvon.aebr.hvLauncher", "hvLauncher60.apk");
            mPackageApkName.put("hanvon.aebr.hvreader", "hvReader60.apk");
            mPackageApkName.put("com.yozo.office.read", "yozoOffice60.apk");
            mPackageApkName.put("hanvon.aebr.hvimageshow", "hvImageShow60.apk");
            mPackageApkName.put("hanvon.aebr.hvbookstore", "hvbookstore60.apk");
            mPackageApkName.put("hanvon.aebr.wifitransfer", "hvWifiTransfer60.apk");
            mPackageApkName.put("hanvon.aebr.hvddreader", "hvDDReader60.apk");
            mPackageApkName.put("hanvon.aebr.hvaudio", "hvAudio60.apk");
            mPackageApkName.put("hanvon.aebr.hvDict", "hvDict60.apk");
            mPackageApkName.put("hanvon.aebr.hvWordList", "hvWordList60.apk");
            mPackageApkName.put("com.hanvon.inputmethod.hanvonime.pad",
                    "hvInputMethod60.apk");
        } else { // 9.7寸及其以上
            mPackageType.put(0, "hanvon.aebr.hvLauncher");
            mPackageType.put(1, "hanvon.aebr.hvnote");
            mPackageType.put(2, "hanvon.aebr.hvreader");
            mPackageType.put(3, "com.yozo.office.read");
            mPackageType.put(4, "hanvon.aebr.hvimageshow");
            mPackageType.put(5, "hanvon.aebr.hvbookstore");
            mPackageType.put(6, "hanvon.aebr.hvddreader");
            mPackageType.put(7, "hanvon.aebr.wifitransfer");
            mPackageType.put(8, "hanvon.aebr.hvaudio");
            mPackageType.put(9, "hanvon.aebr.hvDict");
            mPackageType.put(10, "hanvon.aebr.hvWordList");
            mPackageType.put(11, "hanvon.aebr.hvtextnote");
            mPackageType.put(12, "hanvon.aebr.hvRecordNote");
            mPackageType.put(13, "com.hanvon.inputmethod.hanvonime.pad");

            mPackageApkName.put("hanvon.aebr.hvLauncher", "hvLauncher.apk");
            mPackageApkName.put("hanvon.aebr.hvnote", "hvNote.apk");
            mPackageApkName.put("hanvon.aebr.hvreader", "hvReader.apk");
            mPackageApkName.put("com.yozo.office.read", "yozoOffice.apk");
            mPackageApkName.put("hanvon.aebr.hvimageshow", "hvImageShow.apk");
            mPackageApkName.put("hanvon.aebr.hvbookstore", "hvbookstore.apk");
            mPackageApkName.put("hanvon.aebr.hvddreader", "hvDDReader.apk");
            mPackageApkName.put("hanvon.aebr.wifitransfer", "hvWifiTransfer.apk");
            mPackageApkName.put("hanvon.aebr.hvaudio", "hvAudio.apk");
            mPackageApkName.put("hanvon.aebr.hvDict", "hvDict.apk");
            mPackageApkName.put("hanvon.aebr.hvWordList", "hvWordList.apk");
            mPackageApkName.put("hanvon.aebr.hvtextnote", "hvTextNote.apk");
            mPackageApkName.put("hanvon.aebr.hvRecordNote", "hvRecordNote.apk");
            mPackageApkName.put("com.hanvon.inputmethod.hanvonime.pad", "hvInputMethod.apk");
        }

        if (hvMemoryUtils.isHWLibraryRxt()) {
            mPackageType.put(14, "com.xys.xysreader");
            mPackageType.put(15, "com.oed.classroom.std");
            mPackageType.put(16, "com.namek.rayclass");

            mPackageApkName.put("com.xys.xysreader", "HwZwReader930UP.apk");
            mPackageApkName.put("com.oed.classroom.std", "OEdClassroomStd.apk");
            mPackageApkName.put("com.namek.rayclass", "oedmobile.apk");
        } else if (hvMemoryUtils.isHWLibraryQingD() || hvMemoryUtils.isHWLibraryThsw() || hvMemoryUtils.isHWLibraryDangjian()) {
            mPackageType.put(14, "com.xys.xysreader");
            mPackageType.put(15, "hanvon.aebr.hvbookaudio");
            mPackageType.put(16, "com.smart.arkbook");
            if (screen_size <= 1) {
                mPackageApkName.put("com.xys.xysreader", "HwZwReader60apk");
                mPackageApkName.put("hanvon.aebr.hvbookaudio", "hvBookAudio60.apk");
            } else {
                mPackageApkName.put("com.xys.xysreader", "HwZwReader930UP.apk");
                mPackageApkName.put("hanvon.aebr.hvbookaudio", "hvBookAudio930UP.apk");
            }
            mPackageApkName.put("com.smart.arkbook", "SarkBook.apk");

        } else if (hvMemoryUtils.isHWLibraryDangjian_SYJS()) {
            mPackageType.put(14, "com.xys.xysreader");
            mPackageApkName.put("com.xys.xysreader", "HwZwReader930UP.apk");
        } else if (hvMemoryUtils.isSGJCY()) {
            mPackageType.put(14, "com.hanvon.mreader");

            mPackageApkName.put("com.hanvon.mreader", "hvMeeting930UP.apk");
        } else if (hvMemoryUtils.isMettingSystem()) {
            mPackageType.put(14, "hanvon.aebr.hvnotemeeting");
            mPackageType.put(15, "com.hanvon.mreader");

            mPackageApkName.put("hanvon.aebr.hvnotemeeting", "hvMeetingNote930UP.apk");
            mPackageApkName.put("com.hanvon.mreader", "hvMeeting.apk");
        } else if (hvMemoryUtils.isBianJian()) {
            mPackageType.put(14, "com.xys.xysreader");
            if (screen_size <= 1) {
                mPackageApkName.put("com.xys.xysreader", "HwZwReader60apk");
            } else {
                mPackageApkName.put("com.xys.xysreader", "HwZwReader930UP.apk");
            }
        }
    }


}

