package com.relyon.whib.modelo;

import java.util.Date;

public class Product {

    private String itemUID;
    private String imagePath;
    private String title;
    private String description;
    private double price;
    private Date validity;
    private Date purchaseDate;

    public Product(String itemUID, String imagePath, String title, String description, double price, Date validity, Date purchaseDate) {
        this.itemUID = itemUID;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.price = price;
        this.validity = validity;
        this.purchaseDate = purchaseDate;
    }

    public String getItemUID() {
        return itemUID;
    }

    public void setItemUID(String itemUID) {
        this.itemUID = itemUID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
}
