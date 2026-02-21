import React from "react";

// placeholder simple bar chart using CSS
export default function Analytics() {
  const data = [
    { label: "Computer System", value: 60 },
    { label: "Data analysis", value: 45 },
    { label: "Multimedia", value: 75 },
    { label: "Database", value: 50 },
  ];

  return (
    <div className="page">
      <h1>Analytics & Charts</h1>
      <div className="chart">
        {data.map((d) => (
          <div key={d.label} className="bar-row">
            <span className="bar-label">{d.label}</span>
            <div className="bar-container">
              <div className="bar" style={{ width: `${d.value}%` }}></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
