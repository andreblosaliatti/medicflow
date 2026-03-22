import { fetchTransport } from "./fetchTransport";
import { API_MODE } from "./config";
import { HttpClient } from "./client";
import { mockTransport } from "./mockTransport";

function resolveTransport() {
  return API_MODE === "mock" ? mockTransport : fetchTransport;
}

export const httpClient = new HttpClient((config) => resolveTransport()(config));

export { HttpError } from "./types";
export type { HttpAuthMode, HttpMethod, HttpRequestConfig, HttpTransport } from "./types";
