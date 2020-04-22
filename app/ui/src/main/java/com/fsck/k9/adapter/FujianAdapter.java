package com.fsck.k9.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fsck.k9.bean.FujianBean;
import com.fsck.k9.mail.internet.Viewable;
import com.fsck.k9.ui.R;
import com.fsck.k9.ui.helper.SizeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class FujianAdapter extends RecyclerView.Adapter<FujianAdapter.MyViewHolder> {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<FujianBean> mList;

    public FujianAdapter(Context context, List<FujianBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<FujianBean> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fujian_adapter_item, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FujianBean bean = mList.get(position);
        String displayName = bean.getDisplayName();
        String returnUri = bean.getReturnUri();
        String size = bean.getSize();
        String date = bean.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format1 = format.format(Long.parseLong(date));
        holder.fujian_size.setText(new SizeFormatter(mContext.getResources()).formatSize(Long.parseLong(size)));
        holder.fujian_name.setText(displayName);
        holder.fujian_date.setText(format1);

        //判断是否设置了监听器
        if (mOnItemClickListener != null) {
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() > 0 ? mList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_icon;
        private final TextView fujian_name;
        private final TextView fujian_date;
        private final TextView fujian_size;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);
            fujian_name = itemView.findViewById(R.id.fujian_name);
            fujian_date = itemView.findViewById(R.id.fujian_date);
            fujian_size = itemView.findViewById(R.id.fujian_size);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
