package com.relyon.whib.modelo;

public class ServerTempInfo {

    private int qtdUsers;
    private int number;
    private boolean activated; //true - available / false - full

    public ServerTempInfo(int qtdUsers, boolean activated, int number) {
        this.qtdUsers = qtdUsers;
        this.number = number;

        this.activated = number <= 99;
    }

    public ServerTempInfo() {
    }

    public int getQtdUsers() {
        return qtdUsers;
    }

    public void setQtdUsers(int qtdUsers) {
        this.qtdUsers = qtdUsers;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
