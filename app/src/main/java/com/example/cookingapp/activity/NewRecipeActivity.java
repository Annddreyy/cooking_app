package com.example.cookingapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.auxiliary_algorithms.RecipeCardLayout;
import com.example.cookingapp.model.RecipeType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NewRecipeActivity extends AppCompatActivity {
    ArrayList<Spinner> recipeTypesSpinners = new ArrayList<>();
    ArrayList<String> recipeTypes = new ArrayList<>();
    ArrayList<EditText> instructions = new ArrayList<>();
    ArrayList<LinearLayout> ingredients = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_recipe);

        setupClickListener(R.id.book_of_recipes_button, RecipesBookActivity.class);
        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.profile_button, ProfileActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

        addSpinner(R.id.complexity_spinner, R.array.complexity_array);

        Button changeImage = findViewById(R.id.button);
        changeImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        try {
            LinearLayout ingredientsLayout = findViewById(R.id.ingredients_input);
            FloatingActionButton addIngredientButton = findViewById(R.id.add_ingredient_input_button);
            addIngredientButton.setOnClickListener(view -> {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout addIngredientInput = (LinearLayout) inflater.inflate(R.layout.ingredient_input, null);

                ingredientsLayout.addView(addIngredientInput);
                ingredients.add(addIngredientInput);
            });


            LinearLayout instructionsLayout = findViewById(R.id.instructions_input);
            FloatingActionButton addInstructionButton = findViewById(R.id.add_instruction_input_button);
            addInstructionButton.setOnClickListener(view -> {
                LayoutInflater inflater = LayoutInflater.from(this);
                EditText addInstructionInput = (EditText) inflater.inflate(R.layout.instruction_input, null);

                instructionsLayout.addView(addInstructionInput);
                instructions.add(addInstructionInput);
            });

            LinearLayout recipeTypesLayout = findViewById(R.id.recipe_types_input);
            FloatingActionButton addRecipeTypeButton = findViewById(R.id.add_recipe_type_input_button);
            addRecipeTypeButton.setOnClickListener(view -> {
                LayoutInflater inflater = LayoutInflater.from(this);
                Spinner addRecipeInput = (Spinner) inflater.inflate(R.layout.recipe_type_input, null);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.color_spinner_layout, recipeTypes);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                addRecipeInput.setAdapter(adapter);

                recipeTypesLayout.addView(addRecipeInput);
                recipeTypesSpinners.add(addRecipeInput);
            });
        }
        catch (Exception e) {
            TextView text = findViewById(R.id.complexity_help_text);
            text.setText(e.getMessage());
        }

        new GetRecipeTypesTask().execute(HTTPHelper.baseUrl + "/recipe_types");
    }

    public class GetRecipeTypesTask extends AsyncTask<String, Void, String> {
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
                    recipeTypes.add(jsonObject.getString("title"));
                }
            } catch (JSONException e) {}
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photoImage = findViewById(R.id.recipe_photo);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                photoImage.setImageBitmap(bitmap);
            } catch (IOException e) {}
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
}