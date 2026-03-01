import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";
import { useAuth } from "../contexts/AuthContext";

export default function MarkAttendance() {
  const api = useApi();
  const { user } = useAuth();
  const [students, setStudents] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [selectedSubject, setSelectedSubject] = useState("");
  const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
  const [attendance, setAttendance] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [studentAttendance, setStudentAttendance] = useState({});

  useEffect(() => {
    Promise.all([api.get("/students"), api.get("/dashboard/subjects")])
      .then(([studRes, subRes]) => {
        setStudents(studRes.data);
        setSubjects(subRes.data);
        if (subRes.data.length > 0) setSelectedSubject(subRes.data[0].id);
        // Initialize all as PRESENT
        const att = {};
        studRes.data.forEach((s) => (att[s.id] = "PRESENT"));
        setAttendance(att);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (selectedStudent) {
      api
        .get(`/attendance/student/${selectedStudent.id}/percentage`)
        .then((r) => setStudentAttendance(r.data))
        .catch(() => setStudentAttendance({}));
    }
  }, [selectedStudent]);

  const toggleAttendance = (studentId) => {
    setAttendance((prev) => ({
      ...prev,
      [studentId]: prev[studentId] === "PRESENT" ? "ABSENT" : "PRESENT",
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedSubject) {
      setMessage("Please select a subject");
      return;
    }
    setSubmitting(true);
    setMessage("");

    try {
      const entries = Object.entries(attendance).map(([studentId, status]) => ({
        studentId: Number(studentId),
        status,
      }));

      await api.post("/attendance", {
        subjectId: Number(selectedSubject),
        date,
        entries,
      });
      setMessage("Attendance saved successfully!");
    } catch (err) {
      setMessage(
        "Failed to save attendance: " +
          (err.response?.data?.error || err.message),
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="page">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div className="page">
      <h1 className="page-title">Mark Attendance</h1>

      {/* Student profile card when selected */}
      {selectedStudent && (
        <div className="student-detail-section">
          <section className="profile-card">
            <div className="avatar" />
            <div className="profile-info">
              <p>
                <strong>Name:</strong> {selectedStudent.name}
              </p>
              <p>
                <strong>Gender:</strong> {selectedStudent.gender || "-"}
              </p>
              <p>
                <strong>Date Of Birth:</strong> {selectedStudent.dob || "-"}
              </p>
              <p>
                <strong>Address:</strong> {selectedStudent.address || "-"}
              </p>
            </div>
          </section>

          {/* Subject attendance bars */}
          <div className="subject-attendance-bars">
            {Object.entries(studentAttendance).map(([subject, pct]) => (
              <div key={subject} className="att-progress-row">
                <span className="att-subject-name">{subject}</span>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: `${pct}%` }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      <form className="attendance-form" onSubmit={handleSubmit}>
        <div className="form-controls">
          <div className="form-group">
            <label>Subject</label>
            <select
              value={selectedSubject}
              onChange={(e) => setSelectedSubject(e.target.value)}
            >
              {subjects.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Date</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
            />
          </div>
        </div>

        <div className="data-table">
          <div className="table-head">
            <div className="col-id">Student ID</div>
            <div className="col-name">Student Name</div>
            <div className="col-gpa">GPA</div>
            <div className="col-status">Status</div>
          </div>
          {students.map((s) => (
            <div
              className={`table-row ${selectedStudent?.id === s.id ? "selected" : ""}`}
              key={s.id}
              onClick={() => setSelectedStudent(s)}
            >
              <div className="col-id">{s.studentId}</div>
              <div className="col-name">{s.name}</div>
              <div className="col-gpa">{s.gpa}</div>
              <div className="col-status">
                <button
                  type="button"
                  className={`status-btn ${attendance[s.id] === "PRESENT" ? "present" : "absent"}`}
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleAttendance(s.id);
                  }}
                >
                  {attendance[s.id]}
                </button>
              </div>
            </div>
          ))}
        </div>

        {message && (
          <div
            className={`form-message ${message.includes("success") ? "success" : "error"}`}
          >
            {message}
          </div>
        )}

        <button type="submit" className="btn-submit" disabled={submitting}>
          {submitting ? "Saving..." : "Save Attendance"}
        </button>
      </form>
    </div>
  );
}
