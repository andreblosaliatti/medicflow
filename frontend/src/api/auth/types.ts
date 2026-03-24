import type { SessionData, UserRole } from "../../auth/session";

export type LoginRequest = {
  login: string;
  senha: string;
};

export type LoginResponse = {
  id: number;
  login: string;
  nomeCompleto: string;
  roles: string[];
  permissions: string[];
  token: string;
};

export type SessionUserViewModel = SessionData["user"];

export type AuthSessionViewModel = SessionData;

export function isUserRole(value: string): value is UserRole {
  return value === "ADMIN"
    || value === "MEDICO"
    || value === "SECRETARIA"
    || value === "ATENDENTE"
    || value === "ENFERMEIRO"
    || value === "PACIENTE";
}
