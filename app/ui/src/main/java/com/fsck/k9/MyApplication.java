package com.fsck.k9;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.fsck.k9.db.FujianBaseHelper;

public class MyApplication extends Application {

//    private DaoSession session;
    private static MyApplication instance;
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        setDatabase()  ;
    }
    public  static  MyApplication getInstance(){
        return  instance ;
    }
    /**
     * 设置数据库
     */
    public void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级
        FujianBaseHelper helper = new FujianBaseHelper(this);
        db = helper.getWritableDatabase();
//        try {
//        } catch(SQLiteException e){
//            e.printStackTrace();
//        } finally {
//            if(db != null && db.isOpen())
//                db.close();
//        }
    }
//    public DaoSession getSession(){
//        return session;
//    }
    public  SQLiteDatabase getDb(){
        return db;
    }
}
