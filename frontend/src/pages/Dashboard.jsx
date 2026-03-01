import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useApi } from "../contexts/ApiContext";
import { FaBell, FaDownload } from "react-icons/fa";

function Calendar() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  const monthName = now
    .toLocaleString("default", { month: "long" })
    .toUpperCase();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const firstDay = new Date(year, month, 1).getDay();
  const today = now.getDate();

  const days = [];
  for (let i = 0; i < firstDay; i++) days.push(null);
  for (let i = 1; i <= daysInMonth; i++) days.push(i);

  const weeks = [];
  for (let i = 0; i < days.length; i += 7) {
    weeks.push(days.slice(i, i + 7));
  }

  return (
    <div className="calendar-card">
      <h3 className="calendar-month">{monthName}</h3>
      <table className="calendar-table">
        <thead>
          <tr>
            {["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"].map((d) => (
              <th key={d} className={d === "SUN" ? "sun" : ""}>
                {d}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {weeks.map((week, wi) => (
            <tr key={wi}>
              {week.map((day, di) => (
                <td
                  key={di}
                  className={`${day === today ? "today" : ""} ${di === 0 ? "sun" : ""}`}
                >
                  {day || ""}
                </td>
              ))}
              {week.length < 7 &&
                Array(7 - week.length)
                  .fill(null)
                  .map((_, i) => <td key={`e${i}`}></td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AdminDashboard({ stats, api }) {
  const handleExport = async () => {
    // Export all student data as a summary
    try {
      const res = await api.get("/students");
      const students = res.data;
      let csv = "Student ID,Name,Batch,Group,GPA\n";
      students.forEach((s) => {
        csv += `${s.studentId},${s.name},${s.batch},${s.groupName},${s.gpa}\n`;
      });
      const blob = new Blob([csv], { type: "text/csv" });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "students_report.csv";
      document.body.appendChild(link);
      link.click();
      link.remove();
      URL.revokeObjectURL(url);
    } catch {
      alert("Failed to export");
    }
  };

  return (
    <div className="dashboard-content">
      <div className="dashboard-top">
        <Calendar />
        <div className="profile-card-dash">
          <div className="profile-avatar-large" />
          <p className="profile-role">{stats.userRole}</p>
          <p className="profile-title">{stats.userName}</p>
        </div>
      </div>

      <div className="dashboard-actions">
        <div className="action-group">
          <FaDownload className="action-icon" />
          <button className="btn-action btn-dark" onClick={handleExport}>
            EXPORT REPORT
          </button>
        </div>

        <div className="stats-card">
          <h3>Quick Stats</h3>
          <div className="stat-row">
            <span>Total Students</span>
            <strong>{stats.totalStudents}</strong>
          </div>
          <div className="stat-row">
            <span>Teachers Count</span>
            <strong>{stats.totalTeachers}</strong>
          </div>
          <div className="stat-row">
            <span>Average Attendance</span>
            <div className="progress-bar-mini">
              <div
                className="progress-fill"
                style={{ width: `${stats.averageAttendance}%` }}
              />
            </div>
          </div>
        </div>

        <div className="stats-card">
          <h3>Personal Info</h3>
          <p>{stats.userName}</p>
          <p>{stats.userPhone || "-"}</p>
          <p>{stats.userAddress || "-"}</p>
        </div>
      </div>
    </div>
  );
}

function StudentDashboard({ stats, api }) {
  const handleDownloadPdf = async () => {
    if (!stats.studentInfo?.studentId) return;
    // Find the numeric student ID by searching students
    try {
      const res = await api.get("/students");
      const student = res.data.find(
        (s) => s.studentId === stats.studentInfo.studentId,
      );
      if (!student) return;
      const pdfRes = await api.get(`/reports/student/${student.id}/pdf`, {
        responseType: "blob",
      });
      const url = URL.createObjectURL(new Blob([pdfRes.data]));
      const link = document.createElement("a");
      link.href = url;
      link.download = `report_${student.studentId}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      URL.revokeObjectURL(url);
    } catch {
      alert("Failed to download report");
    }
  };

  return (
    <div className="dashboard-content">
      <div className="dashboard-top">
        <div className="search-bar-wrapper">
          <input type="text" placeholder="Search..." className="search-input" />
        </div>
        <div className="profile-card-dash">
          <div className="profile-avatar-large" />
          <p className="profile-role">Student</p>
          <p className="profile-title">{stats.userName}</p>
        </div>
      </div>

      <div className="dashboard-actions">
        <div className="student-report-card">
          <h3>MY REPORT</h3>
          <FaDownload className="action-icon" />
          <button className="btn-action btn-dark" onClick={handleDownloadPdf}>
            Download PDF
          </button>
          <div className="performance-box">
            <h4>Performance</h4>
            <p className="gpa-display">
              GPA : {stats.gpa?.toFixed(3) || "0.000"}
            </p>
          </div>
        </div>

        <div className="stats-card">
          <h3>Attendance</h3>
          {stats.subjectAttendances?.map((sa) => (
            <div key={sa.subjectName} className="attendance-bar-row">
              <span className="att-label">{sa.subjectName}</span>
              <div className="progress-bar-mini">
                <div
                  className="progress-fill"
                  style={{ width: `${sa.percentage}%` }}
                />
              </div>
            </div>
          ))}
        </div>

        <div className="stats-card">
          <h3>Personal Info</h3>
          <p>{stats.studentInfo?.name || stats.userName}</p>
          <p>{stats.studentInfo?.batch || "-"}</p>
          <p>{stats.userPhone || stats.studentInfo?.phone || "-"}</p>
          <p>{stats.userAddress || stats.studentInfo?.address || "-"}</p>
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);
  const { user, isStudent } = useAuth();
  const api = useApi();

  useEffect(() => {
    api
      .get("/dashboard/stats")
      .then((r) => setStats(r.data))
      .catch(() => setStats({}))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="page">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="welcome">
        <div className="welcome-left">
          <h1>Welcome</h1>
          <h2>{user?.name || "Guest"}</h2>
        </div>
        <FaBell className="notification-bell" />
      </header>

      {isStudent ? (
        <StudentDashboard stats={stats} api={api} />
      ) : (
        <AdminDashboard stats={stats} api={api} />
      )}
    </div>
  );
}
