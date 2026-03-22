import { setSessionUser } from "../../auth/session";
import { httpClient } from "../http";
import { toSessionUserViewModel } from "./mappers";
import type { LoginRequest, LoginResponse, SessionUserViewModel } from "./types";

export async function login(credentials: LoginRequest): Promise<SessionUserViewModel> {
  const response = await httpClient.post<LoginResponse, LoginRequest>("/auth/login", credentials);
  const sessionUser = toSessionUserViewModel(response.usuario);
  setSessionUser(sessionUser);
  return sessionUser;
}
