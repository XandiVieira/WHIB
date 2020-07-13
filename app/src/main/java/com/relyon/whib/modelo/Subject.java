package com.relyon.whib.modelo;

import android.os.Parcel;

import java.util.HashMap;

public class Subject {

    private String title;
    private HashMap<String, Server> servers;
    private String date;
    private boolean on; //true - on / false - off

    public Subject(String title, HashMap<String, Server> servers, String date, boolean on) {
        this.title = title;
        this.servers = servers;
        this.date = date;
        this.on = on;
    }

    public Subject() {
    }

    public Subject(String title) {
        this.title = title;
    }

    protected Subject(Parcel in) {
        title = in.readString();
        date = in.readString();
        on = in.readByte() != 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public HashMap<String, Server> getServers() {
        return servers;
    }

    public void setServers(HashMap<String, Server> servers) {
        this.servers = servers;
    }
}
