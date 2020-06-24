package com.relyon.whib.modelo;

public class Product {

    private String itemUID;
    private String imagePath;
    private String title;
    private String description;
    private float price;
    private Long purchaseDate;

    public Product() {
    }

    public Product(String itemUID, String imagePath, String title, String description, float price, Long purchaseDate) {
        this.itemUID = itemUID;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.price = price;
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

    public void setPrice(float price) {
        this.price = price;
    }

    public Long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}