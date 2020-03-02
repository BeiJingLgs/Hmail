package com.fsck.k9.adapter;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fsck.k9.Account;
import com.fsck.k9.activity.MessageList;
import com.fsck.k9.ui.R;

import java.util.List;

public class HeaderRecyclerView  extends RecyclerView.Adapter<HeaderRecyclerView.MyHolder>{
    private  MessageList mContent;
    private  List<Account> mList;
    private OnItemClickListener mOnItemClickListener;

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    private Boolean  isChecked;
    public HeaderRecyclerView(MessageList messageList, List<Account> accounts) {
     this.mContent=messageList;
     this.mList=accounts;
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
        Log.i("tag","0000000000000"+email+"22222"+description);
        holder.header_item_name.setText(email);





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

    class MyHolder extends  RecyclerView.ViewHolder{

        private final TextView header_item_name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            header_item_name = itemView.findViewById(R.id.header_item_name);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
