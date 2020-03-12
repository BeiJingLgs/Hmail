/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fsck.k9.util;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class MimeTypes {

	private Map<String, String> mMimeTypes;
    String[] mCursorCols = new String[] {
            "audio._id AS _id",             // index must match IDCOLIDX below
            MediaStore.Audio.Media.MIME_TYPE,

    };
	public MimeTypes() {
		mMimeTypes = new HashMap<String,String>();
	}
	
	public void put(String type, String extension) {
		// Convert extensions to lower case letters for easier comparison
		/////extension = extension.toLowerCase();

		if (!hvMemoryUtils.isHWLibrary() && !hvMemoryUtils.isHWLibraryThsw()
				&& !hvMemoryUtils.isHWLibraryQingD() 
				&& !hvMemoryUtils.isHWLibraryNxyc() 
				&& !hvMemoryUtils.isHWLibraryDangjian()
				&& !hvMemoryUtils.isHWLibraryDangjian_SYJS()
				&& !hvMemoryUtils.isHWLibraryQingDNew()
				&& !hvMemoryUtils.isHWLibraryAndSarkBook()
				&& (type.equalsIgnoreCase(".ofd") 
					||type.equalsIgnoreCase(".cebx"))
				){
			return;
		}
		
		if (!hvMemoryUtils.isHWLibrary() && !hvMemoryUtils.isHWLibraryThsw()
				&& !hvMemoryUtils.isHWLibraryQingD() 
				&& !hvMemoryUtils.isHWLibraryNxyc() 
				&& !hvMemoryUtils.isHWLibraryDangjian()
				&& !hvMemoryUtils.isHWLibraryDangjian_SYJS()
				&& !hvMemoryUtils.isHWLibraryQingDNew()
				&& !hvMemoryUtils.isHWLibraryAndSarkBook()
				&& !hvMemoryUtils.isBianJian()
				&& type.equalsIgnoreCase(".ofdx")){
			return;
		}
		
		if (!hvMemoryUtils.hasMusicFuction() && 
				(type.equalsIgnoreCase(".amr") || type.equalsIgnoreCase(".mp3"))){
			return;
		}

		mMimeTypes.put(type, extension);
	}
	
	public String getMimeType(String afilename) {
		String filename = afilename.toLowerCase(Locale.getDefault());
		String extension = FileUtils.getExtension(filename);
		extension = extension.toLowerCase(Locale.getDefault());
		if(extension.length() > 0){
			String mimetype = mMimeTypes.get(extension);
			if(mimetype != null){
				return mimetype;
			}
		}
		return null;
	}
	public String getMimeType(File afilename,Context context) {
		String filename = afilename.getName().toLowerCase(Locale.getDefault());
		String extension = FileUtils.getExtension(filename);
		if(afilename.getName().contains("3gpp")){
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(afilename.getAbsolutePath());
	         String where = MediaStore.Audio.Media.DATA + "=?";
	         String []selectionArgs = new String[] { afilename.getAbsolutePath()};
	      try {
	          Cursor mCursor = context.getContentResolver().query(uri, mCursorCols, where, selectionArgs, null);
	          //String a = mCursor.getString(0);
	          if  (mCursor != null) {
	              if (mCursor.getCount() == 0) {
	                  mCursor.close();
	                  mCursor = null;
	              } else {
	                  mCursor.moveToNext();
	                  int num = mCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
	                  String a = mCursor.getString(num);
	                  return a;
	              }
	          }
	      } catch (UnsupportedOperationException ex) {
	      }
		}
		extension = extension.toLowerCase(Locale.getDefault());
		String mimetype = mMimeTypes.get(extension);
		return mimetype;
	}
	
	
	public void getAllMimeType(HashSet<String> mimeTypesSet) 
	{
		mimeTypesSet.addAll(mMimeTypes.keySet());
	}
}
