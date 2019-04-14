package com.fleetmanagment.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fleetmanagment.R;
import com.fleetmanagment.model.Login;
import com.fleetmanagment.model.WorkloadData;
import com.fleetmanagment.service.http.LoginApi;
import com.fleetmanagment.service.http.LoginApi.LoginApiProtocol;
import com.fleetmanagment.service.http.WorkloadListAllApi;
import com.fleetmanagment.service.http.WorkloadListAllApi.WorkloadListAllApiProtocol;
import com.fleetmanagment.ui.adapter.ScheduleAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleActivity extends AppCompatActivity {
    private WorkloadListAllApi workloadListAllApi;
    private String username;
    private String password;
    private String accessToken;
    private LoginApi loginApi;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        if (ActivityCompat.checkSelfPermission(ScheduleActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(ScheduleActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScheduleActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        final SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN, MODE_PRIVATE);
        username = prefs.getString(LoginActivity.USERNAME, null);
        password = prefs.getString(LoginActivity.PASSWORD, null);
        accessToken =prefs.getString(LoginActivity.ACCESSTOKEN, null);

        setuoUi();
        authenticate();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLogout(final View view) {
        final Editor editor = getSharedPreferences(LoginActivity.LOGIN, MODE_PRIVATE).edit();
        editor.remove(LoginActivity.USERNAME);
        editor.apply();
        finish();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setuoUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Schedule");

        final Spinner spinnerStatus = findViewById(R.id.spinner_status);
        final List<String> status = new ArrayList<>(0);
        status.add("Active");
        status.add("Completed");
        status.add("Canceled");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void authenticate() {
        loginApi = new LoginApi(new LoginApiProtocol() {
            @Override
            public void loginSucceeded(final Login login) {
                loadWorkloads();
            }

            @Override
            public void loginFailed() {

            }

            @Override
            public void apiStarted() {

            }

            @Override
            public void apiFinished() {

            }

            @Override
            public void apiFailed(final String error) {

            }
        });
        loginApi.start(username, password);
    }

    private void loadWorkloads() {
        final RecyclerView recyclerView = findViewById(R.id.schedule_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ScheduleActivity self = this;

        workloadListAllApi = new WorkloadListAllApi(new WorkloadListAllApiProtocol() {
            @Override
            public void workloadsReady(final WorkloadData[] workloadData) {
                recyclerView.setAdapter(new ScheduleAdapter(workloadData, self));
            }

            @Override
            public void listAllWorkloadsFailed(final String error) {

            }

            @Override
            public void apiStarted() {

            }

            @Override
            public void apiFinished() {

            }

            @Override
            public void apiFailed(final String error) {

            }
        });
        workloadListAllApi.start(username);
    }
}
