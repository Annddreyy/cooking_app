package com.example.cookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity1 extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String clientKey = "client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form1);

        databaseReference = FirebaseDatabase.getInstance().getReference(clientKey);

        Button registrationButton = findViewById(R.id.registration_button);
        registrationButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RegistrationActivity2.class);
            view.getContext().startActivity(intent);});

        TextView autorizationLink = findViewById(R.id.account_link);
        autorizationLink.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AutorizationActivity.class);
            view.getContext().startActivity(intent);});
    }


}
