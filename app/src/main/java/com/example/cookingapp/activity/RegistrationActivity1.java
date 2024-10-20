package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.SHA256;
import com.example.cookingapp.model.AuthorizationInfo;

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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity1 extends AppCompatActivity {
    ArrayList<AuthorizationInfo> authorizationInformation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form1);

        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);

        Button registrationButton = findViewById(R.id.registration_button);

        new GetAuthorizationTask().execute("https://cooking-app-api-andrey2211.amvera.io/api/v1/authorization");

        registrationButton.setOnClickListener(view -> {
            TextView errorText = findViewById(R.id.error_text);

            errorText.setText("");

            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();

            if (!emailText.isEmpty() && !passwordText.isEmpty()) {
                Pattern pattern = Pattern.compile("\\w+@\\w+\\.\\w+");
                Matcher matcher = pattern.matcher(emailText);

                boolean isCorrectEmail = matcher.find();

                if (passwordText.length() >= 8 && isCorrectEmail) {

                    boolean isNewEmail = true;

                    for (AuthorizationInfo authorizationInfo: authorizationInformation) {
                        if (Objects.equals(authorizationInfo.email, emailText)) {
                            errorText.setText("Пользователь с такой почтой уде существует!");
                            isNewEmail = false;
                            break;
                        }
                    }
                    if (isNewEmail) {
                        Intent intent = new Intent(view.getContext(), RegistrationActivity2.class);
                        intent.putExtra("email", emailText);
                        intent.putExtra("password", SHA256.calculateSHA256(passwordText));
                        view.getContext().startActivity(intent);
                    }
                }
                else if (!isCorrectEmail)
                    errorText.setText("Почта введена в неверном формате!");
                else
                    errorText.setText("Пароль меньше 8 символов!");
            }
            else
                errorText.setText("Заполните все поля!");
        });

        TextView autorizationLink = findViewById(R.id.account_link);
        autorizationLink.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AutorizationActivity.class);
            view.getContext().startActivity(intent);});
    }

    private class GetAuthorizationTask extends AsyncTask<String, Void, String> {

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
