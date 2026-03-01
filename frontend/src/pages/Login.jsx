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
              <input
                type="text"
                placeholder="Username / Email"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
              <span className="input-icon">
                <FaUser />
              </span>
            </div>
            <div className="input-group">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <span
                className="input-icon"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
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
                    style={{ stopColor: "#00d4ff", stopOpacity: 0.8 }}
                  />
                  <stop
                    offset="100%"
                    style={{ stopColor: "#7c3aed", stopOpacity: 0.6 }}
                  />
                </linearGradient>
                <linearGradient id="grad2" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop
                    offset="0%"
                    style={{ stopColor: "#00d4ff", stopOpacity: 0.6 }}
                  />
                  <stop
                    offset="100%"
                    style={{ stopColor: "#7c3aed", stopOpacity: 0.4 }}
                  />
                </linearGradient>
              </defs>
              {/* Graduation cap */}
              <polygon
                points="200,80 280,120 200,160 120,120"
                fill="url(#grad2)"
                opacity="0.8"
              />
              <rect
                x="185"
                y="60"
                width="30"
                height="20"
                fill="url(#grad2)"
                rx="2"
                opacity="0.8"
              />
              <line
                x1="280"
                y1="120"
                x2="280"
                y2="170"
                stroke="#00d4ff"
                strokeWidth="3"
              />
              <circle cx="280" cy="175" r="6" fill="#00d4ff" />
              {/* Open book */}
              <path
                d="M130,180 Q200,160 200,200 Q200,160 270,180 L270,260 Q200,240 200,280 Q200,240 130,260 Z"
                fill="rgba(255,255,255,0.05)"
                stroke="rgba(255,255,255,0.15)"
                strokeWidth="1"
              />
              <line
                x1="200"
                y1="200"
                x2="200"
                y2="280"
                stroke="rgba(255,255,255,0.1)"
                strokeWidth="1"
              />
              {/* Book lines */}
              {[210, 225, 240].map((y) => (
                <line
                  key={`l${y}`}
                  x1="145"
                  y1={y}
                  x2="190"
                  y2={y - 10}
                  stroke="rgba(255,255,255,0.1)"
                  strokeWidth="1"
                />
              ))}
              {[200, 215, 230].map((y) => (
                <line
                  key={`r${y}`}
                  x1="210"
                  y1={y}
                  x2="255"
                  y2={y + 10}
                  stroke="rgba(255,255,255,0.1)"
                  strokeWidth="1"
                />
              ))}
              {/* Pencil */}
              <rect
                x="290"
                y="140"
                width="12"
                height="100"
                rx="2"
                fill="url(#grad1)"
                transform="rotate(15,296,190)"
                opacity="0.7"
              />
              <polygon
                points="290,240 296,260 302,240"
                fill="#00d4ff"
                transform="rotate(15,296,250)"
                opacity="0.5"
              />
              {/* Magnifying glass */}
              <circle
                cx="100"
                cy="220"
                r="25"
                fill="none"
                stroke="#7c3aed"
                strokeWidth="4"
                opacity="0.6"
              />
              <line
                x1="82"
                y1="238"
                x2="65"
                y2="260"
                stroke="#7c3aed"
                strokeWidth="5"
                strokeLinecap="round"
                opacity="0.6"
              />
              {/* People */}
              <circle cx="80" cy="140" r="12" fill="#00d4ff" opacity="0.5" />
              <rect
                x="70"
                y="155"
                width="20"
                height="30"
                rx="5"
                fill="#00d4ff"
                opacity="0.5"
              />
              <circle cx="320" cy="100" r="12" fill="#7c3aed" opacity="0.5" />
              <rect
                x="310"
                y="115"
                width="20"
                height="25"
                rx="5"
                fill="#7c3aed"
                opacity="0.5"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
}
