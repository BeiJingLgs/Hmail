package com.fsck.k9.ui;

import com.fsck.k9.mailstore.DisplayFolder;

import java.util.List;

public class MessageEvent {
    private List<DisplayFolder> message;

    public List<DisplayFolder> getMessage() {
        return message;
    }

    public void setMessage( List<DisplayFolder> message) {
        this.message = message;
    }
}
