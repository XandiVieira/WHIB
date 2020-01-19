package com.relyon.whib.modelo;

public class Valuation {

    private double goodPercentage;
    private double badPercentage;
    private double mediumPercentage;
    private int goodQtd;
    private int badQtd;
    private int regularQtd;
    private int totalOfValuations;

    public Valuation(double goodPercentage, double badPercentage, double regularPercentage, int goodQtd, int badQtd, int regularQtd) {
        this.goodPercentage = goodPercentage;
        this.badPercentage = badPercentage;
        this.mediumPercentage = regularPercentage;
        this.goodQtd = goodQtd;
        this.badQtd = badQtd;
        this.regularQtd = regularQtd;
        this.totalOfValuations = this.goodQtd + this.badQtd + this.regularQtd;
    }

    public Valuation() {
    }

    public int getTotalOfValuations() {
        return totalOfValuations;
    }

    public void setTotalOfValuations(int totalOfValuations) {
        this.totalOfValuations = totalOfValuations;
    }

    public double getGoodPercentage() {
        return goodPercentage;
    }

    public void setGoodPercentage(double goodPercentage) {
        this.goodPercentage = goodPercentage;
    }

    public double getBadPercentage() {
        return badPercentage;
    }

    public void setBadPercentage(double badPercentage) {
        this.badPercentage = badPercentage;
    }

    public double getMediumPercentage() {
        return mediumPercentage;
    }

    public void setMediumPercentage(double mediumPercentage) {
        this.mediumPercentage = mediumPercentage;
    }

    public int getGoodQtd() {
        return goodQtd;
    }

    public void setGoodQtd(int goodQtd) {
        this.goodQtd = goodQtd;
    }

    public int getBadQtd() {
        return badQtd;
    }

    public void setBadQtd(int badQtd) {
        this.badQtd = badQtd;
    }

    public int getRegularQtd() {
        return regularQtd;
    }

    public void setRegularQtd(int regularQtd) {
        this.regularQtd = regularQtd;
    }
}
