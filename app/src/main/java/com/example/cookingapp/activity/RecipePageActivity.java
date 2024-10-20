package com.example.cookingapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.auxiliary_algorithms.HTTPHelper;
import com.example.cookingapp.model.Ingredient;
import com.example.cookingapp.model.Instruction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipePageActivity extends AppCompatActivity {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    ArrayList<Instruction> instructions = new ArrayList<>();

    boolean isFavourityRecipe = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        int recipe_id = getIntent().getIntExtra("recipe_id", 0);

        TextView title = findViewById(R.id.recipe_title);
        title.setText(String.valueOf(recipe_id));

        setupClickListener(R.id.book_of_recipes_button, RecipesBookActivity.class);
        setupClickListener(R.id.recipes_button, RecipesActivity.class);
        setupClickListener(R.id.profile_button, ProfileActivity.class);
        setupClickListener(R.id.favourites_button, FavouritesActivity.class);

        ImageView favourityStar = findViewById(R.id.favourity_image);
        favourityStar.setOnClickListener(view -> {
            isFavourityRecipe = !isFavourityRecipe;
            if (isFavourityRecipe) {
                favourityStar.setImageResource(R.drawable.star_icon_red_fill);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("client_id", getIntent().getIntExtra("client_id", 0));
                    jsonObject.put("recipe_id", getIntent().getIntExtra("recipe_id", 0));

                } catch (JSONException e) {
                    TextView text = findViewById(R.id.registration_title_text);
                    text.setText(e.getMessage());
                }

                PostFavourityRecipeTask task = new PostFavourityRecipeTask(jsonObject);
                task.execute(HTTPHelper.baseUrl + "/favourite_recipes");
            }
            else {
                favourityStar.setImageResource(R.drawable.star_icon_red);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("client_id", getIntent().getIntExtra("client_id", 0));
                    jsonObject.put("recipe_id", getIntent().getIntExtra("recipe_id", 0));

                } catch (JSONException e) {
                    TextView text = findViewById(R.id.registration_title_text);
                    text.setText(e.getMessage());
                }

                DeleteFavourityRecipeTask task = new DeleteFavourityRecipeTask(jsonObject);
                task.execute(HTTPHelper.baseUrl + "/favourity_recipes");
            }
        });

        new GetRecipesTask().execute(HTTPHelper.baseUrl + "/favourite_recipes/" + getIntent().getIntExtra("client_id", 0));
        new GetRecipeTask().execute(HTTPHelper.baseUrl + "/recipes/" + recipe_id);
        new GetRecipeIngredientsTask().execute(HTTPHelper.baseUrl + "/recipe_ingredients/" + recipe_id);
        new GetRecipeInstructionsTask().execute(HTTPHelper.baseUrl + "/recipe_instructions/" + recipe_id);
    }

    private class GetRecipeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionAndReadData(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                TextView titleText = findViewById(R.id.recipe_title);
                titleText.setText(jsonObject.getString("title"));

                TextView calloriesText = findViewById(R.id.callories_text);
                calloriesText.setText(String.format("%s ккал.", jsonObject.getString("callories")));

                TextView cookingTimeText = findViewById(R.id.time_text);
                cookingTimeText.setText(String.format("%s мин.", jsonObject.getString("cooking_time")));

                TextView complexityText = findViewById(R.id.complexity_text);
                complexityText.setText(jsonObject.getString("complexity"));

                TextView descriptionText = findViewById(R.id.description_text);
                descriptionText.setText(jsonObject.getString("description"));

                ImageView recipeImage = findViewById(R.id.recipe_image);
                Glide.with(getApplicationContext()).load(jsonObject.getString("image_path") + "?raw=true").into(recipeImage);

                } catch (JSONException ex) {}
        }
    }

    private class GetRecipeIngredientsTask extends AsyncTask<String, Void, String> {

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

                    int ingredient_id  = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    String count = jsonObject.getString("count");

                    Ingredient ingredient = new Ingredient(ingredient_id, title, count);

                    ingredients.add(ingredient);
                }

                createIngredientsTable();
            } catch (JSONException e) {}
        }
    }

    private class GetRecipeInstructionsTask extends AsyncTask<String, Void, String> {

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

                    int ingredient_id  = jsonObject.getInt("id");
                    String text = jsonObject.getString("text");

                    Instruction instruction = new Instruction(ingredient_id, text);

                    instructions.add(instruction);
                }

                createInstructionsTable();
            } catch (JSONException e) {}
        }
    }

    private class GetRecipesTask extends AsyncTask<String, Void, String> {

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
                    int recipe_id  = jsonObject.getInt("id");

                    if (recipe_id == getIntent().getIntExtra("recipe_id", 0)) {
                        isFavourityRecipe = true;
                        break;
                    }
                }

                ImageView favourityStar = findViewById(R.id.favourity_image);

                if (isFavourityRecipe)
                    favourityStar.setImageResource(R.drawable.star_icon_red_fill);
                else
                    favourityStar.setImageResource(R.drawable.star_icon_red);

            } catch (JSONException e) {}
        }
    }

    public class PostFavourityRecipeTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonBody;

        public PostFavourityRecipeTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionData(urls[0], jsonBody, "POST");
        }
    }

    public class DeleteFavourityRecipeTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonBody;

        public DeleteFavourityRecipeTask(JSONObject jsonBody) {
            this.jsonBody = jsonBody;
        }

        @Override
        protected String doInBackground(String... urls) {
            return HTTPHelper.createConnectionData(urls[0], jsonBody, "DELETE");
        }
    }

    private void createIngredientsTable() {
        try {
            LinearLayout ingredientsTable = findViewById(R.id.ingredients_table);
            ingredientsTable.removeAllViews();
            for (Ingredient ingredient: ingredients) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View ingredient_table_row = inflater.inflate(R.layout.ingredient_row, null);

                TextView ingredientTitle = ingredient_table_row.findViewById(R.id.ingredient_title);
                ingredientTitle.setText(ingredient.title);

                TextView ingredientCount = ingredient_table_row.findViewById(R.id.ingredient_count);
                ingredientCount.setText(ingredient.count);

                ingredientsTable.addView(ingredient_table_row);
            }
        }
        catch (Exception e) {}
    }

    private void createInstructionsTable() {
        try {
            LinearLayout instructionsTable = findViewById(R.id.instruction_table);
            instructionsTable.removeAllViews();
            for (int i = 0; i < instructions.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View instruction_table_row = inflater.inflate(R.layout.instruction_row, null);

                TextView instructionNumber = instruction_table_row.findViewById(R.id.instruction_number_text);
                instructionNumber.setText(String.valueOf(i + 1));

                TextView instructionText = instruction_table_row.findViewById(R.id.instruction_text);
                instructionText.setText(instructions.get(i).text);

                instructionsTable.addView(instruction_table_row);
            }
        }
        catch (Exception e) {}
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