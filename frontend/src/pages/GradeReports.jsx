import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";
import { useAuth } from "../contexts/AuthContext";

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
      // Auto-load own data
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

  if (loading && isStudent)
    return (
      <div className="page">
        <div className="spinner" />
      </div>
    );

  return (
    <div className="page">
      <h1 className="page-title">Grade Reports</h1>

      <div className="report-layout">
        {/* Student list for admin/teacher */}
        {!isStudent && (
          <div className="student-selector">
            <input
              type="text"
              placeholder="Search student..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="search-input"
            />
            <div className="student-list-scroll">
              {filtered.map((s) => (
                <div
                  key={s.id}
                  className={`student-option ${selectedStudent?.id === s.id ? "selected" : ""}`}
                  onClick={() => selectStudent(s)}
                >
                  <div className="avatar-sm" />
                  <div>
                    <p className="name">{s.name}</p>
                    <p className="sid">{s.studentId}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Report panel */}
        {displayStudent && (
          <div className="report-panel">
            <section className="profile-card">
              <div className="avatar" />
              <div className="profile-info">
                <p>
                  <strong>Name:</strong> {displayStudent.name}
                </p>
                <p>
                  <strong>Student ID:</strong> {displayStudent.studentId}
                </p>
              </div>
            </section>

            <div className="report-header">
              <h2>MY REPORT</h2>
              <button
                className="btn-download"
                onClick={downloadPdf}
                disabled={downloading}
              >
                {downloading ? "Downloading..." : "Download PDF"}
              </button>
            </div>

            {/* GPA card */}
            {gpa !== null && (
              <div className="gpa-card">
                <span className="gpa-label">GPA</span>
                <span className="gpa-value">{gpa}</span>
              </div>
            )}

            {/* Grades table */}
            <div className="data-table">
              <div className="table-head">
                <div className="col-subject">Subject</div>
                <div className="col-grade">Grade</div>
                <div className="col-points">Points</div>
                <div className="col-semester">Semester</div>
              </div>
              {grades.map((g) => (
                <div className="table-row" key={g.id}>
                  <div className="col-subject">{g.subjectName}</div>
                  <div className="col-grade">{g.grade}</div>
                  <div className="col-points">{g.gradePoint}</div>
                  <div className="col-semester">{g.semester}</div>
                </div>
              ))}
              {grades.length === 0 && (
                <div className="table-row">
                  <div className="col-subject">No grades recorded</div>
                </div>
              )}
            </div>

            {/* Attendance bars */}
            <h3 className="section-title">Attendance</h3>
            <div className="subject-attendance-bars">
              {Object.entries(attendance).map(([subject, pct]) => (
                <div key={subject} className="att-progress-row">
                  <span className="att-subject-name">{subject}</span>
                  <div className="progress-bar">
                    <div
                      className="progress-fill"
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                  <span className="att-pct">{pct}%</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
