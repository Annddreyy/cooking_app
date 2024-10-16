package com.example.cookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    boolean choose_image = false;

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

        Button changeImage = findViewById(R.id.choose_image_button);
        changeImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        Button saveDataButton = findViewById(R.id.save_data_button);
        saveDataButton.setOnClickListener(view -> {
            EditText surnameInput = findViewById(R.id.surname_input);
            EditText nameInput = findViewById(R.id.name_input);
            EditText patronymicInput = findViewById(R.id.patronymic_input);
            EditText phoneInput = findViewById(R.id.phone_input);

            ImageView photoImage = findViewById(R.id.profile_user_image);

            Pattern pattern = Pattern.compile("\\+\\d\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}");
            Matcher matcher = pattern.matcher(phoneInput.getText().toString());

            boolean isCorrectPhone = matcher.find();

            if (isCorrectPhone || phoneInput.getText().toString().isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (!surnameInput.getText().toString().isEmpty())
                        jsonObject.put("surname", surnameInput.getText().toString());
                    if (!nameInput.getText().toString().isEmpty())
                        jsonObject.put("name", nameInput.getText().toString());
                    if (!patronymicInput.getText().toString().isEmpty())
                        jsonObject.put("patronymic", patronymicInput.getText().toString());
                    if (!phoneInput.getText().toString().isEmpty())
                        jsonObject.put("phone", phoneInput.getText().toString());

                    Bitmap bitmap = ((BitmapDrawable) photoImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    jsonObject.put("image_path", encodedImage);

                } catch (JSONException e) {
                    TextView text = findViewById(R.id.registration_title_text);
                    text.setText(e.getMessage());
                }
                new PatchJsonRequestTask(jsonObject).execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/client/" + client_id);
            }
            else {
                TextView text = findViewById(R.id.error_text3);
                text.setText("Телефон введен в неверном формате");
            }
        });



        new GetClientTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/client/" + client_id);
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
                surnameInput.setHint(surname);

                EditText nameInput = findViewById(R.id.name_input);
                nameInput.setHint(name);

                EditText patronymicInput = findViewById(R.id.patronymic_input);
                patronymicInput.setHint(patronymic);

                EditText phoneInput = findViewById(R.id.phone_input);
                phoneInput.setHint(phone);

                ImageView image = findViewById(R.id.profile_user_image);
                Glide.with(getApplicationContext()).load(image_path + "?raw=true").into(image);

            } catch (JSONException e) {}
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photoImage = findViewById(R.id.profile_user_image);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                photoImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class PatchJsonRequestTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "PostJsonRequestTask";

        private JSONObject jsonBody; // JSON object to send as the request body

        public PatchJsonRequestTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PATCH");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Send the JSON request body
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();
                writer.close();

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();

            } catch (Exception e) {
                EditText text = findViewById(R.id.surname_input);
                text.setText(e.getMessage());
                Log.d(TAG, "POST Response: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                TextView text = findViewById(R.id.registration_title_text);
                text.setText(e.getMessage());
            } catch (Exception e) {
                TextView text = findViewById(R.id.registration_title_text);
                text.setText(e.getMessage());
            }
        }
    }
}