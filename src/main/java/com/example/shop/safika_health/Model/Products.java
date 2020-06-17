package com.example.shop.safika_health.Model;

public class Products {


    private String id;
    private String Name;
    private String description;
    private String category;
    private String price;
    private String stock;
    private String allergies;
    private String healthProblem;
    private String ingredients;
    private String imageUrl;
    private String rate_num = "0";
    private String rate = "0";
    private String favorited = "0";

    public Products(String id, String name, String description, String category, String price, String stock, String allergies, String healthProblem, String ingredients, String imageUrl, String rate_num, String rate, String favorited) {
        this.id = id;
        Name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.allergies = allergies;
        this.healthProblem = healthProblem;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.rate_num = rate_num;
        this.rate = rate;
        this.favorited = favorited;
    }

    public Products() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return String.valueOf(stock);
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getHealthProblem() {
        return healthProblem;
    }

    public void setHealthProblem(String healthProblem) {
        this.healthProblem = healthProblem;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl == "" ? "https://f0.pngfuel.com/png/809/958/black-shopping-cart-clip-art-png-clip-art.png" : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRate_num() {
        return String.valueOf(rate_num);
    }

    public void setRate_num(String rate_num) {
        this.rate_num = rate_num;
    }

    public String getRate() {
        return String.valueOf(rate);
    }


    public String getFavorited() {
        return String.valueOf(favorited);
    }

    public void setFavorited(String favorited) {
        this.favorited = favorited;
    }


    public void setRate(String rate) {
        this.rate = rate;
    }
}
