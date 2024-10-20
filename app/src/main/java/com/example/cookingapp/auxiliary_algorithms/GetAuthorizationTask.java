package com.example.cookingapp.auxiliary_algorithms;

import android.os.AsyncTask;

import com.example.cookingapp.model.AuthorizationInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetAuthorizationTask extends AsyncTask<String, Void, String> {
    public static ArrayList<AuthorizationInfo> authorizationInformation = new ArrayList<>();
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