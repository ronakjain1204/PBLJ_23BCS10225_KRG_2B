package com.feedbacksystem.feedback_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO (Data Transfer Object) for receiving a new feedback submission
 * from the frontend.
 */
@Data
public class FeedbackRequestDTO {

    @NotBlank(message = "Feedback content cannot be empty")
    private String content;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Anonymity status is required")
    private Boolean isAnonymous;
}