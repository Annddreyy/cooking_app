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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form1);

        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);

        Button registrationButton = findViewById(R.id.registration_button);

        new GetAuthorizationTask().execute(HTTPHelper.baseUrl + "/authorization");

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

                    for (AuthorizationInfo authorizationInfo: GetAuthorizationTask.authorizationInformation) {
                        if (Objects.equals(authorizationInfo.email, emailText)) {
                            errorText.setText("Пользователь с такой почтой уже существует!");
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
}
