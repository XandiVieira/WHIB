package com.relyon.whib.modelo;

public class Answer extends Argument {

    public Answer(String type, Long date, String authorsName, String authorsUID, String subject, String text, String audioPath, String groupUID, Long time, Sending sending) {
        super(type, date, authorsName, authorsUID, subject, text, audioPath, groupUID, time, sending);
    }

    public Answer(String text, String audioPath, String groupUID, Long time, Sending sending) {
        super(text, audioPath, groupUID, time, sending);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
