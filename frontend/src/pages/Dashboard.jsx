import React, { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useApi } from "../contexts/ApiContext";

export default function Dashboard() {
  const [students, setStudents] = useState([]);
  const { user } = useAuth();

  const api = useApi();

  useEffect(() => {
    api
      .get("/students")
      .then((r) => setStudents(r.data))
      .catch(() => setStudents([]));
  }, []);

  return (
    <div className="dashboard">
      <header className="welcome">
        <h1>Welcome</h1>
        <h2>{user?.name || "Guest"}</h2>
      </header>

      <section className="profile-card">
        <div className="avatar" />
        <div className="info">
          <div>Name</div>
          <div>Gender</div>
          <div>Date Of Birth</div>
        </div>
      </section>

      <section className="table">
        <div className="table-head">
          <div>Student ID</div>
          <div>Name</div>
          <div>GPA</div>
        </div>
        {students.map((s) => (
          <div className="table-row" key={s.id}>
            <div>{s.studentId}</div>
            <div>{s.name}</div>
            <div>{s.gpa}</div>
          </div>
        ))}
      </section>
    </div>
  );
}
