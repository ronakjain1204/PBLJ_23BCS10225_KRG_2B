import axios from "axios";

// This is the base URL of your Spring Boot backend.
// We configured this in Spring Boot's "SecurityConfig.java" (CORS)
const API_URL = "http://localhost:8080/api/auth/";

/**
 * Handles the registration request.
 * @param {string} name 
 * @param {string} email 
 * @param {string} password 
 * @returns {Promise} Axios response
 */
const register = (name, email, password) => {
  return axios.post(API_URL + "register", {
    name,
    email,
    password,
  });
};

/**
 * Handles the login request.
 * @param {string} email 
 * @param {string} password 
 * @returns {Promise} Axios response
 */
const login = (email, password) => {
  return axios
    .post(API_URL + "login", {
      email,
      password,
    })
    .then((response) => {
      // If the response contains a token, store it in localStorage.
      // This is exactly what you planned.
      if (response.data.token) {
        // We stringify the user object (token + role) to store it
        localStorage.setItem("user", JSON.stringify(response.data));
      }
      return response.data;
    });
};

/**
 * Logs the user out by removing the user item from localStorage.
 */
const logout = () => {
  localStorage.removeItem("user");
};

/**
 * A helper function to get the currently stored user data (token + role).
 */
const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

// We export all the functions as a single object named "AuthService"
const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
};

export default AuthService;