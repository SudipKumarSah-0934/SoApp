package com.example.sample.view.adapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.sample.utilities.Constants.PRODUCT_FOLDER;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.sample.BuildConfig;
import com.example.sample.interfaces.OnLikeCommentListener;
import com.example.sample.localDb.AppSessionManager;
import com.example.sample.model.CommentDetailsModel;
import com.example.sample.model.PostListModel;
import com.example.sample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsAdapterVH> {
    ArrayList<PostListModel> products;
    ArrayList<CommentDetailsModel> commentList;
    Context context;
    AppSessionManager appSessionManager;
    Dialog dialog;
    OnLikeCommentListener onLikeCommentListener;
    HashMap<String, Integer> likeUnlikeMap;
    Integer mPos;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public PostsAdapter(List<PostListModel> list,
                        Context context, OnLikeCommentListener onLikeCommentListener, HashMap<String, Integer> likeUnlikeMap) {
        this.products = (ArrayList<PostListModel>) list;
        this.context = context;
        this.onLikeCommentListener = onLikeCommentListener;
        this.likeUnlikeMap = likeUnlikeMap;
    }

    @NonNull
    @Override
    public PostsAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts, parent, false);
        appSessionManager = new AppSessionManager(context);
        dialog = new Dialog(context, R.style.TransparentProgressDialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_layout);
        return new PostsAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapterVH holder,int position) {
        holder.postCreatorName.setText(products.get(position).getFirst_name() + " " + products.get(position).getLast_name());
        holder.postContent.setText(products.get(position).getContent());
        if (products.get(position).getPicture() != null) {
            Glide.with(holder.itemView)
                    .load(products.get(position).getPicture())
                    .fitCenter()
                    .placeholder(context.getResources().getDrawable(R.drawable.placeholder_img))
                    .into(holder.postImg);
        } else {
            holder.postImg.setVisibility(View.GONE);
        }
        if (products.get(position).getPicture() != null) {
            Glide.with(holder.itemView)
                    .load(products.get(position).getPicture())
                    .fitCenter()
                    .placeholder(context.getResources().getDrawable(R.drawable.placeholder_img))
                    .into(holder.imageViewPost);
        } else {
            holder.postImg.setVisibility(View.VISIBLE);
        }


        if (products.get(position).getIslike()) {
            holder.likeCard.setBackgroundColor(Color.parseColor("#4267B2"));
        } else {
            holder.likeCard.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.totalLike.setText(Integer.parseInt(products.get(position).getTotal_like())+" likes ");
        holder.likeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (products.get(position).getIslike()) {
                    Log.d("TAG", "onClick:"+ Integer.parseInt(products.get(position).getTotal_like()));
                    holder.totalLike.setText((Integer.parseInt(products.get(position).getTotal_like()))-1+" !!!!!likes ");
                    products.get(position).setIslike(false);

                } else {
                    products.get(position).setIslike(true);
                }
                notifyDataSetChanged();
                onLikeCommentListener.onCardClick(products.get(position).getId());

            }
        });
        if (appSessionManager.getUserDetails().get(AppSessionManager.USER_ID).equals(products.get(position).getUser_id() + "")){
            holder.menu.setVisibility(View.VISIBLE);
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPos = position;
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater menuInflater = new MenuInflater(context);
                    menuInflater.inflate(R.menu.menu_item, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new moreMenuClickListener());
                    popupMenu.show();
                }
            });
        }else {
            holder.menu.setVisibility(View.GONE);
        }
        holder.totalComment.setText(products.get(position).getTotal_comment()+" comments");
            holder.totalComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(products.get(position).getTotal_comment().toString())==0){
                        Toast.makeText(context, "No comments", Toast.LENGTH_SHORT).show();
                    }else if (!holder.commentRecycler.isShown()) {
                        holder.commentHeader.setVisibility(View.VISIBLE);
                        holder.commentRecycler.setVisibility(View.VISIBLE);
                        holder.line.setVisibility(View.VISIBLE);
                        commentList = (ArrayList<CommentDetailsModel>) products.get(position).getComment();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(
                                holder.commentRecycler.getContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                        );
                        layoutManager.setInitialPrefetchItemCount(commentList.size());

                        // Create sub item view adapter
                        CommentsAdapter subItemAdapter = new CommentsAdapter(commentList);

                        holder.commentRecycler.setLayoutManager(layoutManager);
                        holder.commentRecycler.setAdapter(subItemAdapter);
                        holder.commentRecycler.setRecycledViewPool(viewPool);
                    } else {
                        holder.commentRecycler.setVisibility(View.GONE);
                        holder.commentHeader.setVisibility(View.GONE);
                        holder.line.setVisibility(View.GONE);

                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class PostsAdapterVH extends RecyclerView.ViewHolder {
        TextView postCreatorName, postContent,totalLike,totalComment,commentHeader;
        ImageView postImg, img_like, img_comment,imageViewPost;
        CardView likeCard, commentCard;
        ConstraintLayout commentCons;
        ImageButton menu;
        View line;
        RecyclerView commentRecycler;

        public PostsAdapterVH(@NonNull View itemView) {
            super(itemView);
            postCreatorName = itemView.findViewById(R.id.postCreatorName);
            postContent = itemView.findViewById(R.id.postContent);
            postImg = itemView.findViewById(R.id.postImg);
            likeCard = itemView.findViewById(R.id.likeCard);
            commentCard = itemView.findViewById(R.id.commentCard);
            img_like = itemView.findViewById(R.id.img_like);
            img_comment = itemView.findViewById(R.id.img_comment);
            commentCons = itemView.findViewById(R.id.comment_cons);
            menu = itemView.findViewById(R.id.postMenu);
            totalLike = itemView.findViewById(R.id.like_count);
            totalComment = itemView.findViewById(R.id.comment_count);
            commentRecycler=itemView.findViewById(R.id.commentRecycler);
            commentHeader=itemView.findViewById(R.id.commentHead);
            line=itemView.findViewById(R.id.view3);
            imageViewPost=itemView.findViewById(R.id.imageViewPost);
        }
    }

    class moreMenuClickListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    onLikeCommentListener.onUpdatePost(products.get(mPos).getId());
                    break;
                case R.id.action_delete:
                    onLikeCommentListener.onDeletePost(products.get(mPos).getId());
                    break;
            }
            return false;
        }
    }
}
