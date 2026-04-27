package com.example.academicfeedback.model;

public enum CapstoneStatus {
    PROPOSED("Proposed"),
    APPROVED("Approved"),
    IN_PROGRESS("In Progress"),
    REVISION_REQUIRED("Revision Required"),
    COMPLETED("Completed");

    private final String displayName;

    CapstoneStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
