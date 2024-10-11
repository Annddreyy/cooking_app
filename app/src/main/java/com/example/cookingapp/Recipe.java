package com.example.cookingapp;

public class Recipe {
    int recipe_id;
    String title;
    String callories;
    String cooking_time;
    String complexity;
    String description;
    String image_path;
    String date;

    Recipe(int recipe_id, String title, String callories,
           String cooking_time, String complexity, String description,
           String image_path, String date) {
        this.recipe_id = recipe_id;
        this.title = title;
        this.callories = callories;
        this.cooking_time = cooking_time;
        this.complexity = complexity;
        this.description = description;
        this.image_path = image_path;
        this.date = date;
    }
}
