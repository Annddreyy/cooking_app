package com.example.cookingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class RecipePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        int recipe_id = getIntent().getIntExtra("recipe_id", 0);

        TextView title = findViewById(R.id.recipe_title);
        title.setText(String.valueOf(recipe_id));

        LinearLayout recipesBookButton = findViewById(R.id.book_of_recipes_button);
        recipesBookButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipesBookActivity.class);
            view.getContext().startActivity(intent);});

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

        new GetRecipeTask().execute("https://cooking-app-api-seven.vercel.app/api/v1/recipes/" + recipe_id);
    }

    private class GetRecipeTask extends AsyncTask<String, Void, String> {

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

                TextView titleText = findViewById(R.id.recipe_title);
                titleText.setText(jsonObject.getString("title"));

                TextView calloriesText = findViewById(R.id.callories_text);
                calloriesText.setText(String.format("%s ккал.", jsonObject.getString("callories")));

                TextView cookingTimeText = findViewById(R.id.time_text);
                cookingTimeText.setText(String.format("%s мин.", jsonObject.getString("cooking_time")));

                TextView complexityText = findViewById(R.id.complexity_text);
                complexityText.setText(jsonObject.getString("complexity"));

                TextView descriptionText = findViewById(R.id.description_text);
                descriptionText.setText(jsonObject.getString("description"));

                ImageView recipeImage = findViewById(R.id.recipe_image);
                Glide.with(getApplicationContext()).load(jsonObject.getString("image_path") + "?raw=true").into(recipeImage);

                } catch (JSONException ex) {}
        }
    }
}