package com.feedbacksystem.feedback_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving a new status update from the admin.
 */
@Data
@NoArgsConstructor
public class StatusDTO {

    @NotBlank(message = "Status cannot be empty")
    private String status;
}