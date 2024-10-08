package com.example.cookingapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes);

        LinearLayout recipesBookButton = findViewById(R.id.book_of_recipes_button);
        recipesBookButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipesBookActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            view.getContext().startActivity(intent);});

        LinearLayout favouritesButton = findViewById(R.id.favourites_button);
        favouritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FavouritesActivity.class);
            view.getContext().startActivity(intent);});


        new GetRecipesTask().execute("https://cooking-app-api.vercel.app/api/v1/recipes");
        new GetRecipeTypesTask().execute("https://cooking-app-api.vercel.app/api/v1/recipe_types");
    }

    private class GetRecipesTask extends AsyncTask<String, Void, String> {

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
                LinearLayout recipes = findViewById(R.id.recipes_cards);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int recipe_id  = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String callories = jsonObject.getString("callories");
                    String cookingTime = jsonObject.getString("cooking_time");
                    String imagePath = jsonObject.getString("image_path");

                    recipes.addView(createRecipeCard(
                            title,
                            callories,
                            cookingTime,
                            imagePath,
                            recipe_id
                    ));
                }
            } catch (JSONException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
            }
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
                LinearLayout categories = findViewById(R.id.categories);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int recipe_id  = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String image_path = jsonObject.getString("image_path");

                    categories.addView(createRecipeTypeCard(title, image_path));
                }
            } catch (JSONException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
            }
        }
    }

    protected View createRecipeTypeCard(
            String title,
            String imageLink
    ) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.category_card, null);

        TextView cardTitle = card.findViewById(R.id.category_text);
        cardTitle.setText(title);

        ImageView imageView = card.findViewById(R.id.category_card_image);
        Glide.with(this).load(imageLink + "?raw=true").into(imageView);

        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            view.getContext().startActivity(intent);});

        return card;
    }

    protected View createRecipeCard(
            String title,
            String callories,
            String time,
            String imageLink,
            int recipe_id
    ) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.recipe_card, null);

        TextView cardTitle = card.findViewById(R.id.recipe_card_title_text);
        cardTitle.setText(title);

        TextView calloriesText = card.findViewById(R.id.callories_card_text);
        calloriesText.setText(String.format("%s калл.", callories));

        TextView timeText = card.findViewById(R.id.time_card_text);
        timeText.setText(String.format("%s мин.", time));

        ImageView imageView = card.findViewById(R.id.recipe_card_image);
        Glide.with(this).load(imageLink + "?raw=true").into(imageView);

        card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipePageActivity.class);
            intent.putExtra("recipe_id", recipe_id);
            view.getContext().startActivity(intent);});

        return card;
    }
}
