const DEFAULT_DEV_API_PREFIX = "/api";

function normalizeBaseUrl(value: string | undefined) {
  if (!value) return null;
  return value.replace(/\/$/, "");
}

export const API_BASE_URL = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL)
  ?? (import.meta.env.DEV ? DEFAULT_DEV_API_PREFIX : "");

export const API_MODE = import.meta.env.VITE_API_MODE === "mock" ? "mock" : "backend";
