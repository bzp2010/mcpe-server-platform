package com.iydhp.app.mcmagicbox.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiResponsePage;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.adapter.UserFavouriteAdapter;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.data.model.api.UserFavourite;
import com.iydhp.app.mcmagicbox.databinding.ActivityUserFavouriteBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

public class UserFavouriteActivity extends BaseActivity {

    private ActivityUserFavouriteBinding binding;
    private long page = 1;
    private boolean hasMore = true;
    private ArrayList<ServerCardItem> serverList = new ArrayList<>();
    private UserFavouriteAdapter adapter = new UserFavouriteAdapter(this, serverList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_favourite);
        initView();
    }

    private void initView() {
        setSupportActionBar(binding.activityUserFavouriteTbHeader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的收藏");
        binding.activityUserFavouriteSrlList.setRefreshHeader(new MaterialHeader(this));
        binding.activityUserFavouriteSrlList.setRefreshFooter(new ClassicsFooter(this));
        binding.activityUserFavouriteSrlList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                serverList.clear();
                loadData(1, false, new DataLoadListener() {
                    @Override
                    public void finish() {
                        binding.activityUserFavouriteSrlList.finishRefresh(100);
                    }
                });

            }
        });
        binding.activityUserFavouriteSrlList.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData(page + 1, true, new DataLoadListener() {
                    @Override
                    public void finish() {
                        binding.activityUserFavouriteSrlList.finishLoadmore(100);
                    }
                });
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(UserFavouriteActivity.this, ServerDetailActivity.class);
                intent.putExtra("id", serverList.get(position).getId());
                startActivity(intent);
            }
        });
        binding.activityUserFavouriteRvList.setLayoutManager(new LinearLayoutManager(this));
        binding.activityUserFavouriteRvList.setItemAnimator(new DefaultItemAnimator());
        binding.activityUserFavouriteRvList.setAdapter(adapter);
        binding.activityUserFavouriteRvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        binding.activityUserFavouriteRvList.setHasFixedSize(true);
        loadData(page, false, null);

    }

    private void loadData(final long page, boolean isLoadMore, final DataLoadListener listener) {
        if (!UserFavouriteActivity.this.hasMore && isLoadMore){
            Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
            if (listener != null){
                listener.finish();
            }
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "", "加载中");
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {return;}
        });
        if (!(page == 1 && !isLoadMore && listener == null)){
            dialog.dismiss();
        }

        App.getRetrofit()
                .create(ApiService.class)
                .listFavourite(page)
                .enqueue(new ApiCallback<ApiResponse<ApiResponsePage<UserFavourite[]>>>(false) {
                    @Override
                    public void onSuccess(ApiResponse<ApiResponsePage<UserFavourite[]>> response) {
                        super.onSuccess(response);
                        if(response!=null && response.getData() != null){
                            ApiResponsePage<UserFavourite[]> pageModel = response.getData();
                            for (int i = 0; i < pageModel.data.length; i++){
                                serverList.add(new ServerCardItem(pageModel.data[i].server));
                            }
                            adapter.notifyDataSetChanged();
                            UserFavouriteActivity.this.page = pageModel.current_page;
                            if (pageModel.last_page <= page){
                                UserFavouriteActivity.this.hasMore = false;
                            }
                            dialog.dismiss();
                            if (listener != null){
                                listener.finish();
                            }
                        }else{
                            onFailure(new Exception());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        Toast.makeText(UserFavouriteActivity.this, "加载错误", Toast.LENGTH_SHORT).show();
                        if (listener != null){
                            listener.finish();
                        }
                    }

                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private interface DataLoadListener{
        public void finish();
    }

}
