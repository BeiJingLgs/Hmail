package com.fsck.k9.bean;

import java.io.Serializable;

public class FujianBean  implements Serializable {
    private String returnUri;
    private String mimeType;
    private String displayName;
    private String size;
    private String internalUri;
    private String date;
    private String file_path;

    public FujianBean(String returnUri, String mimeType, String displayName, String size, String internalUri, String date, String file_path) {
        this.returnUri = returnUri;
        this.mimeType = mimeType;
        this.displayName = displayName;
        this.size = size;
        this.internalUri = internalUri;
        this.date = date;
        this.file_path = file_path;
    }

    public String getReturnUri() {
        return returnUri;
    }

    public void setReturnUri(String returnUri) {
        this.returnUri = returnUri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getInternalUri() {
        return internalUri;
    }

    public void setInternalUri(String internalUri) {
        this.internalUri = internalUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
}
