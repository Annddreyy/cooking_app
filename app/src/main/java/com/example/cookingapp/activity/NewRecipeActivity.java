package com.example.cookingapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

        Button saveRecipeButton = findViewById(R.id.save_recipe_button);
        saveRecipeButton.setOnClickListener(view -> {
            String title = ((EditText)findViewById(R.id.title_input)).getText().toString();
            String description = ((EditText)findViewById(R.id.description_input)).getText().toString();
            String callories = ((EditText)findViewById(R.id.callories_input)).getText().toString();
            String time = ((EditText)findViewById(R.id.time_input)).getText().toString();
            String complexity = ((Spinner)findViewById(R.id.complexity_spinner)).getSelectedItem().toString();

            ArrayList<ArrayList<String>> ingredientsList = new ArrayList<>();
            for (LinearLayout ingredient: ingredients) {
                String titleOfIngredient = ((EditText)ingredient.findViewById(R.id.title_of_ingredient_input)).getText().toString();
                String countOfIngredient = ((EditText)ingredient.findViewById(R.id.count_of_ingredient_input)).getText().toString();
                ingredientsList.add(new ArrayList<>(Arrays.asList(titleOfIngredient, countOfIngredient)));
            }

            ArrayList<String> instructionsList = new ArrayList<>();
            for (EditText ingredient: instructions) {
                instructionsList.add(ingredient.getText().toString());
            }

            ArrayList<String> recipeTypesList = new ArrayList<>();
            for (Spinner recipeType: recipeTypesSpinners) {
                recipeTypesList.add(recipeType.getSelectedItem().toString());
            }

            JSONObject recipeInformation = new JSONObject();
            try {
                recipeInformation.put("title", title);
                recipeInformation.put("description", description);
                recipeInformation.put("callories", callories);
                recipeInformation.put("time", time);
                recipeInformation.put("complexity", complexity);

                JSONArray jsonIngredientsArray = new JSONArray();
                for (LinearLayout ingredient: ingredients) {
                    JSONArray ingredientDescription = new JSONArray();
                    EditText ingredientTitle = ingredient.findViewById(R.id.title_of_ingredient_input);
                    EditText ingredientCount = ingredient.findViewById(R.id.count_of_ingredient_input);
                    ingredientDescription.put(ingredientTitle.getText().toString());
                    ingredientDescription.put(ingredientCount.getText().toString());
                    jsonIngredientsArray.put(ingredientDescription);
                }

                JSONArray jsonInstructionsArray = new JSONArray();
                for (String string : instructionsList) jsonInstructionsArray.put(string);

                JSONArray jsonRecipeTypesArray = new JSONArray();
                for (String string : recipeTypesList) jsonRecipeTypesArray.put(string);

                recipeInformation.put("ingredients", jsonIngredientsArray);
                recipeInformation.put("instructions", jsonInstructionsArray);
                recipeInformation.put("recipe_types", jsonRecipeTypesArray);

                Bitmap bitmap = ((BitmapDrawable) ((ImageView)(findViewById(R.id.recipe_photo))).getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                recipeInformation.put("image_path", encodedImage);
                recipeInformation.put("client_id", getIntent().getIntExtra("client_id", 0));

                new PostJsonRequestTask(recipeInformation).execute(HTTPHelper.baseUrl + "/recipe");
            } catch (JSONException e) {}
        });

        new GetRecipeTypesTask().execute(HTTPHelper.baseUrl + "/recipe_types");
    }

    private class PostJsonRequestTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonBody;

        public PostJsonRequestTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionData(urls[0], jsonBody, "POST");
        }
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