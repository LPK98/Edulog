import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";

export default function GradeReports() {
  const api = useApi();
  const [reports, setReports] = useState([]);

  useEffect(() => {
    api
      .get("/reports")
      .then((r) => setReports(r.data))
      .catch(() => setReports([]));
  }, []);

  return (
    <div className="page">
      <h1>Grade Reports</h1>
      <table className="reports-table">
        <thead>
          <tr>
            <th>Student</th>
            <th>GPA</th>
            <th>Hours</th>
          </tr>
        </thead>
        <tbody>
          {reports.map((rep) => (
            <tr key={rep.id}>
              <td>{rep.studentName}</td>
              <td>{rep.gpa}</td>
              <td>{rep.hours}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
