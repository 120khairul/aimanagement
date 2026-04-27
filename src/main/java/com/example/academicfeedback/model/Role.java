package com.example.academicfeedback.model;

public enum Role {
    ADMIN("Admin"),
    FACULTY("Faculty"),
    STUDENT("Student");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
