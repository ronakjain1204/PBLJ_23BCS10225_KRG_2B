package com.feedbacksystem.feedback_backend.service;

import com.feedbacksystem.feedback_backend.dto.AnalyticsDTO;
import com.feedbacksystem.feedback_backend.dto.FeedbackAdminViewDTO;
import com.feedbacksystem.feedback_backend.dto.FeedbackRequestDTO;
import com.feedbacksystem.feedback_backend.dto.ReplyDTO;
import com.feedbacksystem.feedback_backend.model.Comment;
import com.feedbacksystem.feedback_backend.model.Feedback;
import com.feedbacksystem.feedback_backend.model.User;
import com.feedbacksystem.feedback_backend.repository.FeedbackRepository;
import com.feedbacksystem.feedback_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates and saves a new feedback submission. (Module 2)
     */
    public Feedback submitFeedback(FeedbackRequestDTO requestDTO, String studentId) {

        Feedback feedback = Feedback.builder()
                .studentId(studentId)
                .isAnonymous(requestDTO.getIsAnonymous())
                .content(requestDTO.getContent())
                .rating(requestDTO.getRating())
                .category(requestDTO.getCategory())
                .status("open") // Default status
                .createdAt(LocalDateTime.now())
                .thread(new ArrayList<>()) // Start with an empty, modifiable list
                .resolutionLog(null) // No resolution yet
                .build();

        return feedbackRepository.save(feedback);
    }

    /**
     * Gets all feedback for a specific student. (Module 3)
     */
    public List<Feedback> getFeedbackByStudentId(String studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    /**
     * Gets all feedback for the admin dashboard. (Module 4)
     */
    public List<FeedbackAdminViewDTO> getAllFeedbackForAdmin() {
        List<Feedback> allFeedback = feedbackRepository.findAll();

        List<String> studentIds = allFeedback.stream()
                .map(Feedback::getStudentId)
                .distinct()
                .toList();

        Map<String, User> userMap = userRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<FeedbackAdminViewDTO> adminViewList = new ArrayList<>();
        for (Feedback feedback : allFeedback) {
            if (feedback.isAnonymous()) {
                adminViewList.add(new FeedbackAdminViewDTO(feedback, "Anonymous", ""));
            } else {
                User student = userMap.get(feedback.getStudentId());
                String name = (student != null) ? student.getName() : "Unknown User";
                String email = (student != null) ? student.getEmail() : "";
                adminViewList.add(new FeedbackAdminViewDTO(feedback, name, email));
            }
        }
        return adminViewList;
    }

    /**
     * Gets the status analytics data. (Module 4)
     */
    public List<AnalyticsDTO> getStatusAnalytics() {
        return feedbackRepository.countByStatus();
    }

    /**
     * Gets the category analytics data. (Module 4)
     */
    public List<AnalyticsDTO> getCategoryAnalytics() {
        return feedbackRepository.countByCategory();
    }

    // --- NEW METHODS FOR MODULE 5 ---

    /**
     * Gets a single, detailed feedback item for an admin.
     * Respects anonymity just like the main list.
     *
     * @param feedbackId The ID of the feedback to fetch.
     * @return A detailed DTO for the admin view.
     */
    public FeedbackAdminViewDTO getFeedbackByIdForAdmin(String feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + feedbackId));

        if (feedback.isAnonymous()) {
            return new FeedbackAdminViewDTO(feedback, "Anonymous", "");
        }

        User student = userRepository.findById(feedback.getStudentId())
                .orElse(null); // Handle case where user might be deleted

        String name = (student != null) ? student.getName() : "Unknown User";
        String email = (student != null) ? student.getEmail() : "";

        return new FeedbackAdminViewDTO(feedback, name, email);
    }

    /**
     * Updates the status of a specific feedback item.
     *
     * @param feedbackId The ID of the feedback to update.
     * @param newStatus  The new status (e.g., "in_progress", "resolved").
     * @return The updated Feedback object.
     */
    public Feedback updateFeedbackStatus(String feedbackId, String newStatus) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + feedbackId));

        feedback.setStatus(newStatus);
        
        // We'll add logic for the ResolutionLog here later
        // if (newStatus.equals("resolved")) { ... }

        return feedbackRepository.save(feedback);
    }

    /**
     * Posts a new reply (a Comment) to a feedback thread.
     *
     * @param feedbackId The ID of the feedback to reply to.
     * @param replyDTO   The DTO containing the reply content.
     * @param adminUserId The ID of the admin who is replying.
     * @return The updated Feedback object with the new comment in its thread.
     */
    public Feedback postReplyToFeedback(String feedbackId, ReplyDTO replyDTO, String adminUserId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + feedbackId));

        Comment newComment = new Comment(
                adminUserId,
                replyDTO.getContent(),
                LocalDateTime.now()
        );

        // Add the new comment to the existing thread
        feedback.getThread().add(newComment);

        // Mark as in_progress if it was open
        if (feedback.getStatus().equals("open")) {
            feedback.setStatus("in_progress");
        }

        return feedbackRepository.save(feedback);
    }

}