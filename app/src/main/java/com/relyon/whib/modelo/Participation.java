package com.relyon.whib.modelo;

import java.util.List;

public class Participation {

    private List<Argument> argumentList;
    private Valuation valuation;
    private int supporters;
    private String authorUID;

    public Participation(List<Argument> argumentList, Valuation valuation, int supporters, String authorUID) {
        this.argumentList = argumentList;
        this.valuation = valuation;
        this.supporters = supporters;
        this.authorUID = authorUID;
    }

    public Participation() {
    }

    public List<Argument> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<Argument> argumentList) {
        this.argumentList = argumentList;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    public int getSupporters() {
        return supporters;
    }

    public void setSupporters(int supporters) {
        this.supporters = supporters;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }
}
