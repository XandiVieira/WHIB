package com.relyon.whib.modelo;

public class Valuation {

    private double goodPercentage;
    private double badPercentage;
    private double mediumPercentage;
    private int goodQtd;
    private int badQtd;
    private int regularQtd;
    private int totalOfValuations;
    private int numberOfRatings;
    private float sumOfRatings;

    public Valuation() {
    }

    public Valuation(double goodPercentage, double badPercentage, double regularPercentage, int goodQtd, int badQtd, int regularQtd) {
        this.goodPercentage = goodPercentage;
        this.badPercentage = badPercentage;
        this.mediumPercentage = regularPercentage;
        this.goodQtd = goodQtd;
        this.badQtd = badQtd;
        this.regularQtd = regularQtd;
        this.totalOfValuations = this.goodQtd + this.badQtd + this.regularQtd;
        this.numberOfRatings = 0;
        this.sumOfRatings = 0;
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

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public float getSumOfRatings() {
        return sumOfRatings;
    }

    public void setSumOfRatings(float sumOfRatings) {
        this.sumOfRatings = sumOfRatings;
    }
}