import React, { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check for existing token on mount
    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");
    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
      } catch {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
      }
    }
    setLoading(false);
  }, []);

  const login = async (username, password, role) => {
    const response = await axios.post("/api/auth/login", {
      username,
      password,
      role,
    });
    const data = response.data;

    const userObj = {
      id: data.userId,
      name: data.name,
      role: data.role,
      username: data.username,
      studentId: data.studentId,
      token: data.token,
    };

    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(userObj));
    setUser(userObj);
    return userObj;
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  const isAdmin = user?.role === "ADMIN";
  const isTeacher = user?.role === "TEACHER";
  const isStudent = user?.role === "STUDENT";

  return (
    <AuthContext.Provider
      value={{ user, login, logout, loading, isAdmin, isTeacher, isStudent }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
