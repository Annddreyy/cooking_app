package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.HTTPObjects;
import com.example.cookingapp.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Recipe> recipes = new ArrayList<>();
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

    private class GetRecipesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionAndReadData(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    recipes.add(HTTPObjects.createRecipe(jsonObject));
                }

                createRecipeCards();

            } catch (JSONException e) {}
        }
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

    protected View createRecipeCard(Recipe recipe){
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.recipe_card, null);

        TextView cardTitle = card.findViewById(R.id.recipe_card_title_text);
        cardTitle.setText(recipe.title);

        TextView calloriesText = card.findViewById(R.id.callories_card_text);
        calloriesText.setText(String.format("%s калл.", recipe.callories));

        TextView timeText = card.findViewById(R.id.time_card_text);
        timeText.setText(String.format("%s мин.", recipe.cooking_time));

        ImageView imageView = card.findViewById(R.id.recipe_card_image);
        Glide.with(this).load(recipe.image_path + "?raw=true").into(imageView);

        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            intent.putExtra("recipe_id", recipe.recipe_id);
            view.getContext().startActivity(intent);});

        return card;
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