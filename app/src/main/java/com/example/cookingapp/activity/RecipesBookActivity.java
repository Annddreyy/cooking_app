package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.RecipeCard;

public class RecipesBookActivity extends RecipeCard {
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
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String searchText = findText.getText().toString();

                    findByTitle(searchText);

                    return true;
                }
                return false;
            }
        });

        new GetRecipesTask().execute(HTTPHelper.baseUrl + "/user_recipes/" + + getIntent().getIntExtra("client_id", 0));
        new GetRecipeTypesTask().execute(HTTPHelper.baseUrl + "/recipe_types");
    }

    private void setupClickListener(int viewId, Class<?> targetActivity) {
        LinearLayout button = findViewById(viewId);
        if (button != null) {
            button.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), targetActivity);
                intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
                startActivity(intent);
            });
        }
    }
}