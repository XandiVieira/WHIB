package com.relyon.whib.modelo;

import java.util.Date;

public class Doubts {

    private String question;
    private Date dateQuestion;
    private String answer;
    private Date dateAnswer;

    public Doubts(String question, Date dateQuestion, String answer, Date dateAnswer) {
        this.question = question;
        this.dateQuestion = dateQuestion;
        this.answer = answer;
        this.dateAnswer = dateAnswer;
    }

    public Doubts() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Date getDateQuestion() {
        return dateQuestion;
    }

    public void setDateQuestion(Date dateQuestion) {
        this.dateQuestion = dateQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getDateAnswer() {
        return dateAnswer;
    }

    public void setDateAnswer(Date dateAnswer) {
        this.dateAnswer = dateAnswer;
    }
}
