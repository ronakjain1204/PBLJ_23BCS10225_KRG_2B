import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import FeedbackService from "../services/FeedbackService";
import FeedbackList from "./FeedbackList";
import "./StudentDashboard.css"; // 1. Import the new CSS file

const StudentDashboard = () => {
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();

  const [feedbackList, setFeedbackList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadFeedback = async () => {
      try {
        setLoading(true);
        setError("");
        const response = await FeedbackService.getMyFeedback();
        
        const sortedFeedback = response.data.sort((a, b) => 
          new Date(b.createdAt) - new Date(a.createdAt)
        );

        setFeedbackList(sortedFeedback);
      } catch (err) {
        setError("Failed to load feedback history.");
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    loadFeedback();
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!currentUser) {
    return <div className="feedback-loading">Loading...</div>; // Use CSS class
  }

  return (
    // 2. Use new CSS classes for layout
    <div className="student-dashboard">
      {/* --- Header Section --- */}
      <header className="student-header">
        <h2>Welcome, {currentUser.name}!</h2>
        
        <div className="student-user-info">
          <span><strong>Email:</strong> {currentUser.email}</span>
          <span><strong>Role:</strong> {currentUser.role}</span>
        </div>

        <div className="student-actions">
          <Link to="/submit-feedback" className="student-button">
            Submit New Feedback
          </Link>
          <button onClick={handleLogout} className="student-button logout-button">
            Logout
          </button>
        </div>
      </header>

      {/* --- Main Content Section --- */}
      <main className="feedback-section">
        <h3>My Feedback History</h3>
        
        {/* 3. Use classes for loading/error messages */}
        {loading && <p className="feedback-loading">Loading feedback...</p>}
        {error && <p className="feedback-error">{error}</p>}
        
        {/* The FeedbackList component will now be inside this styled container */}
        {!loading && !error && <FeedbackList feedbackItems={feedbackList} />}
      </main>
    </div>
  );
};

export default StudentDashboard;