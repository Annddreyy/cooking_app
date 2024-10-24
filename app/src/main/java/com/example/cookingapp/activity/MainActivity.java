package com.example.cookingapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.HTTPObjects;
import com.example.cookingapp.auxiliary_algorithms.RecipeCardLayout;
import com.example.cookingapp.model.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends RecipeCardLayout {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickListener(R.id.book_of_recipes_button, RecipesBookActivity.class);
        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.profile_button, ProfileActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

        new GetRecipesTask().execute(HTTPHelper.baseUrl + "/new_recipes");
        new GetMostPopularRecipeTask().execute(HTTPHelper.baseUrl + "/most_popular_recipe");
    }

    private class GetMostPopularRecipeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionAndReadData(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                LinearLayout recipesLayout = findViewById(R.id.recipe_of_day_card_layout);
                recipesLayout.addView(createRecipeCard(HTTPObjects.createRecipe(jsonObject)));
            } catch (JSONException e) {}
        }
    }

    protected void createRecipeCards() {
        LinearLayout recipesLayout = findViewById(R.id.new_recipes_cards);
        recipesLayout.removeAllViews();
        for (Recipe recipe: recipes) recipesLayout.addView(createRecipeCard(recipe));
    }
}