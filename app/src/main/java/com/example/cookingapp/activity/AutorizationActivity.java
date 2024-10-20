package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.GetAuthorizationTask;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.SHA256;
import com.example.cookingapp.model.AuthorizationInfo;

public class AutorizationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autorization);

        TextView errorText = findViewById(R.id.error_text);
        errorText.setText("");

        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);

        TextView registrationLink = findViewById(R.id.registration_link);
        registrationLink.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RegistrationActivity1.class);
            view.getContext().startActivity(intent);
        });


        Button autorizationButton = findViewById(R.id.autorization_button);
        autorizationButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String passwordSHA256 = SHA256.calculateSHA256(password);

            boolean findUser = false;

            for (AuthorizationInfo authorizationInfo: GetAuthorizationTask.authorizationInformation) {
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

        new GetAuthorizationTask().execute(HTTPHelper.baseUrl + "/authorization");
    }
}