package com.feedbacksystem.feedback_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving an admin's reply content.
 */
@Data
@NoArgsConstructor
public class ReplyDTO {

    @NotBlank(message = "Reply content cannot be empty")
    private String content;
}