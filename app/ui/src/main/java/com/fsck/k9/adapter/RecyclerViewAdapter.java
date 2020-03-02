package com.fsck.k9.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fsck.k9.activity.MessageList;
import com.fsck.k9.mailstore.DisplayFolder;
import com.fsck.k9.ui.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {
    private List<DisplayFolder> mList;
    private MessageList mContent;
    private OnItemClickListener mOnItemClickListener;
    public RecyclerViewAdapter(MessageList messageList) {
        this.mContent=messageList;
    }
    public  void setData(List<DisplayFolder> list){
        this.mList=list;
    }
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContent).inflate(R.layout.folder_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        DisplayFolder folder = mList.get(position);
        String name = folder.getFolder().getName();
        if (name.equals("INBOX")){
            name=mContent.getResources().getString(R.string.special_mailbox_name_inbox);
        }
        if (name.equals("Outbox")){
            name=mContent.getResources().getString(R.string.special_mailbox_name_outbox);
        }
        holder.folder_tv.setText(name);
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

        private final TextView folder_tv;
        private final View checked_folder_item;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            folder_tv = itemView.findViewById(R.id.tv_folder);
            checked_folder_item = itemView.findViewById(R.id.checked_folder_item_cu);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
