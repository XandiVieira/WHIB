package com.relyon.whib.modelo;

import java.util.List;

public class Question extends Argument {

    private List<Answer> answerList;

    public Question(String type, String date, String authorsName, String authorsUID, Subject subject, String text, String audioPath, String groupUID, String time, Sending sending, List<Answer> answerList) {
        super(type, date, authorsName, authorsUID, subject, text, audioPath, groupUID, time, sending);
        this.answerList = answerList;
    }

    public Question(String text, String audioPath, String groupUID, Sending sending, List<Answer> answerList, String time) {
        super(text, audioPath, groupUID, time, sending);
        this.answerList = answerList;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
}
