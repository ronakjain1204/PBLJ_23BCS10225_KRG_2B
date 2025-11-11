package com.feedbacksystem.feedback_backend.controller;

import com.feedbacksystem.feedback_backend.model.User; // User model ko import kar rahe hain
import com.feedbacksystem.feedback_backend.dto.FeedbackRequestDTO;
import com.feedbacksystem.feedback_backend.model.Feedback;
import com.feedbacksystem.feedback_backend.repository.UserRepository;
import com.feedbacksystem.feedback_backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List; // List (array) use karne ke liye import

/**
 * Controller to handle feedback submission and retrieval.
 * (Yeh controller student ke feedback ko submit karne aur wapas laane ka kaam karta hai)
 */
@RestController // Yeh batata hai ki yeh file ek API controller hai (jo JSON bhejega).
@RequestMapping("/api/feedback") // Iska matlab hai ki is file ke saare API URLs "/api/feedback" se shuru honge.
public class FeedbackController {

    // @Autowired Spring se bolta hai ki "Mujhe FeedbackService file ka ek object bana kar do."
    @Autowired
    private FeedbackService feedbackService;

    // @Autowired Spring se bolta hai ki "Mujhe UserRepository file ka ek object do."
    // Isse hum user ko database mein find karenge.
    @Autowired
    private UserRepository userRepository;

    /**
     * POST endpoint for a student to submit new feedback.
     * (Yeh POST API endpoint hai naya feedback submit karne ke liye - Module 2)
     * URL: POST /api/feedback/submit
     */
    @PostMapping("/submit")
    // Yeh function chalta hai jab student feedback submit karta hai.
    // @Valid -- validation rules check karta hai (DTO file se)
    // @RequestBody -- frontend se aaye JSON ko FeedbackRequestDTO object mein badal deta hai.
    // Authentication -- Spring Security ismein logged-in user ki details (token se) daal deta hai.
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequestDTO requestDTO, Authentication authentication) {

        // Yeh line 'authentication' object se student ka email nikaal rahi hai.
        String email = authentication.getName();

        // **YEH LINE STUDENT ID FIND KAREGI**
        // Hum email ka use karke database se student ka poora User object nikaal rahe hain.
        User user = userRepository.findByEmail(email)
                // Agar user nahi mila, toh error throw karega.
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // Hum 'FeedbackService' ko bol rahe hain ki "is feedback (requestDTO) ko aur student ki ID (user.getId()) ko database mein save kar do."
        Feedback savedFeedback = feedbackService.submitFeedback(requestDTO, user.getId());

        // Save kiya hua feedback (JSON mein) wapas frontend ko '200 OK' response ke sath bhej rahe hain.
        return ResponseEntity.ok(savedFeedback);
    }

    /**
     * GET endpoint for a student to fetch their own feedback.
     * (Yeh GET API endpoint hai student ka apna feedback history laane ke liye - Module 3)
     * URL: GET /api/feedback/my-feedback
     */
    @GetMapping("/my-feedback")
    // Yeh function chalta hai jab student apna dashboard kholta hai.
    public ResponseEntity<List<Feedback>> getMyFeedback(Authentication authentication) {
        
        // 1. Wapas, 'authentication' object se student ka email nikaal rahe hain.
        String email = authentication.getName();

        // 2. Email ka use karke database se poora User object nikaal rahe hain.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // 3. User object se uski unique MongoDB ID nikaal rahe hain.
        String studentId = user.getId();

        // 4. Hum 'FeedbackService' ko bol rahe hain ki "sirf is studentId ka saara feedback la do."
        List<Feedback> feedbackList = feedbackService.getFeedbackByStudentId(studentId);

        // 5. Poori feedback list (JSON array) ko '200 OK' response ke sath bhej rahe hain.
        return ResponseEntity.ok(feedbackList);
    }
}