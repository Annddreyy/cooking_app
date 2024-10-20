package com.example.cookingapp.activity;

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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form2);

        EditText surnameInput = findViewById(R.id.surname_input);
        EditText nameInput = findViewById(R.id.name_input);
        EditText patronymicInput = findViewById(R.id.patronymic_input);
        EditText phoneInput = findViewById(R.id.phone_input);
        ImageView photoImage = findViewById(R.id.profile_image);

        Button changeImage = findViewById(R.id.choose_image_button);
        changeImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        Button autorizationButton = findViewById(R.id.registration_button);
        autorizationButton.setOnClickListener(view -> {
            TextView errorText = findViewById(R.id.error_text2);

            errorText.setText("");

            String surname = surnameInput.getText().toString();
            String name = nameInput.getText().toString();
            String patronymic = patronymicInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (!surname.isEmpty() && !name.isEmpty() && !patronymic.isEmpty() && !phone.isEmpty()) {
                Pattern pattern = Pattern.compile("\\+\\d\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}");
                Matcher matcher = pattern.matcher(phone);

                boolean isCorrectPhone = matcher.find();

                if (isCorrectPhone) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("surname", surname);
                        jsonObject.put("name", name);
                        jsonObject.put("patronymic", patronymic);
                        jsonObject.put("phone", phone);
                        jsonObject.put("email", getIntent().getStringExtra("email"));
                        jsonObject.put("password", getIntent().getStringExtra("password"));

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

                    PostJsonRequestTask task = new PostJsonRequestTask(jsonObject);
                    task.execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/client");
                }
                else
                    errorText.setText("Номер телефона введен в неверном формате!");
            }
            else
                errorText.setText("Заполните все поля!");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photoImage = findViewById(R.id.profile_image);
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

    public class PostJsonRequestTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "PostJsonRequestTask";

        private JSONObject jsonBody;

        public PostJsonRequestTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionAndPostData(urls[0], jsonBody);
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("client_id", jsonObject.getInt("id"));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
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