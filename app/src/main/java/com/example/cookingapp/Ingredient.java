package com.example.cookingapp;
public class Ingredient {
    int ingredient_id;
    String title;
    String count;
    Ingredient(int ingredient_id, String title, String count) {
        this.ingredient_id = ingredient_id;
        this.title = title;
        this.count = count;
    }
}
