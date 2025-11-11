import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import AdminService from "../services/AdminService";
import "./AdminFeedbackDetail.css"; // We will create this CSS file next

const AdminFeedbackDetail = () => {
  const { id } = useParams(); // Gets the feedback ID from the URL
  const navigate = useNavigate();

  // State for the feedback data
  const [feedbackData, setFeedbackData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State for the admin's reply
  const [replyContent, setReplyContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Helper function to format dates
  const formatDate = (dateString) => {
    const options = {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // Function to fetch the feedback data from the API
  const fetchFeedback = async () => {
    try {
      setLoading(true);
      const res = await AdminService.getFeedbackById(id);
      setFeedbackData(res.data);
      setError("");
    } catch (err) {
      setError("Failed to load feedback details. It may not exist.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Fetch data when the component loads (or when 'id' changes)
  useEffect(() => {
    fetchFeedback();
  }, [id]);

  // Handler for changing the status
  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    try {
      // 1. Call the API to update the status
      const updatedFeedback = await AdminService.updateFeedbackStatus(id, newStatus);
      
      // 2. Update the state locally to show the change instantly
      setFeedbackData((prevData) => ({
        ...prevData,
        feedback: updatedFeedback.data, // The API returns the updated feedback object
      }));
    } catch (err) {
      setError("Failed to update status.");
      console.error(err);
    }
  };

  // Handler for submitting a new reply
  const handleReplySubmit = async (e) => {
    e.preventDefault();
    if (!replyContent.trim()) {
      return; // Don't submit empty replies
    }
    
    setIsSubmitting(true);
    setError("");

    try {
      // 1. Call the API to post the reply
      const updatedFeedback = await AdminService.postReply(id, replyContent);
      
      // 2. Update the state with the new comment and status
      setFeedbackData((prevData) => ({
        ...prevData,
        feedback: updatedFeedback.data, // The API returns the updated feedback
      }));
      
      // 3. Clear the reply box
      setReplyContent("");
    } catch (err) {
      setError("Failed to post reply. Please try again.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- Render Logic ---

  if (loading) {
    return <div className="detail-loading">Loading feedback details...</div>;
  }

  if (error) {
    return (
      <div className="detail-error">
        <p>Error: {error}</p>
        <Link to="/admin/dashboard" className="back-link">&larr; Back to Dashboard</Link>
      </div>
    );
  }

  if (!feedbackData) {
    return null; // Should be covered by loading/error, but good practice
  }

  // Destructure for easier access
  const { feedback, studentName, studentEmail } = feedbackData;

  // Helper to get status tag color
  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case "open": return "#007bff";
      case "in_progress": return "#ffc107";
      case "resolved": return "#28a745";
      default: return "#6c757d";
    }
  };

  return (
    <div className="feedback-detail-container">
      <nav className="detail-nav">
        <Link to="/admin/dashboard" className="back-link">&larr; Back to Dashboard</Link>
      </nav>

      <div className="detail-layout">
        
        {/* --- Left Column: Feedback Details --- */}
        <div className="info-panel">
          <h3>Feedback Details</h3>
          <div className="detail-card">
            <div className="detail-header">
              <span 
                className="detail-status" 
                style={{ backgroundColor: getStatusColor(feedback.status) }}
              >
                {feedback.status}
              </span>
              <span className="detail-category">{feedback.category}</span>
            </div>
            
            <div className="detail-stars">{"★".repeat(feedback.rating)}</div>
            
            <p className="detail-content">{feedback.content}</p>
            
            <div className="detail-meta">
              <strong>Submitted:</strong> {formatDate(feedback.createdAt)}
            </div>
            <div className="detail-meta">
              <strong>Student:</strong> {studentName} {studentEmail && `(${studentEmail})`}
            </div>
          </div>
        </div>

        {/* --- Right Column: Admin Actions --- */}
        <div className="action-panel">
          <h3>Admin Actions</h3>

          {/* Change Status Form */}
          <div className="action-card">
            <h4>Update Status</h4>
            <select 
              value={feedback.status} 
              onChange={handleStatusChange} 
              className="status-select"
            >
              <option value="open">Open</option>
              <option value="in_progress">In Progress</option>
              <option value="resolved">Resolved</option>
            </select>
          </div>

          {/* Post Reply Form */}
          <div className="action-card">
            <h4>Post a Reply</h4>
            <form onSubmit={handleReplySubmit}>
              <textarea
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                placeholder="Write your reply to the student or add internal notes..."
                rows="5"
                required
              />
              <button type="submit" disabled={isSubmitting} className="reply-button">
                {isSubmitting ? "Posting..." : "Post Reply"}
              </button>
            </form>
          </div>
        </div>

      </div>

      {/* --- Bottom Section: Conversation Thread --- */}
      <div className="thread-section">
        <h3>Conversation Thread</h3>
        <div className="thread-container">
          {feedback.thread.length === 0 ? (
            <p>No replies yet.</p>
          ) : (
            feedback.thread.map((comment, index) => (
              <div key={index} className="comment-bubble admin-comment">
                <p className="comment-content">{comment.content}</p>
                <div className="comment-meta">
                  <strong>Admin</strong>
                  <span>{formatDate(comment.timestamp)}</span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminFeedbackDetail;