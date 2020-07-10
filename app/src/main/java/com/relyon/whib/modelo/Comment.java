package com.relyon.whib.modelo;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comment extends Sending {

    private String commentUID;
    private String serverUID;
    private String text;
    private float rating;
    private String userPhotoURL;
    private Long time;
    private int numberOfRatings;
    private float sumOfRatings;
    private List<String> alreadyRatedList = new ArrayList<>();
    private boolean isAGroup;
    private Group group;
    private HashMap<String, Product> stickers;

    public Comment() {
    }

    public Comment(String serverUID, String type, Long date, String authorsName, String authorsUID, Subject subject, String text,
                   float rating, String userPhotoURL, Long time, int numberOfRatings, float sumOfRatings,
                   Sending sending, boolean isAGroup, Group group) {
        super(type, date, authorsName, authorsUID, subject);
        this.serverUID = serverUID;
        this.text = text;
        this.rating = rating;
        this.userPhotoURL = userPhotoURL;
        this.time = time;
        this.numberOfRatings = numberOfRatings;
        this.sumOfRatings = sumOfRatings;
        this.isAGroup = isAGroup;
        this.group = group;
        super.setAuthorsName(sending.getAuthorsName());
        super.setAuthorsUID(sending.getAuthorsUID());
        super.setDate(sending.getDate());
        super.setSubject(sending.getSubject());
        super.setType(sending.getType());
    }

    public Comment(String serverUID, String text, float rating, String userPhotoURL, Long time, int numberOfRatings, float sumOfRatings,
                   Sending sending, boolean isAGroup, Group group) {
        this.serverUID = serverUID;
        this.text = text;
        this.rating = rating;
        this.userPhotoURL = userPhotoURL;
        this.time = time;
        this.numberOfRatings = numberOfRatings;
        this.sumOfRatings = sumOfRatings;
        this.isAGroup = isAGroup;
        this.group = group;
        super.setAuthorsName(sending.getAuthorsName());
        super.setAuthorsUID(sending.getAuthorsUID());
        super.setDate(sending.getDate());
        super.setSubject(sending.getSubject());
        super.setType(sending.getType());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public float getSumOfRatings() {
        return sumOfRatings;
    }

    public void setSumOfRatings(float sumOfRatings) {
        this.sumOfRatings = sumOfRatings;
    }

    public List<String> getAlreadyRatedList() {
        return alreadyRatedList;
    }

    public void setAlreadyRatedList(List<String> alreadyRatedList) {
        this.alreadyRatedList = alreadyRatedList;
    }

    public boolean isAGroup() {
        return isAGroup;
    }

    public void setAGroup(boolean AGroup) {
        this.isAGroup = AGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getServerUID() {
        return serverUID;
    }

    public void setServerUID(String serverUID) {
        this.serverUID = serverUID;
    }

    @Exclude
    public String getCommentUID() {
        return commentUID;
    }

    @Exclude
    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Comment comment = (Comment) obj;
        if (comment != null) {
            return commentUID.matches(comment.getCommentUID());
        } else {
            return false;
        }
    }

    public HashMap<String, Product> getStickers() {
        return stickers;
    }

    public void setStickers(HashMap<String, Product> stickers) {
        this.stickers = stickers;
    }
}