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
import com.example.cookingapp.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Recipe> recipes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            view.getContext().startActivity(intent);
        });

        LinearLayout favouritesButton = findViewById(R.id.favourites_button);
        favouritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FavouritesActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            view.getContext().startActivity(intent);});

        new GetRecipesTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/new_recipes");
        new GetMostPopularRecipeTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/most_popular_recipe");
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

                    int recipe_id  = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String callories = jsonObject.getString("callories");
                    String cookingTime = jsonObject.getString("cooking_time");
                    String complexity = jsonObject.getString("complexity");
                    String description = jsonObject.getString("description");
                    String imagePath = jsonObject.getString("image_path");
                    String date = jsonObject.getString("date");

                    Recipe recipe = new Recipe(
                            recipe_id, title, callories,
                            cookingTime, complexity, description,
                            imagePath, date
                    );

                    recipes.add(recipe);
                }

                createRecipeCards();

            } catch (JSONException e) {}
        }
    }

    protected void createRecipeCards() {
        LinearLayout recipesLayout = findViewById(R.id.new_recipes_cards);
        recipesLayout.removeAllViews();
        for (Recipe recipe: recipes) {
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

            recipesLayout.addView(card);
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

                int recipe_id  = jsonObject.getInt("id");
                String title = jsonObject.getString("title");
                String callories = jsonObject.getString("callories");
                String cookingTime = jsonObject.getString("cooking_time");
                String imagePath = jsonObject.getString("image_path");

                createMostPopularRecipeCard(recipe_id, title, callories, cookingTime, imagePath);

            } catch (JSONException e) {}
        }
    }

    protected void createMostPopularRecipeCard(
            int recipe_id,
            String title,
            String callories,
            String cooking_time,
            String image_path
    ) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.recipe_card, null);

        TextView cardTitle = card.findViewById(R.id.recipe_card_title_text);
        cardTitle.setText(title);

        TextView calloriesText = card.findViewById(R.id.callories_card_text);
        calloriesText.setText(String.format("%s калл.", callories));

        TextView timeText = card.findViewById(R.id.time_card_text);
        timeText.setText(String.format("%s мин.", cooking_time));

        ImageView imageView = card.findViewById(R.id.recipe_card_image);
        Glide.with(this).load(image_path + "?raw=true").into(imageView);

        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            intent.putExtra("recipe_id", recipe_id);
            view.getContext().startActivity(intent);});

        LinearLayout recipeOfDayLayout = findViewById(R.id.recipe_of_day_card_layout);
        recipeOfDayLayout.removeAllViews();
        recipeOfDayLayout.addView(card);

    }
}