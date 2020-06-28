package com.relyon.whib.modelo;

public class Product {

    private String itemSKU;
    private String imagePath;
    private String title;
    private String description;
    private float price;
    private Long purchaseDate;
    private int quantity = 0;

    public Product() {
    }

    public Product(String itemSKU, String imagePath, String title, String description, float price, Long purchaseDate) {
        this.itemSKU = itemSKU;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    public String getItemSKU() {
        return itemSKU;
    }

    public void setItemSKU(String itemSKU) {
        this.itemSKU = itemSKU;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}