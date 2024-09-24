package com.example.cookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class RecipesBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_book);

        LinearLayout recipesButton = findViewById(R.id.recipes_button);
        recipesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipesActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout favouritesButton = findViewById(R.id.favourites_button);
        favouritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FavouritesActivity.class);
            view.getContext().startActivity(intent);});

        Button addRecipeButton = findViewById(R.id.add_recipe_button);
        addRecipeButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), NewRecipeActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout recipeCardLayout2Button = findViewById(R.id.recipe_card_layout2);
        recipeCardLayout2Button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout recipeCardLayout3Button = findViewById(R.id.recipe_card_layout3);
        recipeCardLayout3Button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout recipeCardLayout4Button = findViewById(R.id.recipe_card_layout4);
        recipeCardLayout4Button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            view.getContext().startActivity(intent);});
    }
}