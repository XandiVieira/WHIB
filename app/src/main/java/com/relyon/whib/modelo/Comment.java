package com.relyon.whib.modelo;

import java.util.ArrayList;
import java.util.List;

public class Comment extends Sending {

    private String text;
    private float rating;
    private String userPhotoURL;
    private String time;
    private int numberOfRatings;
    private float sumOfRatings;
    private String commentUID;
    private List<String> alreadyRatedList = new ArrayList<>();
    private boolean isAGroup;
    private Group group;

    public Comment() {
    }

    public Comment(String type, String date, String authorsName, String authorsUID, Subject subject, String text,
                   float rating, String userPhotoURL, String time, int numberOfRatings, float sumOfRatings,
                   Sending sending, boolean isAGroup, Group group) {
        super(type, date, authorsName, authorsUID, subject);
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

    public Comment(String text, float rating, String userPhotoURL, String time, int numberOfRatings, float sumOfRatings,
                   Sending sending, boolean isAGroup, Group group) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public String getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
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
}
