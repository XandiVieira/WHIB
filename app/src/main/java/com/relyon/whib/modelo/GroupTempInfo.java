package com.relyon.whib.modelo;

import java.util.List;

public class GroupTempInfo {

    private List<User> usersLine;
    private boolean full;

    public GroupTempInfo() {
    }

    public GroupTempInfo(List<User> usersLine, boolean full) {
        this.usersLine = usersLine;
        this.full = full;
    }

    public List<User> getUsersLine() {
        return usersLine;
    }

    public void setUsersLine(List<User> usersLine) {
        this.usersLine = usersLine;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}