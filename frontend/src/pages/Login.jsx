import React, { useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import { FaUser, FaEye, FaEyeSlash } from "react-icons/fa";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("Student");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!username.trim() || !password.trim()) {
      setError("Please enter username and password");
      return;
    }

    setLoading(true);
    try {
      await login(username, password, role.toUpperCase());
      navigate("/");
    } catch (err) {
      setError(
        err.response?.data?.error || "Invalid credentials. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-card">
          <h2 className="login-title">LOGIN</h2>
          <form className="login-form" onSubmit={handleSubmit}>
            {error && <div className="login-error">{error}</div>}
            <div className="input-group">
              <FaUser className="input-icon" />
              <input
                type="text"
                placeholder="Username / Email"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div className="input-group">
              <span
                className="input-icon password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <div className="role-select">
              <label>SELECT YOUR ROLE</label>
              <select value={role} onChange={(e) => setRole(e.target.value)}>
                <option>Student</option>
                <option>Admin</option>
                <option>Teacher</option>
              </select>
            </div>
            <button type="submit" className="login-button" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>
        </div>
        <div className="login-illustration">
          <div className="login-branding">
            <h1>
              <span className="brand-edu">EDU</span>
              <span className="brand-log">LOG</span>
            </h1>
            <p className="brand-tagline">
              STUDENT PERFORMANCE & ATTENDANCE TRACKER
            </p>
          </div>
          <div className="illustration-placeholder">
            <svg viewBox="0 0 400 300" className="illustration-svg">
              <defs>
                <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop
                    offset="0%"
                    style={{ stopColor: "#ff9a20", stopOpacity: 0.8 }}
                  />
                  <stop
                    offset="100%"
                    style={{ stopColor: "#ff6b00", stopOpacity: 0.6 }}
                  />
                </linearGradient>
              </defs>
              {/* Graduation cap */}
              <polygon points="200,80 280,120 200,160 120,120" fill="#1a365d" />
              <rect
                x="185"
                y="60"
                width="30"
                height="20"
                fill="#1a365d"
                rx="2"
              />
              <line
                x1="280"
                y1="120"
                x2="280"
                y2="170"
                stroke="#ff9a20"
                strokeWidth="3"
              />
              <circle cx="280" cy="175" r="6" fill="#ff9a20" />
              {/* Open book */}
              <path
                d="M130,180 Q200,160 200,200 Q200,160 270,180 L270,260 Q200,240 200,280 Q200,240 130,260 Z"
                fill="#f7f0e3"
                stroke="#ddd"
                strokeWidth="1"
              />
              <line
                x1="200"
                y1="200"
                x2="200"
                y2="280"
                stroke="#ddd"
                strokeWidth="1"
              />
              {/* Book lines */}
              <line
                x1="145"
                y1="210"
                x2="190"
                y2="200"
                stroke="#ccc"
                strokeWidth="1"
              />
              <line
                x1="145"
                y1="225"
                x2="190"
                y2="215"
                stroke="#ccc"
                strokeWidth="1"
              />
              <line
                x1="145"
                y1="240"
                x2="190"
                y2="230"
                stroke="#ccc"
                strokeWidth="1"
              />
              <line
                x1="210"
                y1="200"
                x2="255"
                y2="210"
                stroke="#ccc"
                strokeWidth="1"
              />
              <line
                x1="210"
                y1="215"
                x2="255"
                y2="225"
                stroke="#ccc"
                strokeWidth="1"
              />
              <line
                x1="210"
                y1="230"
                x2="255"
                y2="240"
                stroke="#ccc"
                strokeWidth="1"
              />
              {/* Pencil */}
              <rect
                x="290"
                y="140"
                width="12"
                height="100"
                rx="2"
                fill="#ff9a20"
                transform="rotate(15,296,190)"
              />
              <polygon
                points="290,240 296,260 302,240"
                fill="#f4c67a"
                transform="rotate(15,296,250)"
              />
              {/* Magnifying glass */}
              <circle
                cx="100"
                cy="220"
                r="25"
                fill="none"
                stroke="#4a90d9"
                strokeWidth="4"
              />
              <line
                x1="82"
                y1="238"
                x2="65"
                y2="260"
                stroke="#4a90d9"
                strokeWidth="5"
                strokeLinecap="round"
              />
              {/* People */}
              <circle cx="80" cy="140" r="12" fill="#e8553a" />
              <rect
                x="70"
                y="155"
                width="20"
                height="30"
                rx="5"
                fill="#e8553a"
              />
              <circle cx="320" cy="100" r="12" fill="#4a90d9" />
              <rect
                x="310"
                y="115"
                width="20"
                height="25"
                rx="5"
                fill="#4a90d9"
              />
              <circle cx="200" cy="130" r="10" fill="#e8553a" />
              <rect
                x="192"
                y="143"
                width="16"
                height="20"
                rx="4"
                fill="#e8553a"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
}
