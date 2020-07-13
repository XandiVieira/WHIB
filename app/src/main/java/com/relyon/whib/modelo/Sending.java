package com.relyon.whib.modelo;

public class Sending {

    private String type;
    private Long date;
    private String authorsName;
    private String authorsUID;
    private String subject;

    public Sending(String type, Long date, String authorsName, String authorsUID,
                   String subject) {
        this.type = type;
        this.date = date;
        this.authorsName = authorsName;
        this.authorsUID = authorsUID;
        this.subject = subject;
    }

    public Sending() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public String getAuthorsUID() {
        return authorsUID;
    }

    public void setAuthorsUID(String authorsUID) {
        this.authorsUID = authorsUID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
