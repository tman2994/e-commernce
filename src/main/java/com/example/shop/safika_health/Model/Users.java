package com.example.shop.safika_health.Model;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private String  id;
    private String name;
    private String email;
    private String password;

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    private String photo = "https://pngimage.net/wp-content/uploads/2018/06/no-profile-image-png.png";
    private List<String> healthProblems = new ArrayList<>(), favorited = new ArrayList<>();

    public Users(String id, String name, String email, String password, String  photo, List<String> healthProblems, List<String> favorited) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.healthProblems = healthProblems;
        this.favorited = favorited;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHealthProblems(List<String> healthProblems) {
        this.healthProblems = healthProblems;
    }

    public void setFavrited(List<String> favrited) {
        this.favorited = favrited;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getHealthProblems() {
        return healthProblems;
    }

    public List<String> getFavorited() {
        return favorited;
    }



    public Users() {
    }




}
