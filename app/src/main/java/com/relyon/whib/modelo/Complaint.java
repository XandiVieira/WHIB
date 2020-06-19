package com.relyon.whib.modelo;

public class Complaint {

    private String complaintId;
    private String senderUID;
    private String question;
    private Long dateQuestion;
    private String answer;
    private Long dateAnswer;
    private boolean answered;

    public Complaint(String complaintId, String senderUID, String question, Long dateQuestion) {
        this.complaintId = complaintId;
        this.senderUID = senderUID;
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

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
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
}
