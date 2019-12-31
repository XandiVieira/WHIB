package com.relyon.whib.modelo;

import java.util.HashMap;
import java.util.List;

public class Timeline {

    private HashMap<String, Comment> commentMap;
    private Subject subject;
    private List<Group> groupList;

    public Timeline(HashMap<String, Comment> commentMap, Subject subject, List<Group> groupList) {
        this.commentMap = commentMap;
        this.subject = subject;
        this.groupList = groupList;
    }

    public Timeline(Subject subject) {
        this.subject = subject;
    }

    public Timeline() {
    }

    public HashMap<String, Comment> getCommentMap() {
        return commentMap;
    }

    public void setCommentMap(HashMap<String, Comment> commentMap) {
        this.commentMap = commentMap;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
}
