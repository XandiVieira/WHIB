package com.relyon.whib.modelo;

import java.util.Date;

public class Item {

    private int typeID;
    private String itemUID;
    private String title;
    private Date validity;
    private Date purchaseDate;
    private int benefitID;

    public Item(int typeID, String itemUID, String title, Date validity, Date purchaseDate, int benefitID) {
        this.typeID = typeID;
        this.itemUID = itemUID;
        this.title = title;
        this.validity = validity;
        this.purchaseDate = purchaseDate;
        this.benefitID = benefitID;
    }

    public Item() {
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getItemUID() {
        return itemUID;
    }

    public void setItemUID(String itemUID) {
        this.itemUID = itemUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getBenefitID() {
        return benefitID;
    }

    public void setBenefitID(int benefitID) {
        this.benefitID = benefitID;
    }
}
