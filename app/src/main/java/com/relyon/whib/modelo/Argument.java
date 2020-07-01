package com.relyon.whib.modelo;

public class Argument extends Sending {

    private String text;
    private String imagePath;
    private String groupUID;
    private Long time;

    public Argument() {
    }

    public Argument(String type, Long date, String authorsName, String authorsUID, Subject subject, String text,
                    String imagePath, String groupUID, Long time, Sending sending) {
        super(type, date, authorsName, authorsUID, subject);
        this.text = text;
        this.imagePath = imagePath;
        this.groupUID = groupUID;
        this.text = text;
        this.imagePath = imagePath;
        this.groupUID = groupUID;
        this.time = time;
    }

    public Argument(String text, String imagePath, String groupUID, Long time, Sending sending) {
        this.text = text;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
