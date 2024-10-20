package com.example.cookingapp.model;

public class Recipe {
    public int recipe_id;
    public String title;
    public String callories;
    public String cooking_time;
    public String complexity;
    public String description;
    public String image_path;
    public String date;

    public Recipe(int recipe_id, String title, String callories,
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
