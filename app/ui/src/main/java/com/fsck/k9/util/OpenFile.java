package com.fsck.k9.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class OpenFile {
    private File currentDirectory = new File("");
    private Context mContext;
    private final MimeTypes mMimeTypes;

    public OpenFile(Context mContext) {
        this.mContext = mContext;
        mMimeTypes = hvMimeTypeFileUtils.getNormalFileListMimeType(mContext, currentDirectory.getAbsolutePath(), false);
    }

    public void openFile(File aFile) {
        if (!aFile.exists()) {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        //Uri data = FileUtils.getUri(aFile);

        String type = null;
        type = mMimeTypes.getMimeType(aFile.getName().toLowerCase(Locale.getDefault()));


        if (type == null) {
            type = "null/null/null/null";
        } else if (type != null) {
            String[] parts = type.split("/");
            if (parts[0].equals("application") && parts[1].equals("packageinstaller")) {
                //intent.setDataAndType(data, type);
//        		 hvMemoryUtils.sendMyBroadcastAndStr(FileManagerActivity.this,"hanvon.intent.action.installApp",
//        				 "apkpath", aFile.getPath());
                hvApkUtils.updateApk(aFile, mContext);
                return;

            } else {
                intent.putExtra("filePath", aFile.getPath());
                intent.putExtra("openfile", aFile.getPath());

                //该应用的包名
                String pkg = parts[2];
                //应用的主activity类
                String cls = parts[3];

                if (hvMemoryUtils.isMettingSystem() && false) {
                    pkg = "com.hanvon.mreader";
                    cls = "hanvon.aebr.pdfreader.PdfReaderActivity";
                    intent.putExtra("ISLOCAL", true);
                    intent.putExtra("FILE_PATH", aFile.getPath());
                }
                if (pkg.equalsIgnoreCase("com.yozo.office.read")) {
                    intent.putExtra("File_Path", aFile.getPath());
                    RecentReadingDB.addOfficeRecentReadingInfo(mContext, aFile.getPath());
                }
                ComponentName componet = new ComponentName(pkg, cls);
                intent.setComponent(componet);
            }
        }
        hvApkUtils.startActivityFromIntent(mContext, intent);
    }
}
