package com.example.cookingapp.model;
public class Ingredient {
    public int ingredient_id;
    public String title;
    public String count;

    public Ingredient(int ingredient_id, String title, String count) {
        this.ingredient_id = ingredient_id;
        this.title = title;
        this.count = count;
    }
}
