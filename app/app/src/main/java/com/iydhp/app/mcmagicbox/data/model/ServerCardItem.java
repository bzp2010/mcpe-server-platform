package com.iydhp.app.mcmagicbox.data.model;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ServerCardItem {

    private Long id; //db
    private String name; //db
    private String type;
    private Long onlinePlayerNum;
    private Long maxPlayerNum;
    private String version; //db
    private Double score; //db
    private String category; //db
    private String background; //db

    public ServerCardItem(){}

    public ServerCardItem(ServerListItem data){
        this.id = data.getId();
        this.name = data.getName();
        this.type = data.getType();
        this.onlinePlayerNum = data.getNowPlayer();
        this.maxPlayerNum = data.getMaxPlayer();
        this.version = data.getVersion();
        this.score = data.getScore();
        this.category = data.getCategory();
        this.background = data.getBackground();
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public Long getOnlinePlayerNum() {
        return onlinePlayerNum;
    }
    public Long getMaxPlayerNum() {
        return maxPlayerNum;
    }
    public String getVersion() {
        return version;
    }
    public Double getScore() {
        return score;
    }
    public String getCategory() {
        return category;
    }
    public String getBackground() {
        return background;
    }

    @BindingAdapter({"imageUrl"})
    public static void loadBackground(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                //.placeholder(R.drawable.placeholder)
                .into(view);
    }

    @BindingAdapter({"onlinePlayerNum", "maxPlayerNum"})
    public static void makePlyaerNum(TextView view, Long onlinePlayerNum, Long maxPlayerNum) {
        if (onlinePlayerNum == 0 && maxPlayerNum == 0){
            view.setText("离线");
        }else {
            view.setText("在线 " + onlinePlayerNum + "/" + maxPlayerNum);
        }
    }

    @BindingAdapter({"onlinePlayerNum", "maxPlayerNum"})
    public static void makePlyaerNumCard(CardView view, Long onlinePlayerNum, Long maxPlayerNum) {
        if (onlinePlayerNum == 0 && maxPlayerNum == 0){
            view.setCardBackgroundColor(Color.parseColor("#888888"));
        }else{
            view.setCardBackgroundColor(Color.parseColor("#78a355"));
        }
    }

    @BindingAdapter({"version"})
    public static void makeVersion(TextView view, String version) {
        view.setText("版本 " + version);
    }

    @BindingAdapter({"score"})
    public static void makeScore(TextView view, Double score) {
        view.setText("评分 " + score);
    }

}
