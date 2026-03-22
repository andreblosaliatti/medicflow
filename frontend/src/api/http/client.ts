import type { HttpRequestConfig, HttpTransport } from "./types";

export class HttpClient {
  private readonly transport: HttpTransport;

  constructor(transport: HttpTransport) {
    this.transport = transport;
  }

  request<TResponse, TBody = unknown>(config: HttpRequestConfig<TBody>) {
    return this.transport<TResponse, TBody>(config);
  }

  get<TResponse>(url: string) {
    return this.request<TResponse>({ method: "GET", url });
  }

  post<TResponse, TBody = unknown>(url: string, body?: TBody) {
    return this.request<TResponse, TBody>({ method: "POST", url, body });
  }

  put<TResponse, TBody = unknown>(url: string, body?: TBody) {
    return this.request<TResponse, TBody>({ method: "PUT", url, body });
  }

  patch<TResponse, TBody = unknown>(url: string, body?: TBody) {
    return this.request<TResponse, TBody>({ method: "PATCH", url, body });
  }

  delete<TResponse>(url: string) {
    return this.request<TResponse>({ method: "DELETE", url });
  }
}
