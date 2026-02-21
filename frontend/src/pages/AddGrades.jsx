import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";

export default function AddGrades() {
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
      <h1>Add Grades</h1>
      <form className="grades-form">
        {students.map((s) => (
          <div key={s.id} className="grades-row">
            <label>{s.name}</label>
            <select name={s.id} defaultValue="">
              <option value="">--</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
              <option value="E">E</option>
            </select>
          </div>
        ))}
        <button type="submit">Submit Grades</button>
      </form>
    </div>
  );
}
