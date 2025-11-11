import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * A component to protect routes based on authentication and role.
 *
 * @param {object} props
 * @param {React.ReactNode} props.children - The component to render if authorized.
 * @param {string} [props.role] - The specific role required to access this route.
 */
const ProtectedRoute = ({ children, role }) => {
  const { currentUser } = useAuth(); // Get user from our global state

  // 1. Check if user is logged in
  if (!currentUser) {
    // If not, redirect them to the /login page
    return <Navigate to="/login" />;
  }

  // 2. Check if a specific role is required (e.g., role="ROLE_ADMIN")
  if (role && currentUser.role !== role) {
    // If user has the wrong role, redirect them.
    // We'll send admins to their dashboard and students to theirs.
    if (currentUser.role === "ROLE_ADMIN") {
      return <Navigate to="/admin/dashboard" />;
    } else {
      return <Navigate to="/dashboard" />;
    }
  }

  // 3. If they are logged in AND have the correct role (or no role is required),
  // show the page they were trying to access (the "children").
  return children;
};

export default ProtectedRoute;