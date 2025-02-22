package com.example.profilemanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int profileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        profileID = getIntent().getIntExtra("profileID", -1);

        if (profileID == -1) {
            finish();
            return;
        }

        String[] details = dbHelper.getProfileDetails(profileID);
        if (details == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tv_profile_name)).setText("Name: " + details[1]);
        ((TextView) findViewById(R.id.tv_profile_surname)).setText("Surname: " + details[0]);
        ((TextView) findViewById(R.id.tv_profile_id)).setText("ID: " + details[2]);
        ((TextView) findViewById(R.id.tv_profile_gpa)).setText("GPA: " + details[3]);
        ((TextView) findViewById(R.id.tv_profile_creation_date)).setText("Created: " + details[4]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
