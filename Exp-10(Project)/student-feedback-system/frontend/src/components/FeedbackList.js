import React from "react";
// import { Link } from "react-router-dom"; // We don't need this yet
import "./FeedbackList.css"; // We will update this file next

const FeedbackList = ({ feedbackItems }) => {
  if (feedbackItems.length === 0) {
    return <p>You have not submitted any feedback yet.</p>;
  }

  // Helper function to format the date
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

  // Helper function to get a color for the status
  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case "open":
        return "#007bff";
      case "in_progress":
        return "#ffc107";
      case "resolved":
        return "#28a745";
      default:
        return "#6c757d";
    }
  };

  return (
    <div className="feedback-list-container">
      {feedbackItems.map((item) => (
        <div key={item.id} className="feedback-item-card">
          {/* --- Top Section (Status, Category) --- */}
          <div className="card-header">
            <span
              className="feedback-status"
              style={{ backgroundColor: getStatusColor(item.status) }}
            >
              {item.status}
            </span>
            <span className="feedback-category">{item.category}</span>
          </div>

          {/* --- Original Feedback Content --- */}
          <p className="feedback-content">"{item.content}"</p>
          
          <div className="card-footer">
            <span className="feedback-date">
              Submitted: {formatDate(item.createdAt)}
            </span>
            <span className="feedback-rating">{"★".repeat(item.rating)}</span>
          </div>

          {/* --- NEW: Conversation Thread Section --- */}
          {item.thread && item.thread.length > 0 && (
            <div className="thread-preview-container">
              <h4 className="thread-preview-title">Conversation Thread</h4>
              {item.thread.map((comment, index) => (
                <div key={index} className="comment-bubble admin-comment">
                  <p className="comment-content">{comment.content}</p>
                  <div className="comment-meta">
                    <strong>Admin</strong>
                    <span>{formatDate(comment.timestamp)}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default FeedbackList;