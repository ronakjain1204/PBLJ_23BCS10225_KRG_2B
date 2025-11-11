import React, { createContext, useState, useContext, useEffect } from "react";
import AuthService from "../services/AuthService";
import { useNavigate } from "react-router-dom";

// 1. Create the Context (the "box")
const AuthContext = createContext(null);

// 2. Create the Provider (the component that "fills the box")
export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true); // Add a loading state
  const navigate = useNavigate();

  // 3. Check for user in localStorage on app start (as planned)
  useEffect(() => {
    // This runs once when the app loads
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
    setLoading(false); // We're done checking, so stop loading
  }, []);

  // 4. Login function
  const login = async (email, password) => {
    try {
      // Call the service to login
      const userData = await AuthService.login(email, password);
      // Update our global state
      setCurrentUser(userData);
      // Send the user to their correct dashboard
      if (userData.role === "ROLE_ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/dashboard");
      }
    } catch (error) {
      console.error("Login failed:", error);
      // We'll throw the error so the Login component can display a message
      throw error;
    }
  };

  // 5. Logout function
  const logout = () => {
    // Call the service to logout (clear localStorage)
    AuthService.logout();
    // Clear our global state
    setCurrentUser(null);
    // Send the user back to the login page
    navigate("/login");
  };

  // 6. The value to be passed to all children components
  const value = {
    currentUser,
    login,
    logout,
  };

  // Don't render the app until we've checked localStorage
  if (loading) {
    return <div>Loading app...</div>; // Or a proper spinner component
  }

  // 7. Return the Provider, wrapping the app (children)
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// 8. Create the custom hook (the easy way to "use" the context)
export const useAuth = () => {
  return useContext(AuthContext);
};