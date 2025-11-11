import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import FeedbackService from "../services/FeedbackService"; // Import our new service
import "./FeedbackForm.css"; // Import the CSS

const FeedbackForm = () => {
  // State for each form field
  const [content, setContent] = useState("");
  const [rating, setRating] = useState(0);
  const [category, setCategory] = useState("Facilities"); // Default category
  const [isAnonymous, setIsAnonymous] = useState(false);

  // State for messages
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // Stop page refresh
    setError("");
    setSuccess("");
    setLoading(true);

    // 1. Check for basic validation
    if (rating === 0) {
      setError("Please select a star rating.");
      setLoading(false);
      return;
    }
    if (!content.trim()) {
      setError("Please write your feedback.");
      setLoading(false);
      return;
    }

    // 2. Create the data object (must match the DTO in the backend)
    const feedbackData = {
      content,
      rating,
      category,
      isAnonymous,
    };

    try {
      // 3. Call the service to submit the data
      await FeedbackService.submitFeedback(feedbackData);

      // 4. Handle success
      setSuccess("Feedback submitted successfully!");
      setLoading(false);
      // Reset the form
      setContent("");
      setRating(0);
      setCategory("Facilities");
      setIsAnonymous(false);

      // Optional: Redirect back to dashboard after 2 seconds
      setTimeout(() => {
        navigate("/dashboard");
      }, 2000);
    } catch (err) {
      // 5. Handle failure
      setError("Failed to submit feedback. Please try again.");
      setLoading(false);
      console.error("Submission error:", err);
    }
  };

  return (
    <div className="feedback-form-container">
      <form onSubmit={handleSubmit}>
        <h2>Submit Your Feedback</h2>

        {/* Success Message */}
        {success && <div className="message success">{success}</div>}

        {/* Error Message */}
        {error && <div className="message error">{error}</div>}

        {/* Content Field */}
        <div className="form-group">
          <label htmlFor="content">Your Feedback</label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="What's on your mind?"
            required
          />
        </div>

        {/* Rating Field */}
        <div className="form-group">
          <label>Rating</label>
          <div className="star-rating">
            {[5, 4, 3, 2, 1].map((star) => (
              <React.Fragment key={star}>
                <input
                  type="radio"
                  id={`star-${star}`}
                  name="rating"
                  value={star}
                  checked={rating === star}
                  onChange={() => setRating(star)}
                />
                <label htmlFor={`star-${star}`}>&#9733;</label>
              </React.Fragment>
            ))}
          </div>
        </div>

        {/* Category Field */}
        <div className="form-group">
          <label htmlFor="category">Category</label>
          <select
            id="category"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="Facilities">Facilities</option>
            <option value="Courses">Courses</option>
            <option value="Campus Life">Campus Life</option>
            <option value="Faculty">Faculty</option>
            <option value="Other">Other</option>
          </select>
        </div>

        {/* Anonymous Checkbox */}
        <div className="form-group checkbox-group">
          <input
            type="checkbox"
            id="anonymous"
            checked={isAnonymous}
            onChange={(e) => setIsAnonymous(e.target.checked)}
          />
          <label htmlFor="anonymous">Submit Anonymously</label>
        </div>

        {/* Submit Button */}
        <div className="form-group">
          <button type="submit" disabled={loading}>
            {loading ? "Submitting..." : "Submit Feedback"}
          </button>
        </div>

        <Link to="/dashboard" className="back-link">
          &larr; Back to Dashboard
        </Link>
      </form>
    </div>
  );
};

export default FeedbackForm;