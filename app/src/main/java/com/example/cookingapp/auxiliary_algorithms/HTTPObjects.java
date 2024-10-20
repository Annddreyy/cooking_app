package com.example.cookingapp.auxiliary_algorithms;

import com.example.cookingapp.model.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPObjects {
    public static Recipe createRecipe(JSONObject jsonObject) throws JSONException {
        int recipe_id  = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String callories = jsonObject.getString("callories");
        String cookingTime = jsonObject.getString("cooking_time");
        String complexity = jsonObject.getString("complexity");
        String description = jsonObject.getString("description");
        String imagePath = jsonObject.getString("image_path");
        String date = jsonObject.getString("date");

        return new Recipe(
                recipe_id, title, callories,
                cookingTime, complexity, description,
                imagePath, date
        );
    }
}
