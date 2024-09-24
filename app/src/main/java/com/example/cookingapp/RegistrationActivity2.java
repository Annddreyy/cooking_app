package com.example.cookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form2);

        Button autorizationButton = findViewById(R.id.registration_button);
        autorizationButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);});
    }
}