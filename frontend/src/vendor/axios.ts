export type AxiosHeaders = Record<string, string>;

export type AxiosRequestConfig<TData = unknown> = {
  baseURL?: string;
  url?: string;
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  headers?: AxiosHeaders;
  params?: Record<string, string | number | boolean | null | undefined>;
  data?: TData;
};

export type AxiosResponse<TData = unknown, TRequestData = unknown> = {
  data: TData;
  status: number;
  statusText: string;
  headers: AxiosHeaders;
  config: AxiosRequestConfig<TRequestData>;
};

export class AxiosError<TData = unknown, TRequestData = unknown> extends Error {
  readonly config: AxiosRequestConfig<TRequestData>;
  readonly response?: AxiosResponse<TData, TRequestData>;
  readonly status?: number;

  constructor(
    message: string,
    config: AxiosRequestConfig<TRequestData>,
    response?: AxiosResponse<TData, TRequestData>,
  ) {
    super(message);
    this.name = "AxiosError";
    this.config = config;
    this.response = response;
    this.status = response?.status;
  }
}

type InterceptorFulfilled<TValue> = (value: TValue) => TValue | Promise<TValue>;
type InterceptorRejected = (error: unknown) => unknown;

type InterceptorHandler<TValue> = {
  fulfilled?: InterceptorFulfilled<TValue>;
  rejected?: InterceptorRejected;
};

class InterceptorManager<TValue> {
  private handlers: Array<InterceptorHandler<TValue> | null> = [];

  use(fulfilled?: InterceptorFulfilled<TValue>, rejected?: InterceptorRejected) {
    this.handlers.push({ fulfilled, rejected });
    return this.handlers.length - 1;
  }

  list() {
    return this.handlers.filter((handler): handler is InterceptorHandler<TValue> => handler !== null);
  }
}

function normalizeHeaders(headers: Headers): AxiosHeaders {
  const normalized: AxiosHeaders = {};
  headers.forEach((value, key) => {
    normalized[key] = value;
  });
  return normalized;
}

function buildUrl(config: AxiosRequestConfig) {
  const baseUrl = config.baseURL?.replace(/\/$/, "") ?? "";
  const requestUrl = config.url ?? "";
  const target = /^https?:\/\//.test(requestUrl)
    ? requestUrl
    : `${baseUrl}${requestUrl.startsWith("/") ? requestUrl : `/${requestUrl}`}`;

  const url = new URL(target, window.location.origin);

  Object.entries(config.params ?? {}).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") return;
    url.searchParams.set(key, String(value));
  });

  return url.toString();
}

async function parseBody(response: Response) {
  const contentType = response.headers.get("content-type") ?? "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text || null;
}

class AxiosInstance {
  readonly defaults: AxiosRequestConfig;
  readonly interceptors = {
    request: new InterceptorManager<AxiosRequestConfig>(),
    response: new InterceptorManager<AxiosResponse>(),
  };

  constructor(defaults: AxiosRequestConfig = {}) {
    this.defaults = defaults;
  }

  async request<TResponse = unknown, TData = unknown>(config: AxiosRequestConfig<TData>) {
    let nextConfig = {
      ...this.defaults,
      ...config,
      headers: {
        ...(this.defaults.headers ?? {}),
        ...(config.headers ?? {}),
      },
    } as AxiosRequestConfig<TData>;

    for (const handler of this.interceptors.request.list()) {
      if (handler.fulfilled) {
        nextConfig = await handler.fulfilled(nextConfig) as AxiosRequestConfig<TData>;
      }
    }

    try {
      const response = await fetch(buildUrl(nextConfig), {
        method: nextConfig.method ?? "GET",
        headers: nextConfig.headers,
        body: nextConfig.data === undefined ? undefined : JSON.stringify(nextConfig.data),
      });

      const data = await parseBody(response);
      const axiosResponse: AxiosResponse<TResponse, TData> = {
        data: data as TResponse,
        status: response.status,
        statusText: response.statusText,
        headers: normalizeHeaders(response.headers),
        config: nextConfig,
      };

      if (!response.ok) {
        throw new AxiosError<TResponse, TData>(
          typeof data === "object" && data !== null && "message" in data && typeof data.message === "string"
            ? data.message
            : `Request failed with status code ${response.status}`,
          nextConfig,
          axiosResponse,
        );
      }

      let nextResponse: AxiosResponse<TResponse, TData> = axiosResponse;
      for (const handler of this.interceptors.response.list()) {
        if (handler.fulfilled) {
          nextResponse = await handler.fulfilled(nextResponse) as AxiosResponse<TResponse, TData>;
        }
      }

      return nextResponse;
    } catch (error) {
      let thrown = error;

      for (const handler of this.interceptors.response.list()) {
        if (!handler.rejected) continue;

        try {
          const maybeRecovered = await handler.rejected(thrown);
          if (maybeRecovered !== undefined) {
            return maybeRecovered as AxiosResponse<TResponse, TData>;
          }
        } catch (nextError) {
          thrown = nextError;
        }
      }

      throw thrown;
    }
  }

  get<TResponse = unknown>(url: string, config: Omit<AxiosRequestConfig, "url" | "method"> = {}) {
    return this.request<TResponse>({ ...config, url, method: "GET" });
  }

  post<TResponse = unknown, TData = unknown>(url: string, data?: TData, config: Omit<AxiosRequestConfig<TData>, "url" | "method" | "data"> = {}) {
    return this.request<TResponse, TData>({ ...config, url, method: "POST", data });
  }

  put<TResponse = unknown, TData = unknown>(url: string, data?: TData, config: Omit<AxiosRequestConfig<TData>, "url" | "method" | "data"> = {}) {
    return this.request<TResponse, TData>({ ...config, url, method: "PUT", data });
  }

  patch<TResponse = unknown, TData = unknown>(url: string, data?: TData, config: Omit<AxiosRequestConfig<TData>, "url" | "method" | "data"> = {}) {
    return this.request<TResponse, TData>({ ...config, url, method: "PATCH", data });
  }

  delete<TResponse = unknown>(url: string, config: Omit<AxiosRequestConfig, "url" | "method"> = {}) {
    return this.request<TResponse>({ ...config, url, method: "DELETE" });
  }
}

const axios = {
  create(config: AxiosRequestConfig = {}) {
    return new AxiosInstance(config);
  },
  AxiosError,
};

export default axios;
