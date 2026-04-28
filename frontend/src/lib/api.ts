import axios, {
  AxiosError,
  type AxiosRequestConfig,
  type AxiosResponse,
} from "../vendor/axios";
import {
  clearStoredSession,
  getAccessToken,
  rememberPostLoginRedirect,
} from "../auth/session";

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "/api/v1").replace(/\/$/, "");
const SKIP_AUTH_HEADER = "X-Skip-Auth";

type ApiRequestConfig<TData = unknown> = AxiosRequestConfig<TData>;
type ApiResponse<TData = unknown, TRequestData = unknown> = AxiosResponse<TData, TRequestData>;
type ApiPromise<TData = unknown, TRequestData = unknown> = Promise<ApiResponse<TData, TRequestData>>;

function shouldSkipAuthHeader(headers?: ApiRequestConfig["headers"]) {
  return headers?.[SKIP_AUTH_HEADER] === "true";
}

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  const headers = {
    ...(config.headers ?? {}),
  };

  if (!shouldSkipAuthHeader(headers) && token) {
    headers.Authorization = `Bearer ${token}`;
  }

  config.headers = headers;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const axiosError = error instanceof AxiosError ? error : null;
    const status = axiosError?.response?.status;
    const skipAuth = shouldSkipAuthHeader(axiosError?.config.headers);
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

export function unwrapResponse<TData, TRequestData = unknown>(promise: ApiPromise<TData, TRequestData>): Promise<TData> {
  return promise.then((response) => response.data);
}

export function withPublicRequest<TData = unknown>(config: ApiRequestConfig<TData> = {}): ApiRequestConfig<TData> {
  return {
    ...config,
    headers: {
      ...(config.headers ?? {}),
      [SKIP_AUTH_HEADER]: "true",
    },
  };
}
