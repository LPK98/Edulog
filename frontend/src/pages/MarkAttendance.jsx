import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";

export default function MarkAttendance() {
  const api = useApi();
  const [students, setStudents] = useState([]);

  useEffect(() => {
    api
      .get("/students")
      .then((r) => setStudents(r.data))
      .catch(() => setStudents([]));
  }, []);

  return (
    <div className="page">
      <h1>Mark Attendance</h1>
      <form className="attendance-form">
        {students.map((s) => (
          <div key={s.id} className="attendance-row">
            <label>
              <input type="checkbox" name={s.id} /> {s.name}
            </label>
          </div>
        ))}
        <button type="submit">Save Attendance</button>
      </form>
    </div>
  );
}
