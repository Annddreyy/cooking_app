package com.example.cookingapp.auxiliary_algorithms;

import android.util.Log;
import android.widget.EditText;

import com.example.cookingapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHelper {
    public static String createConnectionAndReadData(String connectionUrl) {
        try {
            URL url = new URL(connectionUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream responseStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    public static String createConnectionAndPostOrPatchData(String connectionUrl, JSONObject jsonBody, String requestType) {
        try {
            URL url = new URL(connectionUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestType);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonBody.toString());
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();

        } catch (Exception e) {}
        return null;
    }
}
