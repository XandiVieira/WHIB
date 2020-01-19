package com.relyon.whib.modelo;

public class Report {

    private String userSenderUID;
    private String userReceiverUID;
    private String reason;
    private String explanation;

    public Report(String userSenderUID, String userReceiverUID, String reason, String explanation) {
        this.userSenderUID = userSenderUID;
        this.userReceiverUID = userReceiverUID;
        this.reason = reason;
        this.explanation = explanation;
    }

    public Report() {
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
}
