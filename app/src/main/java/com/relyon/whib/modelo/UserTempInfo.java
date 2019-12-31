package com.relyon.whib.modelo;

public class UserTempInfo {

    private Server currentServer;
    private Group currentGroup;
    private boolean isOn;
    private boolean isParticipant;

    public UserTempInfo(Server currentServer, Group currentGroup, boolean isOn, boolean isParticipant) {
        this.currentServer = currentServer;
        this.currentGroup = currentGroup;
        this.isOn = isOn;
        this.isParticipant = isParticipant;
    }

    public UserTempInfo() {
    }

    public Server getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(Server currentServer) {
        this.currentServer = currentServer;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isParticipant() {
        return isParticipant;
    }

    public void setParticipant(boolean participant) {
        isParticipant = participant;
    }
}
