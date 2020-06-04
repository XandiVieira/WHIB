package com.relyon.whib.modelo;

import java.util.ArrayList;
import java.util.List;

public class Alternative {

    private String id;
    private String text;
    private int numVotes = 0;
    private List<String> votedForMe = new ArrayList<>();

    public Alternative() {
    }

    public Alternative(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    public List<String> getVotedForMe() {
        return votedForMe;
    }

    public void setVotedForMe(List<String> votedForMe) {
        this.votedForMe = votedForMe;
    }
}