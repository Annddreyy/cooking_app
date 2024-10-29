package com.example.cookingapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        String client_id = String.valueOf(getIntent().getIntExtra("client_id", 0));

        setupClickListener(R.id.book_of_recipes_button, RecipesBookActivity.class);
        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

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

            Pattern pattern = Pattern.compile("^\\+\\d\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$");
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

                } catch (JSONException e) {}
                new PatchJsonRequestTask(jsonObject).execute(HTTPHelper.baseUrl + "/client/" + client_id);
            }
            else {
                TextView text = findViewById(R.id.error_text3);
                text.setText("Телефон введен в неверном формате! +7(999)999-99-99");
            }
        });
        new GetClientTask().execute(HTTPHelper.baseUrl + "/client/" + client_id);
    }

    private class GetClientTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionAndReadData(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                setHintInEditText(R.id.surname_input, jsonObject.getString("surname"));
                setHintInEditText(R.id.name_input, jsonObject.getString("name"));
                setHintInEditText(R.id.patronymic_input, jsonObject.getString("patronymic"));
                setHintInEditText(R.id.phone_input, jsonObject.getString("phone"));

                ImageView image = findViewById(R.id.profile_user_image);
                Glide.with(getApplicationContext()).load(jsonObject.getString("image_path") + "?raw=true").into(image);

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
            } catch (IOException e) {}
        }
    }

    public class PatchJsonRequestTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonBody;

        public PatchJsonRequestTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionData(urls[0], jsonBody, "PATCH");
        }
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

    private void setHintInEditText(int id, String text) {
        EditText editText = findViewById(id);
        editText.setHint(text);
    }
}