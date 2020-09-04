package com.relyon.whib.modelo;

import java.util.HashMap;
import java.util.List;

public class Timeline {

    private HashMap<String, Comment> commentList;
    private String subject;
    private List<Group> groupList;

    public Timeline() {
    }

    public Timeline(HashMap<String, Comment> commentList, String subject, List<Group> groupList) {
        this.commentList = commentList;
        this.subject = subject;
        this.groupList = groupList;
    }

    public Timeline(String subject) {
        this.subject = subject;
    }

    public HashMap<String, Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(HashMap<String, Comment> commentList) {
        this.commentList = commentList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
}