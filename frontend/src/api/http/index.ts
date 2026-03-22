import { fetchTransport } from "./fetchTransport";
import { HttpClient } from "./client";
import { mockTransport } from "./mockTransport";

function resolveTransport(url: string) {
  return url.startsWith("/auth") ? fetchTransport : mockTransport;
}

export const httpClient = new HttpClient(async (config) => resolveTransport(config.url)(config));

export { HttpError } from "./types";
export type { HttpMethod, HttpRequestConfig, HttpTransport } from "./types";
