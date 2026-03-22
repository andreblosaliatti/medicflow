import type { UserRole } from "../../auth/session";

export type LoginRequest = {
  email: string;
  senha: string;
};

export type AuthUserApi = {
  id: string;
  nome: string;
  email: string;
  role: UserRole;
};

export type LoginResponse = {
  token: string;
  usuario: AuthUserApi;
};

export type SessionUserViewModel = {
  id: string;
  name: string;
  email: string;
  role: UserRole;
};
