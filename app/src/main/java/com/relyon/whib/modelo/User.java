package com.relyon.whib.modelo;

import java.util.HashMap;
import java.util.List;

public class User {

    private String userUID;
    private String token;
    private String userName;
    private String photoPath;
    private Valuation valuation;
    private List<User> following;
    private boolean firstTime;
    private boolean extra;
    private List<String> groupsUIDList;
    private List<Comment> commentList;
    private int followers;
    private List<Complaint> pendingDoubts;
    private List<Product> itemsListUID;
    private boolean changedName;
    private Preferences preferences;
    private String nickName;
    private boolean isAdmin;
    private HashMap<String, Product> products;
    private Punishment punishment;

    public User() {
    }

    public User(String userUID, String token, String userName, String photoPath, Valuation valuation, List<User> following, boolean firstTime, boolean extra, List<String> groupsUIDList, int followers, List<Complaint> pendingDoubts, List<Product> itemsListUID, boolean changedName, Preferences preferences, String nickName, boolean isAdmin, Punishment punishment) {
        this.userUID = userUID;
        this.token = token;
        this.userName = userName;
        this.photoPath = photoPath;
        this.valuation = valuation;
        this.following = following;
        this.firstTime = firstTime;
        this.extra = extra;
        this.groupsUIDList = groupsUIDList;
        this.followers = followers;
        this.pendingDoubts = pendingDoubts;
        this.itemsListUID = itemsListUID;
        this.changedName = changedName;
        this.preferences = preferences;
        this.nickName = nickName;
        this.isAdmin = isAdmin;
        this.punishment = punishment;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean isExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
    }

    public List<String> getGroupsUIDList() {
        return groupsUIDList;
    }

    public void setGroupsUIDList(List<String> groupsUIDList) {
        this.groupsUIDList = groupsUIDList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public List<Complaint> getPendingDoubts() {
        return pendingDoubts;
    }

    public void setPendingDoubts(List<Complaint> pendingDoubts) {
        this.pendingDoubts = pendingDoubts;
    }

    public List<Product> getItemsListUID() {
        return itemsListUID;
    }

    public void setItemsListUID(List<Product> itemsListUID) {
        this.itemsListUID = itemsListUID;
    }

    public boolean isChangedName() {
        return changedName;
    }

    public void setChangedName(boolean changedName) {
        this.changedName = changedName;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public HashMap<String, Product> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Product> products) {
        this.products = products;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public void setPunishment(Punishment punishment) {
        this.punishment = punishment;
    }
}