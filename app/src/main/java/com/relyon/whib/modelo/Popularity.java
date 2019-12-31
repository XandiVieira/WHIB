package com.relyon.whib.modelo;

public class Popularity {

    private int numUsers;
    private int numComments;
    private int numServers;

    public Popularity(int numUsers, int numComments, int numServers) {
        this.numUsers = numUsers;
        this.numComments = numComments;
        this.numServers = numServers;
    }

    public Popularity() {
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public int getNumServers() {
        return numServers;
    }

    public void setNumServers(int numServers) {
        this.numServers = numServers;
    }
}


