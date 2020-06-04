package com.relyon.whib.modelo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Survey {

    private List<Alternative> alternatives;
    private List<String> alreadyVoted = new ArrayList<>();
    private long endDate;
    private int numVotes = 0;

    public Survey() {
    }

    public Survey(List<Alternative> alternatives) {
        this.alternatives = alternatives;
        this.endDate = oneWeek();
    }

    private long oneWeek() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 7);
        return c.getTime().getTime();
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public List<String> getAlreadyVoted() {
        return alreadyVoted;
    }

    public void setAlreadyVoted(List<String> alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }
}
