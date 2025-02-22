package com.example.profilemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profileManager.db";
    private static final int DATABASE_VERSION = 1;

    // Profile Table
    private static final String TABLE_PROFILE = "Profile";
    private static final String COLUMN_PROFILE_ID = "ProfileID";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_SURNAME = "Surname";
    private static final String COLUMN_GPA = "GPA";
    private static final String COLUMN_CREATION_DATE = "CreationDate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SURNAME + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GPA + " REAL, " +
                COLUMN_CREATION_DATE + " TEXT)";

        db.execSQL(createProfileTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    public boolean insertProfile(int profileID, String surname, String name, float gpa) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if ID already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + "=?", new String[]{String.valueOf(profileID)});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Duplicate ID
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_ID, profileID);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_GPA, gpa);
        values.put(COLUMN_CREATION_DATE, getCurrentTimestamp());

        return db.insert(TABLE_PROFILE, null, values) != -1;
    }

    public List<String[]> getAllProfiles(boolean sortByID) {
        List<String[]> profiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = sortByID ? COLUMN_PROFILE_ID + " ASC" : COLUMN_SURNAME + " ASC";

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SURNAME + ", " + COLUMN_NAME + ", " + COLUMN_PROFILE_ID +
                " FROM " + TABLE_PROFILE + " ORDER BY " + orderBy, null);

        if (cursor.moveToFirst()) {
            do {
                profiles.add(new String[]{
                        cursor.getString(0), // Surname
                        cursor.getString(1), // Name
                        cursor.getString(2)  // Profile ID
                });
            } while (cursor.moveToNext());
        }
        cursor.close();
        return profiles;
    }


    public String[] getProfileDetails(int profileID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + "=?", new String[]{String.valueOf(profileID)});

        if (cursor.moveToFirst()) {
            String[] details = {
                    cursor.getString(1), // Surname
                    cursor.getString(2), // Name
                    String.valueOf(cursor.getInt(0)), // ID
                    String.valueOf(cursor.getFloat(3)), // GPA
                    cursor.getString(4) // Creation Date
            };
            cursor.close();
            return details;
        }
        cursor.close();
        return null;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
