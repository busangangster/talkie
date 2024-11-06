import axios from "axios";
import axiosRetry from "axios-retry";

const instance = axios.create({
  baseURL: import.meta.env.VITE_SERVER_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosRetry(instance, {
  retries: 3,
  retryDelay: () => 500,
  retryCondition: (error) => {
    return (
      (error.response &&
        (error.response.status >= 500 || error.response.status === 404)) ||
      error.code === "ECONNABORTED"
    );
  },
  onRetry: () => {},
});

export default instance;
