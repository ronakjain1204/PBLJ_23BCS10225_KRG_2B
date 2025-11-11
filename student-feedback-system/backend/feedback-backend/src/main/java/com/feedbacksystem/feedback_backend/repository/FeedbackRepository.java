package com.feedbacksystem.feedback_backend.repository;

import com.feedbacksystem.feedback_backend.dto.AnalyticsDTO;
import com.feedbacksystem.feedback_backend.model.Feedback;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Feedback documents.
 */
@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    /**
     * Finds all feedback submissions for a specific student ID.
     * This will power the Student Dashboard in Module 3.
     */
    List<Feedback> findByStudentId(String studentId);

    // --- NEW METHODS FOR MODULE 4 ---

    /**
     * Aggregates feedback to get counts for each status.
     * This uses the MongoDB Aggregation Pipeline, as planned.
     *
     * @return A list of AnalyticsDTO objects (e.g., [{_id: "open", count: 5}, {_id: "resolved", count: 2}])
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$status', count: { $sum: 1 } } }",
        "{ $sort: { _id: 1 } }"
    })
    List<AnalyticsDTO> countByStatus();

    /**
     * Aggregates feedback to get counts for each category.
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$category', count: { $sum: 1 } } }",
        "{ $sort: { _id: 1 } }"
    })
    List<AnalyticsDTO> countByCategory();
}