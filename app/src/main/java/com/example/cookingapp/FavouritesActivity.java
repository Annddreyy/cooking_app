package com.example.cookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class FavouritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_recipes);

        LinearLayout recipesBookButton = findViewById(R.id.book_of_recipes_button);
        recipesBookButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipesBookActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            view.getContext().startActivity(intent);});

        LinearLayout recipesButton = findViewById(R.id.recipes_button);
        recipesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipesActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            view.getContext().startActivity(intent);});

        LinearLayout profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
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