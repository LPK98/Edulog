import React, { createContext, useContext } from "react";
import axios from "axios";

const ApiContext = createContext(axios);

export function ApiProvider({ children }) {
  axios.defaults.baseURL = "/api";
  // you could add interceptors here
  return <ApiContext.Provider value={axios}>{children}</ApiContext.Provider>;
}

export function useApi() {
  return useContext(ApiContext);
}
