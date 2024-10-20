package com.example.cookingapp.model;

public class RecipeType {
    public int recipe_type_id;
    public String title;
    public String imagePath;

    public RecipeType(int id, String title, String imagePath) {
        this.recipe_type_id = id;
        this.title = title;
        this.imagePath = imagePath;
    }
}
