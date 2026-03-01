import React, { createContext, useContext, useMemo } from "react";
import axios from "axios";

const ApiContext = createContext(null);

export function ApiProvider({ children }) {
  const api = useMemo(() => {
    const instance = axios.create({
      baseURL: "/api",
    });

    // Request interceptor: attach JWT token
    instance.interceptors.request.use((config) => {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    // Response interceptor: handle 401
    instance.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem("token");
          localStorage.removeItem("user");
          window.location.href = "/login";
        }
        return Promise.reject(error);
      },
    );

    return instance;
  }, []);

  return <ApiContext.Provider value={api}>{children}</ApiContext.Provider>;
}

export function useApi() {
  return useContext(ApiContext);
}
