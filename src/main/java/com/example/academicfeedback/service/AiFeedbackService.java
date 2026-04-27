package com.example.academicfeedback.service;

import com.example.academicfeedback.model.Assignment;
import com.example.academicfeedback.model.Course;
import com.example.academicfeedback.model.Submission;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AiFeedbackService {

    private final OpenAiClientService openAiClientService;

    public AiFeedbackService(OpenAiClientService openAiClientService) {
        this.openAiClientService = openAiClientService;
    }

    public boolean isOpenAiConfigured() {
        return openAiClientService.isConfigured();
    }

    public String getModel() {
        return openAiClientService.getModel();
    }

    public String draftFeedback(Submission submission) {
        String context = "Assignment: " + submission.getAssignment().getTitle()
                + "\nMax marks: " + submission.getAssignment().getMaxMarks()
                + "\nMarks: " + submission.getMarks()
                + "\nGrade: " + submission.getGrade()
                + "\nStudent answer: " + submission.getContent();
        return openAiClientService.generateText(
                "Draft personalized academic feedback. Include strengths, weaknesses, and three next steps.",
                context,
                draftFeedbackLocal(submission)
        );
    }

    public String draftFeedbackLocal(Submission submission) {
        Assignment assignment = submission.getAssignment();
        double percentage = submission.getPercentage();
        StringBuilder feedback = new StringBuilder();

        feedback.append("AI Draft Feedback: ");
        if (percentage >= 80) {
            feedback.append("Excellent work. The submission shows strong understanding and clear organization. ");
        } else if (percentage >= 60) {
            feedback.append("Good attempt. The main idea is present, but the answer needs more depth and stronger examples. ");
        } else if (percentage >= 40) {
            feedback.append("Basic requirements are partially met. Please revise the weak areas and add clearer explanation. ");
        } else {
            feedback.append("The submission needs significant improvement. Focus on the core concepts and resubmit after revision. ");
        }

        if (assignment.getDueDate() != null && submission.getSubmittedAt() != null) {
            LocalDate submittedDate = submission.getSubmittedAt().toLocalDate();
            if (submittedDate.isAfter(assignment.getDueDate())) {
                feedback.append("The submission was late, so time management should be improved. ");
            }
        }

        String content = submission.getContent() == null ? "" : submission.getContent().trim();
        if (content.length() < 120) {
            feedback.append("The response is short; include more explanation, evidence, and step-by-step reasoning. ");
        } else {
            feedback.append("The response length is acceptable; polish the structure and highlight key findings. ");
        }

        feedback.append("Recommended next step: review lecture materials, compare with the rubric, and improve the weakest section first.");
        return feedback.toString();
    }

    public String suggestQuizQuestions(Course course) {
        String fallback = "1. Explain the key learning outcomes of " + course.getTitle() + ".\n"
                + "2. Solve a practical problem related to " + course.getCode() + ".\n"
                + "3. Identify common mistakes and explain how to avoid them.\n"
                + "4. Compare two important concepts from this course with examples.";
        String context = "Course code: " + course.getCode()
                + "\nTitle: " + course.getTitle()
                + "\nDescription: " + course.getDescription();
        return openAiClientService.generateText(
                "Generate 6 assessment questions: 2 MCQ, 2 short answer, and 2 analytical questions.",
                context,
                fallback
        );
    }

    public String curriculumGapAnalysis(String context) {
        String fallback = "Curriculum gap analysis:\n"
                + "1. Map every CLO to a measurable PLO.\n"
                + "2. Add practical assessment evidence for weak outcomes.\n"
                + "3. Review industry alignment, emerging technology coverage, and stakeholder feedback.\n"
                + "4. Keep version history before approval.";
        return openAiClientService.generateText(
                "Analyze curriculum mapping, gaps, industry alignment, stakeholder feedback, and revision priorities.",
                context,
                fallback
        );
    }

    public String faqAnswer(String context) {
        String fallback = "Suggested answer: Please check the course outline, LMS materials, deadline notice, and assessment rubric. Contact the course teacher if the issue is specific to your submission.";
        return openAiClientService.generateText(
                "Create a clear student-facing FAQ answer. Keep it short and helpful.",
                context,
                fallback
        );
    }

    public String capstoneProposalScreening(String context) {
        String fallback = "Proposal screening:\n"
                + "Strengths: The project has a clear academic direction.\n"
                + "Risks: Confirm scope, timeline, originality, and data availability.\n"
                + "Recommendation: Approve with supervisor review if milestones and evaluation rubric are complete.";
        return openAiClientService.generateText(
                "Screen this capstone proposal. Mention strengths, risks, originality concerns, and approval recommendation.",
                context,
                fallback
        );
    }

    public String capstoneEvaluationFeedback(String context) {
        String fallback = "Evaluation feedback:\n"
                + "The team should connect deliverables with rubric criteria, improve documentation, and prepare a concise progress summary for the next review.";
        return openAiClientService.generateText(
                "Draft capstone evaluation feedback using the rubric, score, comments, and project context.",
                context,
                fallback
        );
    }

    public String customAcademicAssistant(String taskType, String prompt, String context) {
        String fallback = "AI assistant fallback:\n"
                + "1. Review the provided academic context carefully.\n"
                + "2. Identify the main academic task, expected output, and missing information.\n"
                + "3. Draft a clear response with practical next steps.\n"
                + "4. Ask the teacher or administrator to verify details before final use.";
        String task = "Task type: " + taskType
                + "\nUser request: " + prompt
                + "\nWrite a polished academic output that is useful for an academic feedback management system.";
        return openAiClientService.generateText(task, context, fallback);
    }
}
