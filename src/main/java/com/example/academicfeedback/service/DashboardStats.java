package com.example.academicfeedback.service;

public class DashboardStats {

    private final long userCount;
    private final long courseCount;
    private final long assignmentCount;
    private final long submissionCount;
    private final long recheckCount;
    private final long curriculumCount;
    private final long faqCount;
    private final long capstoneCount;
    private final double averagePercentage;

    public DashboardStats(long userCount, long courseCount, long assignmentCount, long submissionCount,
                          long recheckCount, long curriculumCount, long faqCount, long capstoneCount,
                          double averagePercentage) {
        this.userCount = userCount;
        this.courseCount = courseCount;
        this.assignmentCount = assignmentCount;
        this.submissionCount = submissionCount;
        this.recheckCount = recheckCount;
        this.curriculumCount = curriculumCount;
        this.faqCount = faqCount;
        this.capstoneCount = capstoneCount;
        this.averagePercentage = averagePercentage;
    }

    public long getUserCount() {
        return userCount;
    }

    public long getCourseCount() {
        return courseCount;
    }

    public long getAssignmentCount() {
        return assignmentCount;
    }

    public long getSubmissionCount() {
        return submissionCount;
    }

    public long getRecheckCount() {
        return recheckCount;
    }

    public long getCurriculumCount() {
        return curriculumCount;
    }

    public long getFaqCount() {
        return faqCount;
    }

    public long getCapstoneCount() {
        return capstoneCount;
    }

    public double getAveragePercentage() {
        return averagePercentage;
    }
}
