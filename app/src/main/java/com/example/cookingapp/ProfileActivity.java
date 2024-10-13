package com.example.cookingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
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

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        String client_id = String.valueOf(getIntent().getIntExtra("client_id", 0));

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

        LinearLayout favouritesButton = findViewById(R.id.favourites_button);
        favouritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FavouritesActivity.class);
            intent.putExtra("client_id", getIntent().getIntExtra("client_id", 0));
            view.getContext().startActivity(intent);});

        new GetClientTask().execute("https://cooking-app-api-seven.vercel.app/api/v1/client/" + client_id);
    }

    private class GetClientTask extends AsyncTask<String, Void, String> {

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

                String surname = jsonObject.getString("surname");
                String name = jsonObject.getString("name");
                String patronymic = jsonObject.getString("patronymic");
                String phone = jsonObject.getString("phone");
                String image_path = jsonObject.getString("image_path");

                EditText surnameInput = findViewById(R.id.surname_input);
                surnameInput.setText(surname);

                EditText nameInput = findViewById(R.id.name_input);
                nameInput.setText(name);

                EditText patronymicInput = findViewById(R.id.patronymic_input);
                patronymicInput.setText(patronymic);

                EditText phoneInput = findViewById(R.id.phone_input);
                phoneInput.setText(phone);

                ImageView image = findViewById(R.id.profile_user_image);
                Glide.with(getApplicationContext()).load(image_path + "?raw=true").into(image);

            } catch (JSONException e) {}
        }
    }
}