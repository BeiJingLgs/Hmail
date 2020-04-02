package com.fsck.k9.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import static com.fsck.k9.db.FujianBeanDB.BIAO_NAME_MESSAGE;
public class MessageListItemHepler  extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public MessageListItemHepler(@Nullable Context context) {
        super(context,FujianBeanDB.DB_NAME_MESSAGE, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +BIAO_NAME_MESSAGE + "(" +
                "_id integer primary key autoincrement, " +
                FujianBeanDB.MESSAGELISTTOSTRING +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
