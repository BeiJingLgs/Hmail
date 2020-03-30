package com.fsck.k9.adapter;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fsck.k9.Account;
import com.fsck.k9.activity.MessageList;
import com.fsck.k9.ui.R;

import java.util.List;

public class HeaderRecyclerView extends RecyclerView.Adapter<HeaderRecyclerView.MyHolder> {
    private MessageList mContent;
    private List<Account> mList;
    private OnItemClickListener mOnItemClickListener;
    private HeaderRecyclerView.CallBack mCallBack;

    public void setCallBack(HeaderRecyclerView.CallBack CallBack) {
        this.mCallBack = CallBack;
    }

    public interface CallBack {
        <T extends Object> void convert(HeaderRecyclerView.MyHolder holder, T bean, int position);
    }

    public HeaderRecyclerView(MessageList messageList, List<Account> accounts) {
        this.mContent = messageList;
        this.mList = accounts;
    }

    public void setData(List<Account> accounts) {
        this.mList = accounts;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.header_name_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Account account = mList.get(position);
        String email = account.getEmail();
        String description = account.getDescription();
        holder.header_item_name.setText(email);
        if (mCallBack != null)
            mCallBack.convert(holder, account, position);


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
        return mList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public final TextView header_item_name;
        public final LinearLayout header_ll;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            header_ll = itemView.findViewById(R.id.header_ll);
            header_item_name = itemView.findViewById(R.id.header_item_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
