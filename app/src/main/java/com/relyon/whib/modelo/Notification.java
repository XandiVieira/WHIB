package com.relyon.whib.modelo;

public class Notification {

    private String serverId;
    private String userUID;
    private String text;
    private Long date;

    public Notification() {
    }

    public Notification(String userUID, String text, Long date) {
        this.userUID = userUID;
        this.text = text;
        this.date = date;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
