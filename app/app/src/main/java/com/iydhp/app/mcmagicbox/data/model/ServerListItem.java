package com.iydhp.app.mcmagicbox.data.model;

public class ServerListItem {

    private long id;
    private String category;
    private String name;
    private String type; //tan90
    private double score; //手动构造
    private String manager;
    private String version;
    private String background;
    private String ip;
    private String port;
    private long nowPlayer; //手动构造
    private long maxPlayer; //手动构造
    private boolean online; //手动构造

    public long getId() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public double getScore() {
        return score;
    }
    public String getManager() {
        return manager;
    }
    public String getVersion() {
        return version;
    }
    public String getBackground() {
        return background;
    }
    public String getIp() {
        return ip;
    }
    public String getPort() {
        return port;
    }
    public long getNowPlayer() {
        return nowPlayer;
    }
    public long getMaxPlayer() {
        return maxPlayer;
    }
    public boolean isOnline() {
        return online;
    }

    public ServerListItem setId(long id) {
        this.id = id;
        return this;
    }
    public ServerListItem setCategory(String category) {
        this.category = category;
        return this;
    }
    public ServerListItem setName(String name) {
        this.name = name;
        return this;
    }
    public ServerListItem setType(String type) {
        this.type = type;
        return this;
    }
    public ServerListItem setScore(double score) {
        this.score = score;
        return this;
    }
    public ServerListItem setManager(String manager) {
        this.manager = manager;
        return this;
    }
    public ServerListItem setVersion(String version) {
        this.version = version;
        return this;
    }
    public ServerListItem setBackground(String background) {
        this.background = background;
        return this;
    }
    public ServerListItem setIp(String ip) {
        this.ip = ip;
        return this;
    }
    public ServerListItem setPort(String port) {
        this.port = port;
        return this;
    }
    public ServerListItem setNowPlayer(long nowPlayer) {
        this.nowPlayer = nowPlayer;
        return this;
    }
    public ServerListItem setMaxPlayer(long maxPlayer) {
        this.maxPlayer = maxPlayer;
        return this;
    }
    public ServerListItem setOnline(boolean online) {
        this.online = online;
        return this;
    }

}
