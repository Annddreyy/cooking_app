package com.example.cookingapp;

public class RecipeType {
    int recipe_type_id;
    String title;
    String imagePath;
    RecipeType(int id, String title, String imagePath) {
        this.recipe_type_id = id;
        this.title = title;
        this.imagePath = imagePath;
    }
}
