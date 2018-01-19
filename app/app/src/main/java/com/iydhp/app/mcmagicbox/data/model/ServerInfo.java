package com.iydhp.app.mcmagicbox.data.model;

public class ServerInfo {

    private String id;
    private String category;
    private String name;
    private String manager;
    private String version;
    private String icon;
    private String background;
    private String oneworddesc;
    private String desc;
    private String ip;
    private String port;
    private Long nowPlayer;
    private Long maxPlayer;
    private Boolean online;
    private String[] image;
    private Double score;
    private Long scorernum;

    public String getId() {
        return id;
    }
    public String getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public String getManager() {
        return manager;
    }
    public String getVersion() {
        return version;
    }
    public String getIcon() {
        return icon;
    }
    public String getBackground() {
        return background;
    }
    public String getOneworddesc() {
        return oneworddesc;
    }
    public String getDesc() {
        return desc;
    }
    public String getIp() {
        return ip;
    }
    public String getPort() {
        return port;
    }
    public Long getNowPlayer() {
        return nowPlayer;
    }
    public Long getMaxPlayer() {
        return maxPlayer;
    }
    public Boolean getOnline() {
        return online;
    }
    public String[] getImage() {
        return image;
    }
    public Double getScore() {
        return score;
    }
    public Long getScorernum() {
        return scorernum;
    }

    public ServerInfo setId(String id) {
        this.id = id;
        return this;
    }
    public ServerInfo setCategory(String category) {
        this.category = category;
        return this;
    }
    public ServerInfo setName(String name) {
        this.name = name;
        return this;
    }
    public ServerInfo setManager(String manager) {
        this.manager = manager;
        return this;
    }
    public ServerInfo setVersion(String version) {
        this.version = version;
        return this;
    }
    public ServerInfo setIcon(String icon) {
        this.icon = icon;
        return this;
    }
    public ServerInfo setBackground(String background) {
        this.background = background;
        return this;
    }
    public ServerInfo setOneworddesc(String oneworddesc) {
        this.oneworddesc = oneworddesc;
        return this;
    }
    public ServerInfo setDesc(String desc) {
        this.desc = desc;
        return this;
    }
    public ServerInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }
    public ServerInfo setPort(String port) {
        this.port = port;
        return this;
    }
    public ServerInfo setNowPlayer(Long nowPlayer) {
        this.nowPlayer = nowPlayer;
        return this;
    }
    public ServerInfo setMaxPlayer(Long maxPlayer) {
        this.maxPlayer = maxPlayer;
        return this;
    }
    public ServerInfo setOnline(Boolean online) {
        this.online = online;
        return this;
    }
    public ServerInfo setImage(String[] image) {
        this.image = image;
        return this;
    }
    public ServerInfo setScore(Double score) {
        this.score = score;
        return this;
    }
    public ServerInfo setScorernum(Long scorernum) {
        this.scorernum = scorernum;
        return this;
    }
}
