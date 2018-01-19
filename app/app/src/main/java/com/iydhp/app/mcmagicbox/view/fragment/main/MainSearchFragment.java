package com.iydhp.app.mcmagicbox.view.fragment.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiResponsePage;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.adapter.ServerListAdapter;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.data.model.ServerListItem;
import com.iydhp.app.mcmagicbox.databinding.FragmentMainSearchBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.activity.ServerDetailActivity;
import com.iydhp.app.mcmagicbox.view.activity.UserFavouriteActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import java.util.ArrayList;

public class MainSearchFragment extends BaseFragment {

    private FragmentMainSearchBinding binding;
    private MaterialSearchView searchView;

    private ArrayList<ServerCardItem> serverCardItems;
    private ServerListAdapter serverListAdapter;
    private String lastSearchContent = "";
    private Long searchPage = 1l;
    private boolean isInited = false;

    public static MainSearchFragment newInstance() {

        Bundle args = new Bundle();

        MainSearchFragment fragment = new MainSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_search, container, false);

        serverCardItems = new ArrayList<>();
        serverListAdapter = new ServerListAdapter(getActivity(), serverCardItems);
        serverListAdapter.setEnableLoadMore(true);
        serverListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        serverListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ServerDetailActivity.class);
                intent.putExtra("id", serverCardItems.get(position).getId());
                getActivity().startActivity(intent);
            }
        });
        binding.fragmentMainSearchRvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.fragmentMainSearchRvList.setAdapter(serverListAdapter);
        binding.fragmentMainSearchSrlRefresh.setColorSchemeResources(R.color.colorAccent);
        /*binding.fragmentMainSearchSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(lastSearchContent,searchPage + 1);
            }
        });*/
        //binding.fragmentMainSearchSrlRefresh.setRefreshHeader(new MaterialHeader(this));
        //binding.fragmentMainSearchSrlRefresh.setRefreshFooter(new ClassicsFooter(this));

        return binding.getRoot();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (isInited && !binding.fragmentMainSearchMsvSearch.isSearchOpen()){
            searchView.showSearch();
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.fragmentMainSearchTbHeader);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("服务器搜索");
        searchView = binding.fragmentMainSearchMsvSearch;
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                serverCardItems.clear();
                loadData(query, 1l);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setHint("服务器搜索");
        searchView.showSearch();
        isInited = true;
    }

    private void loadData(String query, Long page){
        lastSearchContent = query;
        searchPage = page;
        App.getRetrofit()
                .create(ApiService.class)
                .searchServer(query, page)
                .enqueue(new ApiCallback<ApiResponse<ApiResponsePage<ServerListItem[]>>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<ApiResponsePage<ServerListItem[]>> response) {
                        for (ServerListItem item : response.getData().data){
                            if (item != null){
                                serverCardItems.add(new ServerCardItem(item));
                            }
                        }
                        serverListAdapter.notifyDataSetChanged();
                        //binding.fragmentMainSearchSrlRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_search, menu);

        MenuItem item = menu.findItem(R.id.menu_main_search_search);
        binding.fragmentMainSearchMsvSearch.setMenuItem(item);
    }
}
