package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;

public class NewRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_recipe);

        setupClickListener(R.id.book_of_recipes_button, RecipesBookActivity.class);
        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.profile_button, ProfileActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

        addSpinner(R.id.complexity_spinner, R.array.times_array);
        addSpinner(R.id.callories_spinner, R.array.callories_array);
        addSpinner(R.id.time_spinner, R.array.times_array);
    }

    private void addSpinner(int spinnerId, int arrayId) {
        Spinner spinner = findViewById(spinnerId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayId,
                R.layout.color_spinner_layout
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
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
}