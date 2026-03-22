import type { HttpRequestConfig, HttpTransport } from "./types";

export class HttpClient {
  private readonly transport: HttpTransport;

  constructor(transport: HttpTransport) {
    this.transport = transport;
  }

  request<TResponse, TBody = unknown>(config: HttpRequestConfig<TBody>) {
    return this.transport<TResponse, TBody>(config);
  }

  get<TResponse>(url: string, config?: Omit<HttpRequestConfig<never>, "method" | "url" | "body">) {
    return this.request<TResponse>({ method: "GET", url, ...config });
  }

  post<TResponse, TBody = unknown>(url: string, body?: TBody, config?: Omit<HttpRequestConfig<TBody>, "method" | "url" | "body">) {
    return this.request<TResponse, TBody>({ method: "POST", url, body, ...config });
  }

  put<TResponse, TBody = unknown>(url: string, body?: TBody, config?: Omit<HttpRequestConfig<TBody>, "method" | "url" | "body">) {
    return this.request<TResponse, TBody>({ method: "PUT", url, body, ...config });
  }

  patch<TResponse, TBody = unknown>(url: string, body?: TBody, config?: Omit<HttpRequestConfig<TBody>, "method" | "url" | "body">) {
    return this.request<TResponse, TBody>({ method: "PATCH", url, body, ...config });
  }

  delete<TResponse>(url: string, config?: Omit<HttpRequestConfig<never>, "method" | "url" | "body">) {
    return this.request<TResponse>({ method: "DELETE", url, ...config });
  }
}
