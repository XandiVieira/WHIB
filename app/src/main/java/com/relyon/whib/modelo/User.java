package com.relyon.whib.modelo;

import java.util.List;

public class User {

    private String userUID;
    private String userName;
    private String photoPath;
    private UserTempInfo tempInfo;
    private Valuation valuation;
    private List<Report> reportList;
    private List<User> following;
    private boolean blocked;
    private boolean firstTime;
    private boolean extra;
    private List<String> groupsUIDList;
    private float rating;
    private History history;
    private int followers;
    private List<Doubts> pendingDoubts;
    private List<Item> itemsListUID;
    private boolean changedName;
    private boolean changedPhoto;
    private double totalInPurchase;
    private int purchases;
    private Preferences preferences;
    private String nickName;
    private boolean isAdmin;

    public User() {
    }

    public User(String userUID, String userName, String photoPath, UserTempInfo tempInfo, Valuation valuation, List<Report> reportList, List<User> following, boolean blocked, boolean firstTime, boolean extra, List<String> groupsUIDList, float rating, History history, int followers, List<Doubts> pendingDoubts, List<Item> itemsListUID, boolean changedName, boolean changedPhoto, double totalInPurchase, int purchases, Preferences preferences, String nickName, boolean isAdmin) {
        this.userUID = userUID;
        this.userName = userName;
        this.photoPath = photoPath;
        this.tempInfo = tempInfo;
        this.valuation = valuation;
        this.reportList = reportList;
        this.following = following;
        this.blocked = blocked;
        this.firstTime = firstTime;
        this.extra = extra;
        this.groupsUIDList = groupsUIDList;
        this.rating = rating;
        this.history = history;
        this.followers = followers;
        this.pendingDoubts = pendingDoubts;
        this.itemsListUID = itemsListUID;
        this.changedName = changedName;
        this.changedPhoto = changedPhoto;
        this.totalInPurchase = totalInPurchase;
        this.purchases = purchases;
        this.preferences = preferences;
        this.nickName = nickName;
        this.isAdmin = isAdmin;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
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

    public UserTempInfo getTempInfo() {
        return tempInfo;
    }

    public void setTempInfo(UserTempInfo tempInfo) {
        this.tempInfo = tempInfo;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public List<Doubts> getPendingDoubts() {
        return pendingDoubts;
    }

    public void setPendingDoubts(List<Doubts> pendingDoubts) {
        this.pendingDoubts = pendingDoubts;
    }

    public List<Item> getItemsListUID() {
        return itemsListUID;
    }

    public void setItemsListUID(List<Item> itemsListUID) {
        this.itemsListUID = itemsListUID;
    }

    public boolean isChangedName() {
        return changedName;
    }

    public void setChangedName(boolean changedName) {
        this.changedName = changedName;
    }

    public boolean isChangedPhoto() {
        return changedPhoto;
    }

    public void setChangedPhoto(boolean changedPhoto) {
        this.changedPhoto = changedPhoto;
    }

    public double getTotalInPurchase() {
        return totalInPurchase;
    }

    public void setTotalInPurchase(double totalInPurchase) {
        this.totalInPurchase = totalInPurchase;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
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
}