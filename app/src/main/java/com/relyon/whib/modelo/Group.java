package com.relyon.whib.modelo;

import java.util.List;

public class Group {

    private String groupUID;
    private String subjectTitle;
    private int number;
    private int serverNumber;
    private GroupTempInfo tempInfo;
    private String mode;
    private List<Question> questionList;
    private List<String> userListUID;
    private boolean ready;
    private String commentUID;

    public Group(String groupUID, String subjectTitle, int number, int serverNumber, GroupTempInfo tempInfo,
                 String mode, List<Question> questionList, List<String> userListUID, boolean ready, String commentUID) {
        this.groupUID = groupUID;
        this.subjectTitle = subjectTitle;
        this.number = number;
        this.serverNumber = serverNumber;
        this.tempInfo = tempInfo;
        this.mode = mode;
        this.questionList = questionList;
        this.userListUID = userListUID;
        this.ready = ready;
        this.commentUID = commentUID;
    }

    public Group() {
    }

    public String getGroupUID() {
        return groupUID;
    }

    public void setGroupUID(String groupUID) {
        this.groupUID = groupUID;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }

    public GroupTempInfo getTempInfo() {
        return tempInfo;
    }

    public void setTempInfo(GroupTempInfo tempInfo) {
        this.tempInfo = tempInfo;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public List<String> getUserListUID() {
        return userListUID;
    }

    public void setUserListUID(List<String> userListUID) {
        this.userListUID = userListUID;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
    }
}
