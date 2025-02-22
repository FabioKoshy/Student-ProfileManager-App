package com.example.profilemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvProfiles;
    private boolean sortByID = true;
    private HashMap<Integer, Integer> profileIDMap; // Map ListView positions to Profile IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        lvProfiles = findViewById(R.id.lv_profiles);
        FloatingActionButton fabAddProfile = findViewById(R.id.fab_add_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadProfiles();

        lvProfiles.setOnItemClickListener((parent, view, position, id) -> {
            int profileID = extractProfileID(position);
            if (profileID != -1) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("profileID", profileID);
                startActivity(intent);
            }
        });

        fabAddProfile.setOnClickListener(v -> {
            InsertProfileDialogFragment dialog = new InsertProfileDialogFragment();
            dialog.show(getSupportFragmentManager(), "InsertProfileDialog");
        });
    }

    private void loadProfiles() {
        List<String> profilesList = new ArrayList<>();
        profileIDMap = new HashMap<>();

        List<String[]> profiles = dbHelper.getAllProfiles(sortByID);

        int counter = 1;
        for (String[] profile : profiles) {
            profileIDMap.put(counter - 1, Integer.parseInt(profile[2])); // Store profile ID
            profilesList.add(counter + ". " + profile[0] + ", " + profile[1]); // "Surname, Name"
            counter++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profilesList);
        lvProfiles.setAdapter(adapter);
    }

    private int extractProfileID(int position) {
        return profileIDMap.getOrDefault(position, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_sort) {
            sortByID = !sortByID;
            loadProfiles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
