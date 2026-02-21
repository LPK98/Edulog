import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <div className="brand">
        EDU<span>LOG</span>
      </div>
      <div className="user-info">
        {user ? <div>{user.name}</div> : <div>Guest</div>}
      </div>
      <nav>
        <ul>
          <li>
            <NavLink to="/" end>
              Dashboard
            </NavLink>
          </li>
          <li>
            <NavLink to="/attendance">Mark Attendance</NavLink>
          </li>
          <li>
            <NavLink to="/grades">Add Grades</NavLink>
          </li>
          <li>
            <NavLink to="/reports">Grade Reports</NavLink>
          </li>
          <li>
            <NavLink to="/analytics">Analytics & Charts</NavLink>
          </li>
        </ul>
      </nav>
      <div
        className="logout"
        onClick={handleLogout}
        style={{ cursor: "pointer" }}
      >
        Logout
      </div>
    </aside>
  );
}
