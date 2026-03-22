export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export type HttpRequestConfig<TBody = unknown> = {
  method: HttpMethod;
  url: string;
  body?: TBody;
  headers?: Record<string, string>;
};

export type HttpTransport = <TResponse = unknown, TBody = unknown>(
  config: HttpRequestConfig<TBody>
) => Promise<TResponse>;

export class HttpError extends Error {
  readonly status: number;
  readonly details?: unknown;

  constructor(message: string, status = 500, details?: unknown) {
    super(message);
    this.name = "HttpError";
    this.status = status;
    this.details = details;
  }
}
