package com.example.shop.safika_health.Model;

public class healthProblem {
    private Integer id;
    private String name;

    public healthProblem(Integer  id, String  name) {
        this.id = id;
        this.name = name;
    }
    public healthProblem() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
