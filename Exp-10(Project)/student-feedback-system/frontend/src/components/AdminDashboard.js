/**
 * AdminDashboard.js
 *
 * This is the main "command center" for the admin. It displays analytics
 * charts and a filterable, sortable table of all feedback from all users.
 * This file integrates Module 4 (viewing all feedback) and Module 5
 * (linking to the detail/management page).
 */

// --- 1. React and Library Imports ---
import React, { useState, useEffect, useMemo } from "react";
import { useAuth } from "../context/AuthContext"; // To get current user and logout
import { useNavigate } from "react-router-dom"; // To navigate programmatically (e.g., logout, view details)
import AdminService from "../services/AdminService"; // API calls for admin
import "./AdminDashboard.css"; // Styles for this component

// --- 2. Chart.js Imports ---
// We explicitly import the components we need from Chart.js and react-chartjs-2
import { Pie, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
} from "chart.js";

// --- 3. Chart.js Registration ---
// We must register the "elements" we're going to use, or the charts won't work.
ChartJS.register(
  ArcElement, // For Pie charts
  Tooltip,    // For hover tooltips
  Legend,     // For the color legend
  CategoryScale, // For the X-axis (labels) on bar charts
  LinearScale,   // For the Y-axis (numbers) on bar charts
  BarElement,    // For the actual bars in the bar chart
  Title        // For chart titles
);

