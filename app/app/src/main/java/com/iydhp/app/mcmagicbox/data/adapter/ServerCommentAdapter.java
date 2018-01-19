package com.iydhp.app.mcmagicbox.data.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.model.Comment;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.databinding.LayoutServerDetailCommentItemBinding;
import com.iydhp.app.mcmagicbox.databinding.LayoutServerListItemBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ServerCommentAdapter extends BaseQuickAdapter<Comment, ServerCommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> models;

    public ServerCommentAdapter(Context context, List<Comment> models){
        super(R.layout.layout_server_detail_comment_item, models);
        this.context = context;
        this.models = models;
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        LayoutServerDetailCommentItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

    @Override
    protected void convert(CommentViewHolder helper, final Comment item) {
        Picasso.with(context).load(item.getUser().getAvatar()).into(helper.getBinding().activityServerDetailCivHead);
        helper.getBinding().activityServerDetailRbUsercomment.setRating(item.getScore().floatValue());
        helper.getBinding().activityServerDetailTvNickname.setText(item.getUser().getNickname());
        helper.getBinding().activityServerDetailTvUsercomment.setText(item.getText());
        final Date date = new Date(item.getTime() * 1000);
        helper.getBinding().activityServerDetailTvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
        helper.getBinding().activityServerDetailRlUsercomment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage("是否要举报这条评论")
                        .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getApp().getRetrofit().create(ApiService.class)
                                        .accusationComment(item.getId())
                                        .enqueue(new ApiCallback<ApiResponse>(false) {
                                            @Override
                                            public void onSuccess(ApiResponse response) {
                                                super.onSuccess(response);
                                                Toast.makeText(context, "举报成功", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                super.onFailure(t);
                                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            }
                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                return;
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (models != null){
            return models.size();
        }else{
            return 0;
        }
    }

    protected class CommentViewHolder extends BaseViewHolder {
        public CommentViewHolder(View view) {
            super(view);
        }

        public LayoutServerDetailCommentItemBinding getBinding() {
            return (LayoutServerDetailCommentItemBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }

    }

}
