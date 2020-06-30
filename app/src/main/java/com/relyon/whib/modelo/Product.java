package com.relyon.whib.modelo;

import java.util.HashMap;

public class Product {

    private String productUID;
    private String itemSKU;
    private String imagePath;
    private String title;
    private String description;
    private float price;
    private Long purchaseDate;
    private int quantity = 0;

    public Product() {
    }

    public Product(String productUID, String itemSKU, String imagePath, String title, String description, float price, Long purchaseDate) {
        this.productUID = productUID;
        this.itemSKU = itemSKU;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    public String getProductUID() {
        return productUID;
    }

    public void setProductUID(String productUID) {
        this.productUID = productUID;
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

    public Product isContained(HashMap<String, Product> products) {
        if (products == null) {
            return null;
        }
        for (Product product : products.values()) {
            if (product.getItemSKU().equals(getItemSKU()) && product.getTitle().equals(getTitle())) {
                return product;
            }
        }
        return null;
    }
}