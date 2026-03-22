import type { SessionData } from "../../auth/session";
import { httpClient } from "../http";
import { toSessionData } from "./mappers";
import type { LoginRequest, LoginResponse } from "./types";

export async function login(credentials: LoginRequest): Promise<SessionData> {
  const response = await httpClient.post<LoginResponse, LoginRequest>("/auth/login", credentials, { auth: "none" });
  return toSessionData(response);
}

export async function me(): Promise<SessionData> {
  const response = await httpClient.get<LoginResponse>("/auth/me", { auth: "required" });
  return toSessionData(response);
}
