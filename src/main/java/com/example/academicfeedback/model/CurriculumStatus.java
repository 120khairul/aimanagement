package com.example.academicfeedback.model;

public enum CurriculumStatus {
    DRAFT("Draft"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    ARCHIVED("Archived");

    private final String displayName;

    CurriculumStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
