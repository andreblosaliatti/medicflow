export type UserRole = "MEDICO" | "SECRETARIA" | "ADMIN";

export type SessionUser = {
  id: string;
  name: string;
  role: UserRole;
};

export function getSessionUser(): SessionUser {
  // mock por enquanto (depois você troca para JWT / backend)
  return {
    id: "u1",
    name: "Dr. João Carvalho",
    role: "MEDICO",
  };
}
