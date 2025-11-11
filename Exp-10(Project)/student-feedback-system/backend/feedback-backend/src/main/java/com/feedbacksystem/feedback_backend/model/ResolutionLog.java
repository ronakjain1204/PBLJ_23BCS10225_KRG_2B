package com.feedbacksystem.feedback_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A POJO to represent the embedded resolution log object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionLog {

    private String resolvedByAdminId;
    private String resolutionNote;
    private LocalDateTime timestamp;

}