package com.example.cookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

                    String title = jsonObject.getString("title");
                    String callories = jsonObject.getString("callories");
                    String cookingTime = jsonObject.getString("cooking_time");

                    recipes.addView(createRecipeCard(title, callories, cookingTime));
                }
            } catch (JSONException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
            }
        }
    }

    protected View createRecipeCard(String title, String callories, String time) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.recipe_card, null);

        TextView cardTitle = card.findViewById(R.id.recipe_card_title_text);
        cardTitle.setText(title);

        TextView calloriesText = card.findViewById(R.id.callories_card_text);
        calloriesText.setText(callories + " калл.");

        TextView timeText = card.findViewById(R.id.time_card_text);
        timeText.setText(time + " мин.");

        return card;
    }
}
