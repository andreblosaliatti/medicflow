import { clearStoredSession, getAccessToken } from "../../auth/session";
import { API_BASE_URL } from "./config";
import { HttpError, type HttpRequestConfig, type HttpTransport } from "./types";

function buildUrl(url: string) {
  if (/^https?:\/\//.test(url)) return url;
  return `${API_BASE_URL}${url}`;
}

async function parseResponse(response: Response) {
  const contentType = response.headers.get("content-type") ?? "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text || null;
}

export const fetchTransport: HttpTransport = async <TResponse, TBody>(
  config: HttpRequestConfig<TBody>,
): Promise<TResponse> => {
  const headers = new Headers(config.headers);
  const token = getAccessToken();
  const authMode = config.auth ?? "auto";

  if (token && authMode !== "none") {
    headers.set("Authorization", `Bearer ${token}`);
  }

  if (config.body !== undefined && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (!headers.has("Accept")) {
    headers.set("Accept", "application/json");
  }

  const response = await fetch(buildUrl(config.url), {
    method: config.method,
    headers,
    body: config.body === undefined ? undefined : JSON.stringify(config.body),
  });

  const payload = await parseResponse(response);

  if (!response.ok) {
    if ((response.status === 401 || response.status === 403) && authMode !== "none") {
      clearStoredSession({
        reason: response.status === 401 ? "unauthorized" : "forbidden",
        status: response.status,
        path: config.url,
      });
    }

    const message =
      typeof payload === "object" && payload !== null && "message" in payload && typeof payload.message === "string"
        ? payload.message
        : `Falha na requisição (${response.status}).`;

    throw new HttpError(message, response.status, payload);
  }

  return payload as TResponse;
};
