# EduLog — Learning Management System (Starter)

This repository is a mono-repo scaffold for the EduLog LMS UI you provided.

Structure

- `frontend/` — React (Vite) frontend
- `backend/` — Spring Boot backend (Maven)

Quick Start

1. Backend (Java / Spring Boot)

- Requirements: Java 17+, Maven
- Start server:

```powershell
cd backend
mvn spring-boot:run
```

The backend will start on port `8080` by default.

2. Frontend (React + Vite)

- Requirements: Node 16+ (npm)
- Start dev server:

```powershell
cd frontend
npm install
npm run dev
```

The frontend dev server will start on port `5173` by default.

Notes

- Place your UI images under `frontend/public/assets` and update components to use them.
- This scaffold uses an in-memory sample for the backend APIs. Replace with a database as needed.
