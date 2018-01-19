package com.iydhp.app.mcmagicbox.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiResponsePage;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.adapter.ServerCommentAdapter;
import com.iydhp.app.mcmagicbox.data.model.Comment;
import com.iydhp.app.mcmagicbox.databinding.ActivityServerCommentBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

public class ServerCommentActivity extends BaseActivity {
    private long id;
    private long page = 1;
    private boolean hasMore = true;

    private ArrayList<Comment> commentList = new ArrayList<>();
    private ServerCommentAdapter adapter = new ServerCommentAdapter(this, commentList);

    private ActivityServerCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_comment);
        this.id = getIntent().getLongExtra("id", 1l);
        initView();
    }

    private void initView() {
        setSupportActionBar(binding.activityServerCommentTbHeader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("全部评分");
        binding.activityServerCommentSrlList.setRefreshHeader(new MaterialHeader(this));
        binding.activityServerCommentSrlList.setRefreshFooter(new ClassicsFooter(this));
        binding.activityServerCommentSrlList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                commentList.clear();
                loadData(1, false, new ServerCommentActivity.DataLoadListener() {
                    @Override
                    public void finish() {
                        binding.activityServerCommentSrlList.finishRefresh(0);
                    }
                });

            }
        });
        binding.activityServerCommentSrlList.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData(page + 1, true, new ServerCommentActivity.DataLoadListener() {
                    @Override
                    public void finish() {
                        binding.activityServerCommentSrlList.finishLoadmore(0);
                    }
                });
            }
        });
        binding.activityServerCommentRvList.setLayoutManager(new LinearLayoutManager(this));
        binding.activityServerCommentRvList.setItemAnimator(new DefaultItemAnimator());
        binding.activityServerCommentRvList.setAdapter(adapter);
        binding.activityServerCommentRvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.activityServerCommentRvList.setHasFixedSize(true);
        loadData(page, false, null);
    }

    private void loadData(final long page, boolean isLoadMore, final ServerCommentActivity.DataLoadListener listener) {
        if (!ServerCommentActivity.this.hasMore && isLoadMore){
            Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
            if (listener != null){
                listener.finish();
            }
            return;
        }
        App.getRetrofit().create(ApiService.class)
                .listComment(page, "server", this.id)
                .enqueue(new ApiCallback<ApiResponse<ApiResponsePage<Comment[]>>>(false) {
                    @Override
                    public void onSuccess(ApiResponse<ApiResponsePage<Comment[]>> response) {
                        super.onSuccess(response);
                        if (response.getData() != null){
                            ApiResponsePage<Comment[]> pageModel = response.getData();
                            if (pageModel.data != null){
                                for (int i = 0; i < response.getData().data.length; i++){
                                    commentList.add(response.getData().data[i]);
                                }
                                adapter.notifyDataSetChanged();
                                ServerCommentActivity.this.page = pageModel.current_page;
                                if (pageModel.last_page <= page){
                                    ServerCommentActivity.this.hasMore = false;
                                }
                                if (listener != null){
                                    listener.finish();
                                }
                            }
                        }else{
                            Toast.makeText(ServerCommentActivity.this, "暂无评分", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        Toast.makeText(ServerCommentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        if (listener != null){
                            listener.finish();
                        }
                        finish();
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
