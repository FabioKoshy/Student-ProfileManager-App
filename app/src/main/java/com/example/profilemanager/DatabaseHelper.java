package com.example.profilemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ProfileManager.db";
    private static final int DATABASE_VERSION = 1;

    // Profile Table
    private static final String TABLE_PROFILE = "Profile";
    public static final String COLUMN_PROFILE_ID = "ProfileID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_GPA = "GPA";
    public static final String COLUMN_CREATION_DATE = "CreationDate";

    // Access Table
    private static final String TABLE_ACCESS = "Access";
    private static final String COLUMN_ACCESS_ID = "AccessID";
    private static final String COLUMN_PROFILE_ID_FK = "ProfileID";
    private static final String COLUMN_ACCESS_TYPE = "AccessType";
    private static final String COLUMN_TIMESTAMP = "Timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " ("
                + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_SURNAME + " TEXT, "
                + COLUMN_GPA + " REAL, "
                + COLUMN_CREATION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String createAccessTable = "CREATE TABLE " + TABLE_ACCESS + " ("
                + COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PROFILE_ID_FK + " INTEGER, "
                + COLUMN_ACCESS_TYPE + " TEXT, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_PROFILE_ID_FK + ") REFERENCES " + TABLE_PROFILE + "(" + COLUMN_PROFILE_ID + "))";
        db.execSQL(createProfileTable);
        db.execSQL(createAccessTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS);
        onCreate(db);
    }

    public boolean addProfile(int profileId, String name, String surname, float gpa) {
        if (isProfileIdExists(profileId)) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_ID, profileId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_GPA, gpa);
        try {
            long result = db.insert(TABLE_PROFILE, null, values);
            if (result != -1) {
                addAccessEntry(profileId, "Created");
                return true;
            }
            return false;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public void addAccessEntry(int profileId, String accessType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_ID_FK, profileId);
        values.put(COLUMN_ACCESS_TYPE, accessType);
        try {
            db.insert(TABLE_ACCESS, null, values);
        } catch (SQLiteException e) {
            // Log or handle silently
        }
    }

    public boolean isProfileIdExists(int profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + "=?",
                new String[]{String.valueOf(profileId)}
        );
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor getProfiles(String sortMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = "By ID".equals(sortMode) ? COLUMN_PROFILE_ID + " ASC" : COLUMN_SURNAME + " ASC";
        return db.rawQuery(
                "SELECT " + COLUMN_PROFILE_ID + ", " + COLUMN_SURNAME + ", " + COLUMN_NAME +
                        " FROM " + TABLE_PROFILE + " ORDER BY " + orderBy,
                null
        );
    }

    public int getProfileIdByPosition(int position, String sortMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = "By ID".equals(sortMode) ? COLUMN_PROFILE_ID + " ASC" : COLUMN_SURNAME + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PROFILE_ID + " FROM " + TABLE_PROFILE + " ORDER BY " + orderBy,
                null
        );
        if (cursor.moveToPosition(position)) {
            int foundProfileId = cursor.getInt(0);
            cursor.close();
            return foundProfileId;
        }
        cursor.close();
        return -1;
    }

    public Cursor getProfileByID(int profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + "=?",
                new String[]{String.valueOf(profileId)}
        );
    }

    public Cursor getAccessHistory(int profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COLUMN_ACCESS_TYPE + ", " + COLUMN_TIMESTAMP +
                        " FROM " + TABLE_ACCESS + " WHERE " + COLUMN_PROFILE_ID_FK + "=? " +
                        " ORDER BY " + COLUMN_TIMESTAMP + " DESC",
                new String[]{String.valueOf(profileId)}
        );
    }

    public void deleteProfile(int profileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_PROFILE, COLUMN_PROFILE_ID + "=?", new String[]{String.valueOf(profileId)});
        } catch (SQLiteException e) {
            // Log or handle silently
        }
    }
}