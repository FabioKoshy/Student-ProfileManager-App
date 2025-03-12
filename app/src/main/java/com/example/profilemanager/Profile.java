package com.example.profilemanager;

import androidx.annotation.NonNull;

public class Profile {
    private final int profileId;
    private final String name;
    private final String surname;
    private final float gpa;
    private final String creationDate;

    public Profile(int profileId, String name, String surname, float gpa, String creationDate) {
        this.profileId = profileId;
        this.name = name;
        this.surname = surname;
        this.gpa = gpa;
        this.creationDate = creationDate;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public float getGpa() {
        return gpa;
    }

    public String getCreationDate() {
        return creationDate;
    }

    @NonNull
    @Override
    public String toString() {
        return surname + ", " + name + " (ID: " + profileId + ")";
    }
}