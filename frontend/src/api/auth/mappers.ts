import type { AuthSessionViewModel, LoginResponse, SessionUserViewModel } from "./types";
import { isUserRole } from "./types";

const ROLE_PRIORITY = ["ADMIN", "MEDICO", "SECRETARIA", "ATENDENTE", "ENFERMEIRO", "PACIENTE"] as const;

function resolveRole(roles: string[]): SessionUserViewModel["role"] {
  const normalized = roles.filter(isUserRole);

  const prioritized = ROLE_PRIORITY.find((role) => normalized.includes(role));
  if (prioritized) return prioritized;

  throw new Error("Usuário autenticado sem perfil compatível com a aplicação.");
}

export function toSessionData(response: LoginResponse): AuthSessionViewModel {
  const normalizedRoles = response.roles.filter(isUserRole);
  const role = resolveRole(response.roles);

  return {
    token: response.token,
    user: {
      id: String(response.id),
      login: response.login,
      name: response.nomeCompleto,
      role,
      roles: normalizedRoles,
      permissions: response.permissions,
    },
  };
}
