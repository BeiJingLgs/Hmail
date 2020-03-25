package com.fsck.k9.util;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fsck.k9.ui.R;


public class CustomDialog extends Dialog implements View.OnClickListener {

    //声明xml文件里的组件

    private TextView tv_title,tv_message;

    private Button bt_cancel,bt_confirm;



    //声明xml文件中组件中的text变量，为string类，方便之后改

    private String title,message;

    private String cancel,confirm;
    private int  btn;
    private int  bar;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件

    private IOnCancelListener cancelListener;

    private IOnConfirmListener confirmListener;
    private LinearLayout ll_update;
    private ProgressBar pg_bar;
    private int bar_max;
    private int progress;

    //设置四个组件的内容

    public void setTitle(String title) {

        this.title = title;

    }
    public void Max(int max) {
        this.bar_max=max;
    }

    public void setMax() {
       if (pg_bar!=null){
           pg_bar.setMax(bar_max);
       }
    }
    public void Progress(int progress) {
    this.progress=progress;
    }
    public void setProgress() {
        if (pg_bar!=null){
            pg_bar.setProgress(progress);
        }
    }
    public void setMessage(String message) {

        this.message = message;

    }
    public void setMessagetext() {
        tv_message.setText(message);
    }
    //隐藏或者显示btn
    public  void setYcorShow_Btn(int btn){
        this.btn=btn;
    }
    public  void setYcorShow_bar(int bar){
        this.bar=bar;
    }


    public void setCancel(String cancel, IOnCancelListener cancelListener) {

        this.cancel = cancel;

        this.cancelListener=cancelListener;

    }

    public void setConfirm(String confirm, IOnConfirmListener confirmListener){

        this.confirm=confirm;

        this.confirmListener=confirmListener;

    }



    //CustomDialog类的构造方法

    public CustomDialog(@NonNull Context context) {

        super(context);

    }

    public CustomDialog(@NonNull Context context, int themeResId) {

        super(context, themeResId);

    }



    //在app上以对象的形式把xml里面的东西呈现出来的方法！

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //为了锁定app界面的东西是来自哪个xml文件

        setContentView(R.layout.custom_dialog);



//        //设置弹窗的宽度
//
//        WindowManager m = getWindow().getWindowManager();
//
//        Display d = m.getDefaultDisplay();
//
//        WindowManager.LayoutParams p =getWindow().getAttributes();
//
//        Point size = new Point();
//
//        d.getSize(size);
//
//        p.width = (int)(size.x * 0.5);//是dialog的宽度为app界面的80%
//
//        getWindow().setAttributes(p);



        //找到组件
        ll_update = findViewById(R.id.ll_update);

        pg_bar = findViewById(R.id.pg_bar);

        tv_title=findViewById(R.id.tv_title);

        tv_message=findViewById(R.id.tv_message);

        bt_cancel=findViewById(R.id.bt_cancel1);

        bt_confirm=findViewById(R.id.bt_confirm1);

        ll_update.setVisibility(btn);
        pg_bar.setVisibility(bar);

        //设置组件对象的text参数

        if (!TextUtils.isEmpty(title)){

            tv_title.setText(title);

        }

        if (!TextUtils.isEmpty(message)){

            tv_message.setText(message);

        }

        if (!TextUtils.isEmpty(cancel)){

            bt_cancel.setText(cancel);

        }



        //为两个按钮添加点击事件

        bt_confirm.setOnClickListener(this);

        bt_cancel.setOnClickListener(this);

    }



    //重写onClick方法

    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.bt_cancel1) {
            if (cancelListener != null) {
                cancelListener.onCancel(this);
            }
            dismiss();
        } else if (id == R.id.bt_confirm1) {
            if (confirmListener != null) {

                confirmListener.onConfirm(this);

            }

            dismiss();//按钮按之后会消失
        }

    }



    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口

    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！

    public interface IOnCancelListener{

        void onCancel(CustomDialog dialog);

    }

    public interface IOnConfirmListener{

        void onConfirm(CustomDialog dialog);

    }

}
