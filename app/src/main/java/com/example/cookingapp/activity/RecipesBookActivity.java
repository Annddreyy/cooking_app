package com.example.cookingapp.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.RecipeCardLayout;

public class RecipesBookActivity extends RecipeCardLayout {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_book);

        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.profile_button, ProfileActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

        EditText findText = findViewById(R.id.search_recipe_input);
        findText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return onKeyPush(v, keyCode, event, findText);
            }
        });

        new GetRecipesTask().execute(HTTPHelper.baseUrl + "/user_recipes/" + + getIntent().getIntExtra("client_id", 0));
        new GetRecipeTypesTask().execute(HTTPHelper.baseUrl + "/recipe_types");
    }
}