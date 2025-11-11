import axios from "axios";
import AuthService from "./AuthService";

// Base URL for our feedback controller
const API_URL = "http://localhost:8080/api/feedback";

/**
 * Helper function to get the auth token header.
 * This is crucial for protected endpoints.
 */
const authHeader = () => {
  const currentUser = AuthService.getCurrentUser();
  if (currentUser && currentUser.token) {
    // Spring Boot expects the token as "Bearer <token>"
    return { Authorization: "Bearer " + currentUser.token };
  } else {
    // No user is logged in
    return {};
  }
};

/**
 * Submits a new feedback form to the backend.
 * @param {object} feedbackData - The data from the form (content, rating, category, etc.)
 */
const submitFeedback = (feedbackData) => {
  return axios.post(API_URL + "/submit", feedbackData, {
    headers: authHeader(), // Send the user's token
  });
};

/**
 * NEW FUNCTION FOR MODULE 3
 * Fetches all feedback for the currently logged-in student.
 */
const getMyFeedback = () => {
  return axios.get(API_URL + "/my-feedback", {
    headers: authHeader(), // Send the user's token!
  });
};

const FeedbackService = {
  submitFeedback,
  getMyFeedback, // <-- Add the new function to the export
};

export default FeedbackService;