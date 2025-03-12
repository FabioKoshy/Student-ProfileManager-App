package com.example.profilemanager;

import androidx.annotation.NonNull;

public class Access {
    private final String accessType;
    private final String timestamp;

    public Access(int accessId, int profileId, String accessType, String timestamp) {
        this.accessType = accessType;
        this.timestamp = timestamp;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return accessType + " - " + timestamp;
    }
}