package com.iydhp.app.mcmagicbox.view.custom;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.model.ServerCardItem;
import com.iydhp.app.mcmagicbox.databinding.LayoutCardServerBinding;


public class ServerInfoCard extends CardView {

    private Context context;
    private LayoutCardServerBinding binding;
    private ServerCardItem data;
    private OnCardClickListener listener;

    public ServerInfoCard(Context context) {
        super(context);
    }

    public ServerInfoCard(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_card_server, this, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, binding.getRoot().getHeight() + 4);
    }

    public void setData(ServerCardItem data){
        this.data = data;
        binding.setDataBean(data);
    }

    public void setOnCardClickListener(@Nullable OnCardClickListener l) {
        this.listener = l;
        binding.layoutCardServerCvRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCardClick(view, data);
            }
        });
    }

    public interface OnCardClickListener{
        public void onCardClick(View view, ServerCardItem data);
    }
}
