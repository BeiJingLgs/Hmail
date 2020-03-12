package com.fsck.k9.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.fsck.k9.db.FujianBeanDB.BIAO_NAME;
import static com.fsck.k9.db.FujianBeanDB.DATE;
import static com.fsck.k9.db.FujianBeanDB.DISPLAYNAME;
import static com.fsck.k9.db.FujianBeanDB.FILE_SIZE;
import static com.fsck.k9.db.FujianBeanDB.INTERNALURI;
import static com.fsck.k9.db.FujianBeanDB.MIMETYPE;
import static com.fsck.k9.db.FujianBeanDB.RETURNURI;

public class FujianBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public FujianBaseHelper(@Nullable Context context) {
        super(context,FujianBeanDB.DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +BIAO_NAME + "(" +
                "_id integer primary key autoincrement, " +
                RETURNURI + "," +
                MIMETYPE + "," +
                FILE_SIZE + "," +
                DISPLAYNAME + "," +
                DATE + "," +
                INTERNALURI +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
