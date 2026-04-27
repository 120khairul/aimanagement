package com.example.academicfeedback.model;

public enum MilestoneStatus {
    PENDING("Pending"),
    SUBMITTED("Submitted"),
    REVIEWED("Reviewed"),
    LATE("Late");

    private final String displayName;

    MilestoneStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
