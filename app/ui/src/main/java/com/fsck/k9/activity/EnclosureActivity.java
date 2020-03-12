package com.fsck.k9.activity;

import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.RadioButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fsck.k9.MyApplication;
import com.fsck.k9.adapter.FujianAdapter;
import com.fsck.k9.bean.FujianBean;
import com.fsck.k9.db.FujianBeanDB;
import com.fsck.k9.ui.R;
import com.fsck.k9.util.Filepath;
import com.fsck.k9.util.OpenFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnclosureActivity extends K9Activity implements View.OnClickListener {

    private List<FujianBean> mList;
    private RecyclerView mRecyclerView;
    private final String FILEPATH = "/storage/emulated/0/我的文档/附件管理/";
    private RadioButton rb_one;
    private RadioButton rb_two;
    private RadioButton rb_three;
    private RadioButton rb_four;
    private List<FujianBean> imgList;
    private List<FujianBean> applicationList;
    private List<FujianBean> qtList;
    private FujianAdapter adapter;
    private int num = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.enclosure_activity);
        /**
         * 获取data
         */
        initData();
        /**
         * 绑定数据
         */
        initView();
        initRecyclerView();
        initOnclick();

    }

    private void initOnclick() {
        adapter.setOnItemClickListener(new FujianAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (num == 0) {
                    OpenApk(mList, position);
                } else if (num == 1) {
                    OpenApk(applicationList, position);
                } else if (num == 2) {
                    OpenApk(imgList, position);
                } else {
                    OpenApk(qtList, position);
                }
            }
        });
    }

    private void OpenApk(List<FujianBean> mList, int position) {
        FujianBean fujianBean = mList.get(position);
        String returnUri = fujianBean.getReturnUri();
        new OpenFile(EnclosureActivity.this).openFile(new File(returnUri));
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new FujianAdapter(this, mList);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        rb_one = findViewById(R.id.RB_one);
        rb_two = findViewById(R.id.RB_two);
        rb_three = findViewById(R.id.RB_three);
        rb_four = findViewById(R.id.RB_four);
        rb_one.setOnClickListener(this);
        rb_two.setOnClickListener(this);
        rb_three.setOnClickListener(this);
        rb_four.setOnClickListener(this);
        buttonOne();
    }

    private void initData() {
        Cursor cursor = MyApplication.getInstance().getDb().query(FujianBeanDB.BIAO_NAME, null, null, null, null, null, null, null);
        mList = new ArrayList<>();
        imgList = new ArrayList<>();
        applicationList = new ArrayList<>();
        qtList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String returnuri = cursor.getString(cursor.getColumnIndex(FujianBeanDB.RETURNURI));
            File installFile = new File(returnuri);
            if (installFile.exists()) {
                String mimetype = cursor.getString(cursor.getColumnIndex(FujianBeanDB.MIMETYPE));
                String date = cursor.getString(cursor.getColumnIndex(FujianBeanDB.DATE));
                String size = cursor.getString(cursor.getColumnIndex(FujianBeanDB.FILE_SIZE));
                String internaluri = cursor.getString(cursor.getColumnIndex(FujianBeanDB.INTERNALURI));
                String displayname = returnuri.substring(FILEPATH.length(), returnuri.length());
                FujianBean fujianBean = new FujianBean(returnuri, mimetype, displayname, size, internaluri, date, returnuri);
                if (displayname.endsWith(".png") || displayname.endsWith(".gif") || displayname.endsWith(".jpg") || displayname.endsWith(".jpeg") || displayname.endsWith(".bmp")) {
                    imgList.add(fujianBean);
                } else if (displayname.endsWith(".doc") || displayname.endsWith(".docx") || displayname.endsWith(".ppt")
                        || displayname.endsWith(".pptx") || displayname.endsWith(".xls") || displayname.endsWith(".xlsx")
                        || displayname.endsWith(".txt") || displayname.endsWith(".htxt") || displayname.endsWith(".pdf")
                        || displayname.endsWith(".epub") || displayname.endsWith(".chm") || displayname.endsWith(".hveb")
                        || displayname.endsWith(".heb") || displayname.endsWith(".mobi") || displayname.endsWith(".fb2")
                        || displayname.endsWith(".htm") || displayname.endsWith(".html") || displayname.endsWith(".php")
                        || displayname.endsWith(".apk") || displayname.endsWith(".ofdx") || displayname.endsWith(".cebx")
                        || displayname.endsWith(".ofd")
                ) {
                    applicationList.add(fujianBean);
                } else {
                    qtList.add(fujianBean);
                }
                mList.add(fujianBean);
            } else {
                MyApplication.getInstance().getDb().delete(FujianBeanDB.BIAO_NAME, FujianBeanDB.RETURNURI + "=?", new String[]{returnuri});
            }

        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
//        Drawable dra= getResources().getDrawable(R.drawable.heng);
//        dra.setBounds( 0, 0, dra.getMinimumWidth(),dra.getMinimumHeight());
//        rb_one.setCompoundDrawables(null, null, null, dra);
//        rb_two.setCompoundDrawables(null, null, null, null);
//        rb_three.setCompoundDrawables(null, null, null, null);
//        rb_four.setCompoundDrawables(null, null, null, null);
        int id = v.getId();
        if (id == R.id.RB_one) {
            num = 0;
            buttonOne();
            adapter.setData(mList);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.RB_two) {
            num = 1;
            buttonTwo();
            adapter.setData(applicationList);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.RB_three) {
            num = 2;
            buttonThree();
            adapter.setData(imgList);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.RB_four) {
            num = 3;
            buttonFour();
            adapter.setData(qtList);
            adapter.notifyDataSetChanged();
        }
    }

    private void buttonFour() {
        rb_one.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_two.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_three.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_four.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        rb_one.setTextSize(30);
        rb_two.setTextSize(30);
        rb_three.setTextSize(30);
        rb_four.setTextSize(34);
    }

    private void buttonThree() {
        rb_one.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_two.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_three.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        rb_four.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_one.setTextSize(30);
        rb_two.setTextSize(30);
        rb_three.setTextSize(34);
        rb_four.setTextSize(30);
    }

    private void buttonTwo() {
        rb_one.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_two.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        rb_three.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_four.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_one.setTextSize(30);
        rb_two.setTextSize(34);
        rb_three.setTextSize(30);
        rb_four.setTextSize(30);
    }

    private void buttonOne() {
        rb_one.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        rb_two.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_three.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_four.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
        rb_one.setTextSize(34);
        rb_two.setTextSize(30);
        rb_three.setTextSize(30);
        rb_four.setTextSize(30);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
