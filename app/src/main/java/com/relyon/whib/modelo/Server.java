package com.relyon.whib.modelo;

public class Server {

    private String serverUID;
    private ServerTempInfo tempInfo; //server temporary data
    private String subject;
    private Timeline timeline;

    public Server(String serverUID, ServerTempInfo tempInfo, String subject, Timeline timeline) {
        this.serverUID = serverUID;
        this.tempInfo = tempInfo;
        this.subject = subject;
        this.timeline = timeline;
    }

    public Server(String serverUID, String subject, int numServer) {
        this.serverUID = serverUID;
        this.tempInfo = new ServerTempInfo(0, true, numServer);
        this.subject = subject;
        this.timeline = new Timeline(this.subject);
    }

    public Server() {
    }

    public ServerTempInfo getTempInfo() {
        return tempInfo;
    }

    public void setTempInfo(ServerTempInfo tempInfo) {
        this.tempInfo = tempInfo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
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