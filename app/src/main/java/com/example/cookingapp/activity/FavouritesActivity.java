package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.model.Recipe;
import com.example.cookingapp.model.RecipeType;

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

public class FavouritesActivity extends AppCompatActivity {
    ArrayList<Recipe> recipes = new ArrayList<>();
    ArrayList<RecipeType> recipeTypes = new ArrayList<>();

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

        new GetRecipesTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/favourite_recipes/" + getIntent().getIntExtra("client_id", 0));
        new GetRecipeTypesTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/recipe_types");
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

    private class GetRecipeTypesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream responseStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int recipe_id  = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String image_path = jsonObject.getString("image_path");

                    RecipeType recipeType = new RecipeType(recipe_id, title, image_path);

                    recipeTypes.add(recipeType);
                }

                createRecipeTypeCards();
            } catch (JSONException e) {}
        }
    }

    private class GetRecipesWithRecipeTypeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream responseStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                ArrayList<Integer> recipes = new ArrayList<>();
                JSONArray recipesArray = jsonObject.getJSONArray("recipes");
                for (int i = 0; i < recipesArray.length(); i++) {
                    recipes.add(recipesArray.getInt(i));
                }

                createRecipeCardsWithType(recipes);

            } catch (JSONException e) {}
        }
    }

    protected void findByTitle(String text) {
        LinearLayout recipesLayout = findViewById(R.id.recipes_cards);
        recipesLayout.removeAllViews();
        for (Recipe recipe: recipes) {
            if (recipe.title.toLowerCase().contains(text.toLowerCase())) {
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
    }

    protected void createRecipeCards() {
        LinearLayout recipesLayout = findViewById(R.id.recipes_cards);
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

    protected void createRecipeCardsWithType(ArrayList<Integer> recipesIds) {
        LinearLayout recipesLayout = findViewById(R.id.recipes_cards);
        recipesLayout.removeAllViews();
        for (Recipe recipe: recipes) {
            if (recipesIds.contains(recipe.recipe_id)) {
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
                    view.getContext().startActivity(intent);
                });

                recipesLayout.addView(card);
            }
        }
    }

    protected void createRecipeTypeCards() {
        LinearLayout categoriesLayout = findViewById(R.id.categories);
        categoriesLayout.removeAllViews();
        for (RecipeType recipeType: recipeTypes) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View card = inflater.inflate(R.layout.category_card, null);

            TextView cardTitle = card.findViewById(R.id.category_text);
            cardTitle.setText(recipeType.title);

            ImageView imageView = card.findViewById(R.id.category_card_image);
            Glide.with(this).load(recipeType.imagePath + "?raw=true").into(imageView);

            card.setOnClickListener(view -> {
                new GetRecipesWithRecipeTypeTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/recipe_with_recipe_type/" + recipeType.recipe_type_id);
            });

            categoriesLayout.addView(card);
        }
    }
}