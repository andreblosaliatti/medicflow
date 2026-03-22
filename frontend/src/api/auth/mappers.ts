import type { AuthUserApi, SessionUserViewModel } from "./types";

export function toSessionUserViewModel(user: AuthUserApi): SessionUserViewModel {
  return {
    id: user.id,
    name: user.nome,
    email: user.email,
    role: user.role,
  };
}
