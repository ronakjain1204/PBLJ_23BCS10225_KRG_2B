package com.feedbacksystem.feedback_backend.controller;

// --- YEH SAARI 'IMPORT' STATEMENTS HAIN ---
// Yeh Java ko batati hain ki hum kaun si dusri files (jaise DTOs, Models, Services)
// is file mein use karne wale hain.
import com.feedbacksystem.feedback_backend.dto.AnalyticsDTO;
import com.feedbacksystem.feedback_backend.dto.FeedbackAdminViewDTO;
import com.feedbacksystem.feedback_backend.dto.ReplyDTO;
import com.feedbacksystem.feedback_backend.dto.StatusDTO;
import com.feedbacksystem.feedback_backend.model.Feedback;
import com.feedbacksystem.feedback_backend.model.User;
import com.feedbacksystem.feedback_backend.repository.UserRepository;
import com.feedbacksystem.feedback_backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for all admin-specific endpoints.
 * (Yeh controller admin ke saare API requests sambhalta hai.)
 * All routes are protected by SecurityConfig to require ROLE_ADMIN.
 * (SecurityConfig file in 'config' folder isko protect karti hai, sirf admin hi access kar sakta hai.)
 */
@RestController // Yeh batata hai ki yeh file ek API controller hai (jo JSON bhejega).
@RequestMapping("/api/admin") // Iska matlab hai ki is file ke saare API URLs "/api/admin" se shuru honge.
public class AdminController {

    // @Autowired Spring se bolta hai ki "Mujhe FeedbackService file ka ek object bana kar do."
    // Hum is service ko use karke database se baat karenge.
    @Autowired
    private FeedbackService feedbackService;

    // @Autowired Spring se bolta hai ki "Mujhe UserRepository file ka ek object do."
    // Hum ise admin ka ID find karne ke liye use karenge.
    @Autowired
    private UserRepository userRepository;

    /**
     * GET endpoint for admins to fetch ALL feedback. (Module 4)
     * (Yeh GET API endpoint hai admin dashboard ke liye)
     * URL: GET /api/admin/feedback
     * This endpoint respects the isAnonymous flag.
     * (Yeh 'anonymous' flag ko check karega)
     */
    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackAdminViewDTO>> getAllFeedback() {
        // Hum FeedbackService ko bol rahe hain "getAllFeedbackForAdmin" function chalao.
        List<FeedbackAdminViewDTO> feedbackList = feedbackService.getAllFeedbackForAdmin();
        // Yeh line feedback list ko JSON ke form mein '200 OK' response ke sath bhej deti hai.
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * GET endpoint for dashboard analytics. (Module 4)
     * (Yeh GET API endpoint hai charts ke data ke liye)
     * URL: GET /api/admin/analytics
     * Returns data for both charts.
     */
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        // Service se status (open/resolved) ka data mangwa rahe hain.
        List<AnalyticsDTO> statusData = feedbackService.getStatusAnalytics();
        // Service se category (Facilities/Courses) ka data mangwa rahe hain.
        List<AnalyticsDTO> categoryData = feedbackService.getCategoryAnalytics();

        // Dono data ko ek JSON object mein daal kar bhej rahe hain.
        // Frontend ko { "statusData": [...], "categoryData": [...] } aisa data milega.
        return ResponseEntity.ok(Map.of(
            "statusData", statusData,
            "categoryData", categoryData
        ));
    }

    // --- YEH MODULE 5 KE NAYE ENDPOINTS HAIN ---

    /**
     * GET endpoint to fetch a single, detailed feedback item by its ID.
     * (Yeh GET API endpoint hai ek single feedback item ke details laane ke liye)
     * URL: GET /api/admin/feedback/{id} (jaise /api/admin/feedback/12345)
     */
    @GetMapping("/feedback/{id}")
    // @PathVariable String id -- yeh line URL se 'id' (jaise 12345) nikaal kar 'id' variable mein daal deti hai.
    public ResponseEntity<FeedbackAdminViewDTO> getFeedbackById(@PathVariable String id) {
        // Service se bol rahe hain ki "sirf is 'id' wala feedback la do."
        FeedbackAdminViewDTO feedback = feedbackService.getFeedbackByIdForAdmin(id);
        // Single feedback ko JSON mein bhej rahe hain.
        return ResponseEntity.ok(feedback);
    }

    /**
     * PUT endpoint to update the status of a feedback item.
     * (Yeh PUT API endpoint hai status update karne ke liye (e.g., "open" se "resolved"))
     * URL: PUT /api/admin/feedback/{id}/status
     */
    @PutMapping("/feedback/{id}/status")
    public ResponseEntity<Feedback> updateFeedbackStatus(
            @PathVariable String id, // URL se feedback ID nikaal rahe hain
            // @Valid -- yeh validation rules check karta hai (DTO file se)
            // @RequestBody StatusDTO -- yeh line frontend se bheje gaye JSON ko StatusDTO object mein badal deti hai.
            @Valid @RequestBody StatusDTO statusDTO) {
        
        // Service ko bol rahe hain ki "is ID wale feedback ka status update kar do."
        Feedback updatedFeedback = feedbackService.updateFeedbackStatus(id, statusDTO.getStatus());
        // Updated feedback ko wapas frontend pe bhej rahe hain.
        return ResponseEntity.ok(updatedFeedback);
    }

    /**
     * POST endpoint for an admin to post a reply to a feedback thread.
     * (Yeh POST API endpoint hai naya reply/comment post karne ke liye)
     * URL: POST /api/admin/feedback/{id}/reply
     */
    @PostMapping("/feedback/{id}/reply")
    public ResponseEntity<Feedback> postReply(
            @PathVariable String id, // URL se feedback ID
            @Valid @RequestBody ReplyDTO replyDTO, // Frontend se reply ka text (JSON)
            // Authentication object -- Spring Security is object mein logged-in user ki details (token se) daal deta hai.
            Authentication authentication) {
        
        // Yeh line 'authentication' object se admin ka email nikaal rahi hai.
        String adminEmail = authentication.getName();
        
        // **YEH LINE ADMIN ID FIND KAREGI** (jaisa aapne pucha tha)
        // Hum email ka use karke database se admin ka poora User object nikaal rahe hain.
        User admin = userRepository.findByEmail(adminEmail)
                // Agar admin nahi mila, toh error throw karega.
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + adminEmail));
        
        // Service ko bol rahe hain ki "is feedback (id) par yeh reply (replyDTO) aur admin ka ID (admin.getId()) add kar do."
        Feedback updatedFeedback = feedbackService.postReplyToFeedback(id, replyDTO, admin.getId());
        // Updated feedback (naye reply ke sath) ko wapas bhej rahe hain.
        return ResponseEntity.ok(updatedFeedback);
    }
}