// --- 4. The Main Dashboard Component ---
const AdminDashboard = () => {
  // --- 5. State Management ---
  // Get user info and logout function from our global context
  const { currentUser, logout } = useAuth();
  // Get the navigate function from React Router
  const navigate = useNavigate();

  // State to hold the master list of all feedback
  const [feedbackList, setFeedbackList] = useState([]);
  // State to hold the analytics data (status counts, category counts)
  const [analytics, setAnalytics] = useState(null);
  // State to show a loading message while we fetch data
  const [loading, setLoading] = useState(true);
  // State to hold any errors from the API
  const [error, setError] = useState("");

  // State to manage the user's filter selections
  const [filterStatus, setFilterStatus] = useState("all"); // Default: show all
  const [filterCategory, setFilterCategory] = useState("all"); // Default: show all

  // --- 6. Data Fetching Effect ---
  // This useEffect hook runs once when the component first loads
  useEffect(() => {
    // We define an async function to fetch our data
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(""); // Clear previous errors

        // Use Promise.all to run both API calls in parallel for speed
        const [feedbackRes, analyticsRes] = await Promise.all([
          AdminService.getAllFeedback(),
          AdminService.getAnalytics(),
        ]);

        // Set the data into our state
        setFeedbackList(feedbackRes.data);
        setAnalytics(analyticsRes.data);
      } catch (err) {
        // If either API call fails, set an error message
        setError("Failed to fetch admin data.");
        console.error("Error fetching admin data:", err);
      } finally {
        // Whether it succeeded or failed, we're done loading
        setLoading(false);
      }
    };

    fetchData(); // Call the function
  }, []); // The empty array `[]` means this effect only runs ONCE

  // --- 7. Event Handlers ---

  // Handle the logout button click
  const handleLogout = () => {
    logout(); // Clear user from context and localStorage
    navigate("/login"); // Send user to login page
  };

  // Handle the "View" button click (Module 5)
  // This function takes the ID of the feedback and navigates to the detail page
  const handleViewClick = (id) => {
    navigate(`/admin/feedback/${id}`);
  };

  // --- 8. Data Formatting for Charts ---
  // We use `useMemo` to re-calculate the chart data ONLY when `analytics` changes.
  // This prevents re-calculating on every single re-render (e.g., when typing in a filter)

  // Data for the Pie Chart (Feedback by Status)
  const pieChartData = useMemo(() => {
    if (!analytics) return null; // Don't do anything if data isn't loaded
    return {
      labels: analytics.statusData.map((d) => d._id), // e.g., ["open", "resolved"]
      datasets: [
        {
          label: "Feedback by Status",
          data: analytics.statusData.map((d) => d.count), // e.g., [5, 2]
          backgroundColor: ["#007bff", "#28a745", "#ffc107"], // Blue, Green, Yellow
        },
      ],
    };
  }, [analytics]); // Dependency: only run when `analytics` changes

  // Data for the Bar Chart (Feedback by Category)
  const barChartData = useMemo(() => {
    if (!analytics) return null;
    return {
      labels: analytics.categoryData.map((d) => d._id), // e.g., ["Facilities", "Courses"]
      datasets: [
        {
          label: "Feedback by Category",
          data: analytics.categoryData.map((d) => d.count), // e.g., [3, 4]
          backgroundColor: "#17a2b8", // Teal
        },
      ],
    };
  }, [analytics]); // Dependency: only run when `analytics` changes

  // --- 9. Filtering Logic for Table ---
  // We use `useMemo` again to create a new *filtered* list.
  // This runs only when the master list or the filters change.
  const filteredFeedback = useMemo(() => {
    return feedbackList
      .filter((item) => {
        // If filter is "all", this is true. Otherwise, check for a match.
        return filterStatus === "all" || item.feedback.status === filterStatus;
      })
      .filter((item) => {
        // Chain another filter for category
        return filterCategory === "all" || item.feedback.category === filterCategory;
      });
  }, [feedbackList, filterStatus, filterCategory]); // Dependencies

  // --- 10. Helper Functions ---
  // Simple function to make the date string look nice
  const formatDate = (dateString) => {
    const options = {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // --- 11. Render Logic ---
  // Show a loading message while fetching
  if (loading) {
    return <div className="admin-dashboard-loading">Loading Admin Dashboard...</div>;
  }

  // Show an error message if fetching failed
  if (error) {
    return <div className="admin-dashboard-error">Error: {error}</div>;
  }

  // If loading is false and no error, render the dashboard
  return (
    <div className="admin-dashboard">
      {/* --- HEADER --- */}
      <header className="admin-header">
        <h1>Admin Dashboard</h1>
        <div className="admin-user-info">
          <span>{currentUser.email}</span>
          <button onClick={handleLogout} className="logout-button">
            Logout
          </button>
        </div>
      </header>

      {/* --- ANALYTICS CHARTS SECTION --- */}
      <section className="admin-section charts-section">
        <h2>Analytics</h2>
        <div className="charts-container">
          {/* Only render the chart if the data is ready */}
          {pieChartData && (
            <div className="chart-wrapper">
              <h3>Feedback by Status</h3>
              <Pie data={pieChartData} />
            </div>
          )}
          {barChartData && (
            <div className="chart-wrapper">
              <h3>Feedback by Category</h3>
              <Bar data={barChartData} options={{ responsive: true }} />
            </div>
          )}
        </div>
      </section>

      {/* --- FEEDBACK TABLE SECTION --- */}
      <section className="admin-section table-section">
        <h2>All Feedback Submissions</h2>

        {/* --- FILTER CONTROLS --- */}
        <div className="filter-controls">
          <div className="filter-group">
            <label htmlFor="status-filter">Filter by Status:</label>
            <select
              id="status-filter"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
            >
              <option value="all">All Statuses</option>
              <option value="open">Open</option>
              <option value="in_progress">In Progress</option>
              <option value="resolved">Resolved</option>
            </select>
          </div>
          <div className="filter-group">
            <label htmlFor="category-filter">Filter by Category:</label>
            <select
              id="category-filter"
              value={filterCategory}
              onChange={(e) => setFilterCategory(e.target.value)}
            >
              <option value="all">All Categories</option>
              <option value="Facilities">Facilities</option>
              <option value="Courses">Courses</option>
              <option value="Campus Life">Campus Life</option>
              <option value="Faculty">Faculty</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>

        {/* --- FEEDBACK TABLE --- */}
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Status</th>
                <th>Category</th>
                <th>Student</th>
                <th>Content</th>
                <th>Rating</th>
                <th>Submitted</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {/* We map over the `filteredFeedback` list, not the master list */}
              {filteredFeedback.length > 0 ? (
                filteredFeedback.map(({ feedback, studentName, studentEmail }) => (
                  <tr key={feedback.id}>
                    {/* Show a shortened ID for cleanliness */}
                    <td title={feedback.id}>{feedback.id.substring(0, 8)}...</td>
                    <td>
                      {/* A colorful tag for the status */}
                      <span className={`status-tag status-${feedback.status.toLowerCase()}`}>
                        {feedback.status}
                      </span>
                    </td>
                    <td>{feedback.category}</td>
                    {/* Show student name, and email on hover */}
                    <td title={studentEmail}>
                      {studentName}
                    </td>
                    <td className="content-cell">{feedback.content}</td>
                    <td>{"★".repeat(feedback.rating)}</td>
                    <td>{formatDate(feedback.createdAt)}</td>
                    <td>
                      {/* *** THIS IS THE MODULE 5 FIX ***
                        The button now calls `handleViewClick` with the feedback's ID 
                      */}
                      <button
                        onClick={() => handleViewClick(feedback.id)}
                        className="action-button"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                // Show this if the table is empty (or filters find nothing)
                <tr>
                  <td colSpan="8">No feedback found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
};

export default AdminDashboard;