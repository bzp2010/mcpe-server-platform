package com.iydhp.app.mcmagicbox.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.adapter.ServerListAdapter;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.data.model.ServerListItem;
import com.iydhp.app.mcmagicbox.data.model.ServerListPagerItem;
import com.iydhp.app.mcmagicbox.databinding.FragmentServerListBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.activity.ServerDetailActivity;
import com.socks.library.KLog;

import java.util.ArrayList;

public class ServerListFragment extends BaseFragment {

    private FragmentServerListBinding binding;
    private ArrayList<ServerCardItem> serverCardItems;
    private ServerListAdapter serverListAdapter;

    public static ServerListFragment newInstance(Long id) {

        Bundle args = new Bundle();
        args.putLong("categoryId", id);

        ServerListFragment fragment = new ServerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_server_list, null, false);
        serverCardItems = new ArrayList<>();
        serverListAdapter = new ServerListAdapter(getActivity(), serverCardItems);
        serverListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        serverListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ServerDetailActivity.class);
                intent.putExtra("id", serverCardItems.get(position).getId());
                getActivity().startActivity(intent);
            }
        });
        binding.fragmentServerListViewpagerRvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.fragmentServerListViewpagerRvList.setAdapter(serverListAdapter);
        binding.fragmentServerListViewpagerSrlRefresh.setColorSchemeResources(R.color.colorAccent);
        binding.fragmentServerListViewpagerSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadData();
    }

    private void loadData(){
        binding.fragmentServerListViewpagerSrlRefresh.setRefreshing(true);
        if (serverCardItems != null) serverCardItems.clear();
        App.getRetrofit()
                .create(ApiService.class)
                .getServerList(getArguments().getLong("categoryId", 0l))
                .enqueue(new ApiCallback<ApiResponse<ServerListItem[]>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<ServerListItem[]> response) {
                        for (ServerListItem item : response.getData()){
                            if (item != null){
                                serverCardItems.add(new ServerCardItem(item));
                            }
                        }
                        serverListAdapter.notifyDataSetChanged();
                        binding.fragmentServerListViewpagerSrlRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                    }
                });
    }
}
