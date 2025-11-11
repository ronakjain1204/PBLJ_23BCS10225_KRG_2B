package com.feedbacksystem.feedback_backend.dto;

import com.feedbacksystem.feedback_backend.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for sending detailed feedback to an admin.
 * It includes the student's name and email, which will be
 * set to "Anonymous" if the feedback was submitted anonymously.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAdminViewDTO {

    // All the fields from the original Feedback object
    private Feedback feedback;

    // Additional student details
    private String studentName;
    private String studentEmail;

}