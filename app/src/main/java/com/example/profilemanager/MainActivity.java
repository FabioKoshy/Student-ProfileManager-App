package com.example.profilemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements InsertProfileDialogFragment.OnProfileAddedListener {
    private TextView textView; // Kept as field for use in loadProfiles()
    private ArrayAdapter<String> adapter;
    private ArrayList<String> profiles;
    private String sortMode;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }

        textView = findViewById(R.id.textView);
        ListView listView = findViewById(R.id.listView);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        profiles = new ArrayList<>();
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        sortMode = prefs.getBoolean("sortById", true) ? "By ID" : "By Name";

        loadProfiles(dbHelper);

        adapter = new ArrayAdapter<>(this, R.layout.list_item_profile, R.id.profileInfo, profiles) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView lineNumber = view.findViewById(R.id.lineNumber);
                lineNumber.setText(getString(R.string.line_number_format, position + 1));
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int profileId = dbHelper.getProfileIdByPosition(position, sortMode);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("PROFILE_ID", profileId);
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.addProfileButton);
        fab.setOnClickListener(v -> {
            InsertProfileDialogFragment dialog = new InsertProfileDialogFragment();
            dialog.show(getSupportFragmentManager(), "InsertProfileDialog");
        });
    }

    @Override
    public void onProfileAdded() {
        loadProfiles(new DatabaseHelper(this));
    }

    private void loadProfiles(DatabaseHelper dbHelper) {
        profiles.clear();
        Cursor cursor = dbHelper.getProfiles(sortMode);
        int totalProfiles = cursor.getCount();
        textView.setText(getString(R.string.profiles_summary, totalProfiles, sortMode));

        while (cursor.moveToNext()) {
            String profile = "By ID".equals(sortMode) ?
                    String.valueOf(cursor.getInt(0)) :
                    cursor.getString(1) + ", " + cursor.getString(2);
            profiles.add(profile);
        }
        cursor.close();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggle_sort) {
            sortMode = "By ID".equals(sortMode) ? "By Name" : "By ID";
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("sortById", "By ID".equals(sortMode));
            editor.apply();
            loadProfiles(new DatabaseHelper(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}