package com.relyon.whib.modelo;

public class Argument extends Sending {

    private String text;
    private String audioPath;
    private String groupUID;
    private Long time;

    public Argument() {
    }

    public Argument(String type, Long date, String authorsName, String authorsUID, Subject subject, String text,
                    String audioPath, String groupUID, Long time, Sending sending) {
        super(type, date, authorsName, authorsUID, subject);
        this.text = text;
        this.audioPath = audioPath;
        this.groupUID = groupUID;
        this.text = text;
        this.audioPath = audioPath;
        this.groupUID = groupUID;
        this.time = time;
    }

    public Argument(String text, String audioPath, String groupUID, Long time, Sending sending) {
        this.text = text;
        this.audioPath = audioPath;
        this.groupUID = groupUID;
        this.time = time;
        super.setAuthorsName(sending.getAuthorsName());
        super.setAuthorsUID(sending.getAuthorsUID());
        super.setDate(sending.getDate());
        super.setSubject(sending.getSubject());
        super.setType(sending.getType());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getGroupUID() {
        return groupUID;
    }

    public void setGroupUID(String groupUID) {
        this.groupUID = groupUID;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
