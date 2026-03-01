import React, { useEffect, useState, useMemo } from "react";
import { useApi } from "../contexts/ApiContext";
import { useAuth } from "../contexts/AuthContext";
import {
  FiDownload,
  FiSearch,
  FiUser,
  FiAward,
  FiBookOpen,
  FiCheckCircle,
  FiBarChart2,
} from "react-icons/fi";

export default function GradeReports() {
  const api = useApi();
  const { user, isStudent } = useAuth();
  const [students, setStudents] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [grades, setGrades] = useState([]);
  const [attendance, setAttendance] = useState({});
  const [gpa, setGpa] = useState(null);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);
  const [search, setSearch] = useState("");

  useEffect(() => {
    if (isStudent) {
      loadStudentData(user.studentId);
    } else {
      api
        .get("/students")
        .then((r) => setStudents(r.data))
        .catch(() => {})
        .finally(() => setLoading(false));
    }
  }, []);

  const loadStudentData = (studentId) => {
    setLoading(true);
    Promise.all([
      api.get(`/grades/student/${studentId}`),
      api.get(`/grades/student/${studentId}/gpa`),
      api.get(`/attendance/student/${studentId}/percentage`),
    ])
      .then(([gradesRes, gpaRes, attRes]) => {
        setGrades(gradesRes.data);
        setGpa(gpaRes.data);
        setAttendance(attRes.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  const selectStudent = (s) => {
    setSelectedStudent(s);
    loadStudentData(s.id);
  };

  const downloadPdf = async () => {
    const id = isStudent ? user.studentId : selectedStudent?.id;
    if (!id) return;
    setDownloading(true);
    try {
      const res = await api.get(`/reports/student/${id}/pdf`, {
        responseType: "blob",
      });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `report_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch {
      alert("Failed to download report");
    } finally {
      setDownloading(false);
    }
  };

  const displayStudent = isStudent
    ? { name: user.name, studentId: user.studentId }
    : selectedStudent;

  const filtered = search
    ? students.filter(
        (s) =>
          s.name.toLowerCase().includes(search.toLowerCase()) ||
          s.studentId?.toLowerCase().includes(search.toLowerCase()),
      )
    : students;

  // Compute summary stats
  const totalSubjects = grades.length;
  const highestGrade = useMemo(() => {
    if (!grades.length) return "—";
    const order = [
      "A+",
      "A",
      "A-",
      "B+",
      "B",
      "B-",
      "C+",
      "C",
      "C-",
      "D+",
      "D",
      "F",
    ];
    const sorted = [...grades].sort(
      (a, b) => order.indexOf(a.grade) - order.indexOf(b.grade),
    );
    return sorted[0]?.grade || "—";
  }, [grades]);

  const avgAttendance = useMemo(() => {
    const vals = Object.values(attendance);
    if (!vals.length) return 0;
    return Math.round(vals.reduce((a, b) => a + b, 0) / vals.length);
  }, [attendance]);

  const getGradeColor = (grade) => {
    if (!grade) return "var(--text-muted)";
    if (grade.startsWith("A")) return "#22c55e";
    if (grade.startsWith("B")) return "#00d4ff";
    if (grade.startsWith("C")) return "#f59e0b";
    if (grade.startsWith("D")) return "#f97316";
    return "#ef4444";
  };

  const getAttColor = (pct) => {
    if (pct >= 85) return "#22c55e";
    if (pct >= 70) return "#00d4ff";
    if (pct >= 50) return "#f59e0b";
    return "#ef4444";
  };

  if (loading && isStudent)
    return (
      <div className="report-fullscreen">
        <div className="spinner" />
      </div>
    );

  return (
    <div className="report-fullscreen">
      {/* Header bar */}
      <div className="report-topbar">
        <div className="report-topbar-left">
          <FiBarChart2 className="report-topbar-icon" />
          <h1 className="report-topbar-title">Grade Reports</h1>
        </div>
        {displayStudent && (
          <button
            className="btn-download-modern"
            onClick={downloadPdf}
            disabled={downloading}
          >
            <FiDownload />
            {downloading ? "Downloading..." : "Download PDF"}
          </button>
        )}
      </div>

      <div className="report-body">
        {/* Student list for admin/teacher */}
        {!isStudent && (
          <aside className="report-sidebar">
            <div className="report-search-wrap">
              <FiSearch className="report-search-icon" />
              <input
                type="text"
                placeholder="Search students..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="report-search-input"
              />
            </div>
            <div className="report-student-list">
              {filtered.map((s) => (
                <div
                  key={s.id}
                  className={`report-student-item ${selectedStudent?.id === s.id ? "active" : ""}`}
                  onClick={() => selectStudent(s)}
                >
                  <div className="report-student-avatar">
                    {s.name?.charAt(0)?.toUpperCase()}
                  </div>
                  <div className="report-student-meta">
                    <span className="report-student-name">{s.name}</span>
                    <span className="report-student-id">{s.studentId}</span>
                  </div>
                </div>
              ))}
              {filtered.length === 0 && (
                <div className="report-empty-list">No students found</div>
              )}
            </div>
          </aside>
        )}

        {/* Main report content */}
        <main className={`report-main ${isStudent ? "report-main-full" : ""}`}>
          {displayStudent ? (
            <>
              {/* Profile + Summary row */}
              <div className="report-summary-grid">
                {/* Profile card */}
                <div className="report-profile-card">
                  <div className="report-avatar-lg">
                    {displayStudent.name?.charAt(0)?.toUpperCase()}
                  </div>
                  <div className="report-profile-details">
                    <h2 className="report-profile-name">
                      {displayStudent.name}
                    </h2>
                    <span className="report-profile-id">
                      <FiUser size={13} /> {displayStudent.studentId}
                    </span>
                  </div>
                </div>

                {/* Stat cards */}
                <div className="report-stat-card">
                  <div className="report-stat-icon gpa-icon">
                    <FiAward />
                  </div>
                  <div className="report-stat-info">
                    <span className="report-stat-label">GPA</span>
                    <span className="report-stat-value">
                      {gpa !== null
                        ? typeof gpa === "number"
                          ? gpa.toFixed(2)
                          : gpa
                        : "—"}
                    </span>
                  </div>
                </div>

                <div className="report-stat-card">
                  <div className="report-stat-icon subjects-icon">
                    <FiBookOpen />
                  </div>
                  <div className="report-stat-info">
                    <span className="report-stat-label">Subjects</span>
                    <span className="report-stat-value">{totalSubjects}</span>
                  </div>
                </div>

                <div className="report-stat-card">
                  <div className="report-stat-icon att-icon">
                    <FiCheckCircle />
                  </div>
                  <div className="report-stat-info">
                    <span className="report-stat-label">Avg Attendance</span>
                    <span className="report-stat-value">{avgAttendance}%</span>
                  </div>
                </div>
              </div>

              {/* Grades table */}
              <div className="report-section">
                <h3 className="report-section-title">
                  <FiBookOpen size={16} /> Academic Grades
                </h3>
                <div className="report-table-wrap">
                  <table className="report-table">
                    <thead>
                      <tr>
                        <th className="th-num">#</th>
                        <th className="th-subject">Subject</th>
                        <th className="th-grade">Grade</th>
                        <th className="th-points">Points</th>
                        <th className="th-semester">Semester</th>
                      </tr>
                    </thead>
                    <tbody>
                      {grades.map((g, i) => (
                        <tr key={g.id}>
                          <td className="td-num">{i + 1}</td>
                          <td className="td-subject">
                            {g.subject?.name || "—"}
                          </td>
                          <td className="td-grade">
                            <span
                              className="grade-badge"
                              style={{
                                background: `${getGradeColor(g.grade)}18`,
                                color: getGradeColor(g.grade),
                                borderColor: `${getGradeColor(g.grade)}40`,
                              }}
                            >
                              {g.grade}
                            </span>
                          </td>
                          <td className="td-points">{g.gradePoint}</td>
                          <td className="td-semester">
                            <span className="semester-tag">{g.semester}</span>
                          </td>
                        </tr>
                      ))}
                      {grades.length === 0 && (
                        <tr>
                          <td colSpan={5} className="td-empty">
                            No grades recorded yet
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Attendance */}
              <div className="report-section">
                <h3 className="report-section-title">
                  <FiCheckCircle size={16} /> Attendance Overview
                </h3>
                <div className="report-attendance-grid">
                  {Object.entries(attendance).map(([subject, pct]) => (
                    <div key={subject} className="report-att-card">
                      <div className="report-att-header">
                        <span className="report-att-subject">{subject}</span>
                        <span
                          className="report-att-pct"
                          style={{ color: getAttColor(pct) }}
                        >
                          {pct}%
                        </span>
                      </div>
                      <div className="report-att-bar">
                        <div
                          className="report-att-fill"
                          style={{
                            width: `${pct}%`,
                            background: `linear-gradient(90deg, ${getAttColor(pct)}, ${getAttColor(pct)}80)`,
                          }}
                        />
                      </div>
                    </div>
                  ))}
                  {Object.keys(attendance).length === 0 && (
                    <div className="report-empty-att">
                      No attendance data available
                    </div>
                  )}
                </div>
              </div>
            </>
          ) : (
            <div className="report-placeholder">
              <FiUser size={48} />
              <h3>Select a Student</h3>
              <p>Choose a student from the list to view their grade report</p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
