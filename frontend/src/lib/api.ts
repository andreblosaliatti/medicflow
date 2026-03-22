import axios, { AxiosError, type AxiosRequestConfig, type AxiosResponse } from "../vendor/axios";
import { clearStoredSession, getAccessToken, rememberPostLoginRedirect } from "../auth/session";

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "/api").replace(/\/$/, "");
const SKIP_AUTH_HEADER = "X-Skip-Auth";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  const headers = { ...(config.headers ?? {}) };
  const skipAuth = headers[SKIP_AUTH_HEADER] === "true";

  if (!skipAuth && token) {
    headers.Authorization = `Bearer ${token}`;
  }


  return {
    ...config,
    headers,
  } as AxiosRequestConfig;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const axiosError = error instanceof AxiosError ? error : null;
    const status = axiosError?.response?.status;
    const skipAuth = axiosError?.config.headers?.[SKIP_AUTH_HEADER] === "true";
    const currentPath = `${window.location.pathname}${window.location.search}${window.location.hash}`;

    if (status === 401 && !skipAuth) {
      clearStoredSession({ reason: "unauthorized", status, path: currentPath });

      if (window.location.pathname !== "/login") {
        rememberPostLoginRedirect(currentPath);
        window.location.replace("/login");
      }
    }

    throw error;
  },
);

export function unwrapResponse<TData>(promise: Promise<AxiosResponse<TData>>) {
  return promise.then((response) => response.data);
}

export function withPublicRequest(config: AxiosRequestConfig = {}) {
  return {
    ...config,
    headers: {
      ...(config.headers ?? {}),
      [SKIP_AUTH_HEADER]: "true",
    },
  } as AxiosRequestConfig;
}
