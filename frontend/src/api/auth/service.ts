import type { SessionData } from "../../auth/session";
import { getSession, login as loginWithStorage } from "../../services/authService";
import type { LoginRequest } from "./types";

export async function login(credentials: LoginRequest): Promise<SessionData> {
  return loginWithStorage(credentials);
}

export async function me(): Promise<SessionData> {
  const session = getSession();

  if (!session) {
    throw new Error("Sessão não encontrada.");
  }

  return session;
}
