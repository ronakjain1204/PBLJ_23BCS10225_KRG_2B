import axios from "axios";
import AuthService from "./AuthService"; // We need this to get the authHeader

// Base URL for our admin controller
const API_URL = "http://localhost:8080/api/admin";

/**
 * Helper function to get the auth token header.
 * All admin actions are protected.
 */
const authHeader = () => {
  const currentUser = AuthService.getCurrentUser();
  if (currentUser && currentUser.token) {
    return { Authorization: "Bearer " + currentUser.token };
  } else {
    return {};
  }
};

/**
 * Fetches all feedback for the admin dashboard. (Module 4)
 */
const getAllFeedback = () => {
  return axios.get(API_URL + "/feedback", { headers: authHeader() });
};

/**
 * Fetches the analytics data (status and category counts). (Module 4)
 */
const getAnalytics = () => {
  return axios.get(API_URL + "/analytics", { headers: authHeader() });
};

// --- NEW FUNCTIONS FOR MODULE 5 ---

/**
 * Fetches a single, detailed feedback item by its ID.
 */
const getFeedbackById = (id) => {
  return axios.get(API_URL + `/feedback/${id}`, { headers: authHeader() });
};

/**
 * Updates the status of a feedback item.
 * @param {string} id - The ID of the feedback
 * @param {string} status - The new status (e.g., "in_progress")
 */
const updateFeedbackStatus = (id, status) => {
  // The backend expects a StatusDTO object: { "status": "new_status" }
  return axios.put(
    API_URL + `/feedback/${id}/status`,
    { status },
    { headers: authHeader() }
  );
};

/**
 * Posts a reply to a feedback thread.
 * @param {string} id - The ID of the feedback
 * @param {string} content - The text content of the reply
 */
const postReply = (id, content) => {
  // The backend expects a ReplyDTO object: { "content": "reply_text" }
  return axios.post(
    API_URL + `/feedback/${id}/reply`,
    { content },
    { headers: authHeader() }
  );
};

const AdminService = {
  getAllFeedback,
  getAnalytics,
  getFeedbackById,      // <-- ADDED
  updateFeedbackStatus, // <-- ADDED
  postReply,            // <-- ADDED
};

export default AdminService;