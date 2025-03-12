package com.example.profilemanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameView, surnameView, idView, gpaView, creationDateView;
    private ListView historyListView;
    private ArrayList<Access> history;
    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_details_title);
        }

        nameView = findViewById(R.id.nameView);
        surnameView = findViewById(R.id.surnameView);
        idView = findViewById(R.id.profileIdView);
        gpaView = findViewById(R.id.gpaView);
        creationDateView = findViewById(R.id.creationDateView);
        historyListView = findViewById(R.id.historyListView);
        Button deleteButton = findViewById(R.id.deleteButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        history = new ArrayList<>();

        profileId = getIntent().getIntExtra("PROFILE_ID", -1);
        if (profileId == -1) {
            finish();
            return;
        }

        dbHelper.addAccessEntry(profileId, "Opened");
        loadProfileDetails(dbHelper);
        loadAccessHistory(dbHelper);

        deleteButton.setOnClickListener(v -> deleteProfile(dbHelper));
    }

    private void loadProfileDetails(DatabaseHelper dbHelper) {
        Cursor cursor = dbHelper.getProfileByID(profileId);
        if (cursor != null && cursor.moveToFirst()) {
            Profile profile = new Profile(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getFloat(3),
                    cursor.getString(4)
            );
            idView.setText(String.valueOf(profile.getProfileId()));
            nameView.setText(profile.getName());
            surnameView.setText(profile.getSurname());
            gpaView.setText(String.valueOf(profile.getGpa()));
            String formattedCreationDate = profile.getCreationDate().replace(" ", " @ ");
            creationDateView.setText(getString(R.string.profile_created, formattedCreationDate));
            cursor.close();
        } else {
            Toast.makeText(this, R.string.profile_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAccessHistory(DatabaseHelper dbHelper) {
        Cursor cursor = dbHelper.getAccessHistory(profileId);
        history.clear();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String accessType = cursor.getString(0);
                String timestamp = cursor.getString(1);
                String formattedTimestamp = timestamp.replace(" ", " @ ");
                history.add(new Access(0, profileId, accessType, formattedTimestamp));
            }
            cursor.close();
        } else {
            history.add(new Access(0, profileId, "No history available", ""));
        }

        ArrayAdapter<Access> historyAdapter = new ArrayAdapter<>(this, R.layout.list_item_access, R.id.accessType, history) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView accessTypeView = view.findViewById(R.id.accessType);
                TextView timestampView = view.findViewById(R.id.accessTimestamp);
                Access entry = history.get(position);
                accessTypeView.setText(entry.getAccessType());
                timestampView.setText(entry.getTimestamp());
                return view;
            }
        };
        historyListView.setAdapter(historyAdapter);
    }

    private void deleteProfile(DatabaseHelper dbHelper) {
        dbHelper.addAccessEntry(profileId, "Deleted");
        dbHelper.deleteProfile(profileId);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addAccessEntry(profileId, "Closed");
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