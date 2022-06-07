package com.example.sample.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample.R;
import com.example.sample.localDb.AppSessionManager;
import com.example.sample.model.CommentDetailsModel;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsAdapterVH> {
    ArrayList<CommentDetailsModel> commentList;


    public CommentsAdapter(ArrayList<CommentDetailsModel> commentList) {
        this.commentList=commentList;
    }

    @NonNull
    @Override
    public CommentsAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments, parent, false);
        return new CommentsAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapterVH holder, int position) {
        Log.d("TAG", "onComments Adapter: "+commentList);
        holder.commentContent.setText(commentList.get(position).getContent());
        holder.commentCreatorName.setText(""+commentList.get(position).getUser_id());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
    class CommentsAdapterVH extends RecyclerView.ViewHolder {
        TextView commentContent,commentCreatorName;
        ImageView imageViewComment;
        public CommentsAdapterVH(@NonNull View itemView) {
            super(itemView);
            commentContent=itemView.findViewById(R.id.commentContent);
            commentCreatorName=itemView.findViewById(R.id.commentCreatorName);
            imageViewComment=itemView.findViewById(R.id.imageViewComment);
        }
    }
}
