package com.feedbacksystem.feedback_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A POJO (Plain Old Java Object) to represent an embedded comment
 * in the Feedback.thread list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private String userId; // ID of the student or admin who wrote it
    private String content;
    private LocalDateTime timestamp;

}