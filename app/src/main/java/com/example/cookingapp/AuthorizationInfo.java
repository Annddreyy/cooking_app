package com.example.cookingapp;

public class AuthorizationInfo {
    int client_id;
    String email;
    String password;
    AuthorizationInfo(int client_id, String email, String password) {
        this.client_id = client_id;
        this.email = email;
        this.password = password;
    }
}
