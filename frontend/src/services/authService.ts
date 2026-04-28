import { clearStoredSession, getAccessToken, getStoredSession, hasStoredSession, setStoredSession, type SessionData, type UserRole } from "../auth/session";
import { api, unwrapResponse, withPublicRequest } from "../lib/api";

export type LoginPayload = {
  login: string;
  senha: string;
};

type LoginResponse = {
  id: number | string;
  login?: string;
  nomeCompleto?: string;
  roles?: string[];
  permissions?: string[];
  token: string;
};

const ROLE_PRIORITY: readonly UserRole[] = ["ADMIN", "MEDICO", "SECRETARIA", "ATENDENTE", "ENFERMEIRO", "PACIENTE"];

function isUserRole(value: string): value is UserRole {
  return value === "ADMIN"
    || value === "MEDICO"
    || value === "SECRETARIA"
    || value === "ATENDENTE"
    || value === "ENFERMEIRO"
    || value === "PACIENTE";
}

function resolveRole(roles: string[]): UserRole {
  const normalized = roles.filter(isUserRole);
  const prioritized = ROLE_PRIORITY.find((role) => normalized.includes(role));
  if (!prioritized) {
    throw new Error("Usuario autenticado sem perfil compativel com a aplicacao.");
  }
  return prioritized;
}

function toSessionData(response: LoginResponse, payload: LoginPayload): SessionData {
  const normalizedRoles = (response.roles ?? []).filter(isUserRole);
  const role = resolveRole(response.roles ?? []);

  return {
    token: response.token,
    user: {
      id: String(response.id),
      login: response.login ?? payload.login,
      name: response.nomeCompleto ?? payload.login,
      role,
      roles: normalizedRoles,
      permissions: response.permissions ?? [],
    },
  };
}

export async function login(credentials: LoginPayload) {
  // Assumimos o contrato atual do backend: token + metadados do usuário na própria resposta de /auth/login.
  const response = await unwrapResponse(
    api.post<LoginResponse, LoginPayload>("/auth/login", credentials, withPublicRequest()),
  );

  const session = toSessionData(response, credentials);
  setStoredSession(session);
  return session;
}

export function logout() {
  clearStoredSession({ reason: "manual" });
}

export function getToken() {
  return getAccessToken();
}

export function isAuthenticated() {
  return hasStoredSession();
}

export function getSession() {
  return getStoredSession();
}
