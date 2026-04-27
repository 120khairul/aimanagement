package com.example.academicfeedback.service;

import org.springframework.stereotype.Service;

@Service
public class GradingService {

    public String calculateGrade(Double marks, Integer maxMarks) {
        if (marks == null || maxMarks == null || maxMarks <= 0) {
            return "N/A";
        }

        double percentage = (marks / maxMarks) * 100;
        if (percentage >= 80) {
            return "A+";
        }
        if (percentage >= 75) {
            return "A";
        }
        if (percentage >= 70) {
            return "A-";
        }
        if (percentage >= 65) {
            return "B+";
        }
        if (percentage >= 60) {
            return "B";
        }
        if (percentage >= 55) {
            return "B-";
        }
        if (percentage >= 50) {
            return "C+";
        }
        if (percentage >= 45) {
            return "C";
        }
        if (percentage >= 40) {
            return "D";
        }
        return "F";
    }
}
