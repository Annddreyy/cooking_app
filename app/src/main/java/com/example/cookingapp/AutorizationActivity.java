package com.example.cookingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class AutorizationActivity extends AppCompatActivity {
    ArrayList<AuthorizationInfo> authorizationInformation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autorization);

        TextView errorText = findViewById(R.id.error_text);
        errorText.setText("");

        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);

        Button autorizationButton = findViewById(R.id.autorization_button);
        autorizationButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String passwordSHA256 = SHA256.calculateSHA256(password);

            boolean findUser = false;

            for (AuthorizationInfo authorizationInfo: authorizationInformation) {
                if (email.equals(authorizationInfo.email) && passwordSHA256.equals(authorizationInfo.password)) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("client_id", authorizationInfo.client_id);
                    view.getContext().startActivity(intent);
                    findUser = true;
                    break;
                }
            }

            if (!findUser)
                errorText.setText("Неверная почта или пароль!");
        });

        new GetAuthorizationTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/authorization");
    }

    private class GetAuthorizationTask extends AsyncTask<String, Void, String> {

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

                    int client_id = jsonObject.getInt("id");
                    String email = jsonObject.getString("email");
                    String password = jsonObject.getString("password");

                    AuthorizationInfo authorizationInfo = new AuthorizationInfo(client_id, email, password);

                    authorizationInformation.add(authorizationInfo);
                }
            } catch (JSONException e) {}
        }
    }
}