import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";

const GRADE_OPTIONS = ["A", "B", "C", "D", "E"];

export default function AddGrades() {
  const api = useApi();
  const [students, setStudents] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [semester, setSemester] = useState("Semester 1");
  const [grades, setGrades] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [search, setSearch] = useState("");

  useEffect(() => {
    Promise.all([api.get("/students"), api.get("/dashboard/subjects")])
      .then(([studRes, subRes]) => {
        setStudents(studRes.data);
        setSubjects(subRes.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const selectStudent = (s) => {
    setSelectedStudent(s);
    setGrades({});
    setMessage("");
  };

  const updateGrade = (subjectId, grade) => {
    setGrades((prev) => ({ ...prev, [subjectId]: grade }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedStudent) {
      setMessage("Please select a student");
      return;
    }
    const entries = Object.entries(grades)
      .filter(([, g]) => g)
      .map(([subjectId, grade]) => ({ subjectId: Number(subjectId), grade }));
    if (entries.length === 0) {
      setMessage("Please assign at least one grade");
      return;
    }
    setSubmitting(true);
    setMessage("");
    try {
      await api.post("/grades", {
        studentId: selectedStudent.id,
        semester,
        grades: entries,
      });
      setMessage("Grades saved successfully!");
      setGrades({});
    } catch (err) {
      setMessage(
        "Failed to save grades: " + (err.response?.data?.error || err.message),
      );
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = search
    ? students.filter(
        (s) =>
          s.name.toLowerCase().includes(search.toLowerCase()) ||
          s.studentId?.toLowerCase().includes(search.toLowerCase()),
      )
    : students;

  if (loading)
    return (
      <div className="page">
        <div className="spinner" />
      </div>
    );

  return (
    <div className="page">
      <h1 className="page-title">Add Grades</h1>

      {/* Student selector */}
      <div className="grade-top-row">
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

        {selectedStudent && (
          <section className="profile-card grade-profile">
            <div className="avatar" />
            <div className="profile-info">
              <p>
                <strong>Name:</strong> {selectedStudent.name}
              </p>
              <p>
                <strong>Student ID:</strong> {selectedStudent.studentId}
              </p>
              <p>
                <strong>Batch:</strong> {selectedStudent.batch || "-"}
              </p>
              <p>
                <strong>GPA:</strong> {selectedStudent.gpa}
              </p>
            </div>
          </section>
        )}
      </div>

      {selectedStudent && (
        <form className="grades-form" onSubmit={handleSubmit}>
          <div className="form-group semester-select">
            <label>Semester</label>
            <select
              value={semester}
              onChange={(e) => setSemester(e.target.value)}
            >
              <option>Semester 1</option>
              <option>Semester 2</option>
              <option>Semester 3</option>
              <option>Semester 4</option>
            </select>
          </div>

          <div className="data-table">
            <div className="table-head">
              <div className="col-subject">Subject</div>
              <div className="col-code">Code</div>
              <div className="col-grade">Grade</div>
            </div>
            {subjects.map((sub, idx) => (
              <div
                className={`table-row ${idx % 2 === 0 ? "even" : "odd"}`}
                key={sub.id}
              >
                <div className="col-subject">{sub.name}</div>
                <div className="col-code">{sub.code}</div>
                <div className="col-grade">
                  <select
                    value={grades[sub.id] || ""}
                    onChange={(e) => updateGrade(sub.id, e.target.value)}
                  >
                    <option value="">--</option>
                    {GRADE_OPTIONS.map((g) => (
                      <option key={g} value={g}>
                        {g}
                      </option>
                    ))}
                  </select>
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
            {submitting ? "Saving..." : "Submit Grades"}
          </button>
        </form>
      )}
    </div>
  );
}
