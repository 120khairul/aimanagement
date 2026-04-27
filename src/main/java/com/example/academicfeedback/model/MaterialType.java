package com.example.academicfeedback.model;

public enum MaterialType {
    SLIDE("Lecture Slide"),
    NOTE("Lecture Note"),
    VIDEO("Video"),
    LINK("Reference Link"),
    LAB_MANUAL("Lab Manual"),
    OTHER("Other");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
