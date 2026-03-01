import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard";
import MarkAttendance from "./pages/MarkAttendance";
import AddGrades from "./pages/AddGrades";
import GradeReports from "./pages/GradeReports";
import Analytics from "./pages/Analytics";
import Login from "./pages/Login";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import { ApiProvider } from "./contexts/ApiContext";
import DareStart from "./pages/DareStart";

function PrivateRoutes() {
  const { user, loading, isStudent } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p>Loading...</p>
      </div>
    );
  }

  if (!user) return <Navigate to="/login" replace />;

  return (
    <div className="app-layout">
      <Sidebar />
      <main className="main-area">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/attendance" element={<MarkAttendance />} />
          <Route path="/grades" element={<AddGrades />} />
          <Route path="/reports" element={<GradeReports />} />
          <Route path="/analytics" element={<Analytics />} />
        </Routes>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <ApiProvider>
        <Router>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/dare" element={<DareStart />} />
            <Route path="/*" element={<PrivateRoutes />} />
          </Routes>
        </Router>
      </ApiProvider>
    </AuthProvider>
  );
}
