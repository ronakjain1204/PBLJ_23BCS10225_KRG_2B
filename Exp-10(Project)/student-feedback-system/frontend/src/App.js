import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

// --- Import All Page Components ---

// Module 1: Auth
import Login from "./components/Login";
import Register from "./components/Register";
import ProtectedRoute from "./components/ProtectedRoute";

// Module 2 & 3: Student
import StudentDashboard from "./components/StudentDashboard";
import FeedbackForm from "./components/FeedbackForm";

// Module 4 & 5: Admin
import AdminDashboard from "./components/AdminDashboard";
import AdminFeedbackDetail from "./components/AdminFeedbackDetail"; // <-- Module 5

function App() {
  return (
    <Routes>
      {/* ---------------------------------- */}
      {/* --- PUBLIC ROUTES --- */}
      {/* ---------------------------------- */}
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* ---------------------------------- */}
      {/* --- STUDENT PROTECTED ROUTES --- */}
      {/* ---------------------------------- */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute role="ROLE_STUDENT">
            <StudentDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/submit-feedback"
        element={
          <ProtectedRoute role="ROLE_STUDENT">
            <FeedbackForm />
          </ProtectedRoute>
        }
      />

      {/* ---------------------------------- */}
      {/* --- ADMIN PROTECTED ROUTES --- */}
      {/* ---------------------------------- */}
      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute role="ROLE_ADMIN">
            <AdminDashboard />
          </ProtectedRoute>
        }
      />
      {/* This is the new route for Module 5.
          The ":id" is a URL parameter that will tell
          the component which feedback item to fetch. */}
      <Route
        path="/admin/feedback/:id"
        element={
          <ProtectedRoute role="ROLE_ADMIN">
            <AdminFeedbackDetail />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;