import React, { useEffect, useState } from "react";
import { useApi } from "../contexts/ApiContext";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from "recharts";

const COLORS = ["#ff9a20", "#4caf50", "#2196f3", "#e91e63", "#9c27b0"];

export default function Analytics() {
  const api = useApi();
  const [distribution, setDistribution] = useState([]);
  const [subjectData, setSubjectData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.get("/grades/distribution"),
      api.get("/dashboard/subjects"),
    ])
      .then(([distRes, subRes]) => {
        // Grade distribution for pie chart: { A: 12, B: 8, ... }
        const dist = distRes.data;
        const pieData = Object.entries(dist).map(([name, value]) => ({
          name,
          value,
        }));
        setDistribution(pieData);

        // Build per-subject bar data from subjects
        const subs = subRes.data || [];
        // Fetch attendance for each subject (we'll use a summary approach)
        setSubjectData(subs.map((s) => ({ name: s.code || s.name, id: s.id })));
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading)
    return (
      <div className="page">
        <div className="spinner" />
      </div>
    );

  return (
    <div className="page">
      <h1 className="page-title">Analytics & Charts</h1>

      <div className="analytics-grid">
        {/* Grade Distribution Pie */}
        <div className="chart-card">
          <h3>Grade Distribution</h3>
          {distribution.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={distribution}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={110}
                  dataKey="value"
                  label={({ name, value }) => `${name}: ${value}`}
                >
                  {distribution.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <p className="no-data">No grade data available</p>
          )}
        </div>

        {/* Grade Count Bar Chart */}
        <div className="chart-card">
          <h3>Grades Overview</h3>
          {distribution.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={distribution}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" fill="#ff9a20" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="no-data">No grade data available</p>
          )}
        </div>

        {/* Subjects summary */}
        <div className="chart-card wide">
          <h3>Enrolled Subjects</h3>
          <div className="subject-chips">
            {subjectData.map((s) => (
              <span key={s.id} className="chip">
                {s.name}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
