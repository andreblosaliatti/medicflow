import { HttpClient } from "./client";
import { mockTransport } from "./mockTransport";

export const httpClient = new HttpClient(mockTransport);

export { HttpError } from "./types";
export type { HttpMethod, HttpRequestConfig, HttpTransport } from "./types";
