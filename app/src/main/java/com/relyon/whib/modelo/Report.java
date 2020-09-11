package com.relyon.whib.modelo;

import java.util.Comparator;

public class Report {

    private String id;
    private String userSenderUID;
    private String userReceiverUID;
    private String reason;
    private String explanation;
    private String text;
    private boolean fair;
    private boolean reviewed;
    private String commentUID;

    public Report() {
    }

    public Report(String userSenderUID, String userReceiverUID, String reason, String explanation, String text, boolean fair, String commentUID) {
        this.userSenderUID = userSenderUID;
        this.userReceiverUID = userReceiverUID;
        this.reason = reason;
        this.explanation = explanation;
        this.text = text;
        this.fair = fair;
        this.reviewed = false;
        this.commentUID = commentUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSenderUID() {
        return userSenderUID;
    }

    public void setUserSenderUID(String userSenderUID) {
        this.userSenderUID = userSenderUID;
    }

    public String getUserReceiverUID() {
        return userReceiverUID;
    }

    public void setUserReceiverUID(String userReceiverUID) {
        this.userReceiverUID = userReceiverUID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFair() {
        return fair;
    }

    public void setFair(boolean fair) {
        this.fair = fair;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public String getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
    }

    public static Comparator<Complaint> dateComparator = (c1, c2) -> (int) (c2.getDateQuestion() - c1.getDateQuestion());
}