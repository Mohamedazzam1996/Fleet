package com.fleetmanagment.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fleetmanagment.R;
import com.fleetmanagment.model.Login;
import com.fleetmanagment.service.http.LoginApi;
import com.fleetmanagment.service.http.LoginApi.LoginApiProtocol;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements LoginApiProtocol {
    public static final String LOGIN = "login";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ACCESSTOKEN = "accesstoken";
    private LoginApi loginApi;
    private String username;
    private String password;

    @Override
    public void apiStarted() {

    }

    @Override
    public void apiFinished() {

    }

    @Override
    public void apiFailed(final String error) {
        Toast.makeText(this, "Invalid username", Toast.LENGTH_LONG).show();
    }

    @Override
    public void loginSucceeded(final Login login) {
        final Editor editor = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putString(ACCESSTOKEN, login.data.access_token);
        editor.apply();

        finish();
        final Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginFailed() {
        Toast.makeText(this, "Invalid username", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginApi = new LoginApi(this);
        setUi();

        final SharedPreferences prefs = getSharedPreferences(LOGIN, MODE_PRIVATE);
        username = prefs.getString(USERNAME, null);
        if (username != null) {
            finish();
            final Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        }
    }

    private void setUi() {
        getSupportActionBar().setTitle("");

        final Spinner spinnerLanguage = findViewById(R.id.spinner_langauge);
        final List<String> languages = new ArrayList<>(0);
        languages.add("English");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);
    }

    public void onLoginClick(final View view) {
        username = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        loginApi.start(username, password);
    }
}
