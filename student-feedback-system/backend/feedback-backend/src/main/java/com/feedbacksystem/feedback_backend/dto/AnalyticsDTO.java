package com.feedbacksystem.feedback_backend.dto;

import lombok.Data;

/**
 * A DTO to hold the results of our MongoDB aggregation queries.
 * The "_id" field will be the value we grouped by (e.g., "status" or "category").
 */
@Data
public class AnalyticsDTO {
    private String _id; // This field MUST be named _id to match the aggregation result
    private int count;
}