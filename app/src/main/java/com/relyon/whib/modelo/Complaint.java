package com.relyon.whib.modelo;

import java.util.Comparator;

public class Complaint {

    private String complaintId;
    private String userSenderUID;
    private String question;
    private Long dateQuestion;
    private String answer;
    private Long dateAnswer;
    private boolean answered;

    public Complaint(String complaintId, String userSenderUID, String question, Long dateQuestion) {
        this.complaintId = complaintId;
        this.userSenderUID = userSenderUID;
        this.question = question;
        this.dateQuestion = dateQuestion;
        this.answered = false;
    }

    public Complaint() {
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getUserSenderUID() {
        return userSenderUID;
    }

    public void setUserSenderUID(String userSenderUID) {
        this.userSenderUID = userSenderUID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getDateQuestion() {
        return dateQuestion;
    }

    public void setDateQuestion(Long dateQuestion) {
        this.dateQuestion = dateQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getDateAnswer() {
        return dateAnswer;
    }

    public void setDateAnswer(Long dateAnswer) {
        this.dateAnswer = dateAnswer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public static Comparator<Complaint> dateComparator = (c1, c2) -> (int) (c2.getDateQuestion() - c1.getDateQuestion());
}