package com.example.academicfeedback.model;

public enum SubmissionStatus {
    SUBMITTED("Submitted"),
    GRADED("Graded"),
    RECHECK_REQUESTED("Recheck Requested"),
    RESUBMISSION_REQUESTED("Resubmission Requested");

    private final String displayName;

    SubmissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
