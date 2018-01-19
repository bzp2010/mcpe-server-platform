package com.iydhp.app.mcmagicbox.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.data.model.ServerListItem;
import com.iydhp.app.mcmagicbox.databinding.LayoutServerListItemBinding;
import com.iydhp.app.mcmagicbox.view.activity.ServerDetailActivity;
import com.iydhp.app.mcmagicbox.view.activity.UserFavouriteActivity;
import com.iydhp.app.mcmagicbox.view.custom.ServerInfoCard;
import com.socks.library.KLog;

import java.util.List;


public class UserFavouriteAdapter extends BaseQuickAdapter<ServerCardItem, UserFavouriteAdapter.ServerListViewHolder> {

    private Context context;

    public UserFavouriteAdapter(Context context, List<ServerCardItem> models){
        super(R.layout.layout_server_list_item, models);
        this.context = context;
    }

    @Override
    protected void convert(ServerListViewHolder helper, ServerCardItem item) {
        helper.getBinding().layoutServerListItemSic.setData(item);
        helper.getBinding().layoutServerListItemSic.setOnCardClickListener(new ServerInfoCard.OnCardClickListener() {
            @Override
            public void onCardClick(View view, ServerCardItem data) {
                Intent intent = new Intent(context, ServerDetailActivity.class);
                intent.putExtra("id", data.getId());
                context.startActivity(intent);
            }
        });
        helper.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (getData() != null){
            return getData().size();
        }else{
            return 0;
        }
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        LayoutServerListItemBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

    protected class ServerListViewHolder extends BaseViewHolder {
        public ServerListViewHolder(View view) {
            super(view);
        }

        public LayoutServerListItemBinding getBinding() {
            return (LayoutServerListItemBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }

    }

}
