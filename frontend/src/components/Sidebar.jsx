import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import {
  FaTh,
  FaUserCheck,
  FaStar,
  FaChartBar,
  FaChartPie,
  FaSignOutAlt,
  FaFileExport,
} from "react-icons/fa";

export default function Sidebar() {
  const { user, logout, isStudent } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const adminTeacherLinks = [
    { to: "/", icon: <FaTh />, label: "Dashboard", end: true },
    { to: "/attendance", icon: <FaUserCheck />, label: "Mark Attendance" },
    { to: "/grades", icon: <FaStar />, label: "Add Grades" },
    { to: "/reports", icon: <FaChartBar />, label: "Grade Reports" },
    { to: "/analytics", icon: <FaChartPie />, label: "Analytics & Charts" },
  ];

  const studentLinks = [
    { to: "/", icon: <FaTh />, label: "Dashboard", end: true },
    { to: "/reports", icon: <FaChartBar />, label: "Grade Reports" },
    { to: "/analytics", icon: <FaChartPie />, label: "Analytics & Charts" },
  ];

  const links = isStudent ? studentLinks : adminTeacherLinks;

  return (
    <aside className="sidebar">
      <div className="brand">
        <span className="brand-edu">EDU</span>
        <span className="brand-log">LOG</span>
      </div>

      <nav className="sidebar-nav">
        <ul>
          {links.map((link) => (
            <li key={link.to + link.label}>
              <NavLink
                to={link.to}
                end={link.end}
                className={({ isActive }) =>
                  `sidebar-link ${isActive ? "active" : ""}`
                }
              >
                <span className="sidebar-icon">{link.icon}</span>
                <span className="sidebar-label">{link.label}</span>
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>

      <div className="sidebar-logout" onClick={handleLogout}>
        <FaSignOutAlt className="sidebar-icon" />
        <span>Logout</span>
      </div>
    </aside>
  );
}
