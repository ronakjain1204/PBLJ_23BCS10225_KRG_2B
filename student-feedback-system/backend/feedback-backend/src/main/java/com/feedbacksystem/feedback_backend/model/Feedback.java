package com.feedbacksystem.feedback_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the main Feedback document in the 'feedback' collection.
 * This document will embed the thread of comments and a final resolution.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "feedback") // This will be saved in the "feedback" collection
public class Feedback {

    @Id
    private String id;

    private String studentId; // ID of the student who submitted it

    private boolean isAnonymous; // Flag to hide studentId from admins

    private String content;

    private int rating; // 1-5 stars

    private String category; // e.g., "Facilities", "Courses"

    private String status; // e.g., "open", "in_progress", "resolved"

    private LocalDateTime createdAt;

    // --- EMBEDDED OBJECTS ---

    // This is the embedded array of comments (the thread)
    // It's a List of the Comment POJO we just created.
    private List<Comment> thread;

    // This is the embedded resolution log
    // It's the ResolutionLog POJO we just created.
    private ResolutionLog resolutionLog;
}