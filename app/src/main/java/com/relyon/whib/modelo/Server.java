package com.relyon.whib.modelo;

import java.util.Date;
import java.util.UUID;

public class Server {

    private String serverUID;
    private String type; //main or secondary
    private ServerTempInfo tempInfo; //server temporary data
    private Subject subject;
    private Timeline timeline;

    public Server(String serverUID, String type, ServerTempInfo tempInfo, Subject subject, Timeline timeline) {
        this.serverUID = serverUID;
        this.type = type;
        this.tempInfo = tempInfo;
        this.subject = subject;
        this.timeline = timeline;
    }

    public Server(String serverUID, String type, String subject, int numServer) {
        this.serverUID = serverUID;
        this.type = type;
        this.tempInfo = new ServerTempInfo(0, true, numServer);
        this.subject = new Subject(UUID.randomUUID().toString(), subject, Util.getCurrentDate(), new Popularity(0, 1, 0), true);
        this.timeline = new Timeline(this.subject);
    }

    public Server() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ServerTempInfo getTempInfo() {
        return tempInfo;
    }

    public void setTempInfo(ServerTempInfo tempInfo) {
        this.tempInfo = tempInfo;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getServerUID() {
        return serverUID;
    }

    public void setServerUID(String serverUID) {
        this.serverUID = serverUID;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}

