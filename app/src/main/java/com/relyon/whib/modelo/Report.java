package com.relyon.whib.modelo;

public class Report {

    private String id;
    private String userSenderUID;
    private String userReceiverUID;
    private String reason;
    private String explanation;
    private String text;
    private boolean fair;
    private boolean reviewed;

    public Report(String userSenderUID, String userReceiverUID, String reason, String explanation, String text) {
        this.userSenderUID = userSenderUID;
        this.userReceiverUID = userReceiverUID;
        this.reason = reason;
        this.explanation = explanation;
        this.text = text;
        this.fair = false;
        this.reviewed = false;
    }

    public Report() {
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
}