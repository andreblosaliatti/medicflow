export type UserRole = "MEDICO" | "SECRETARIA" | "ADMIN";

export type SessionUser = {
  id: string;
  name: string;
  email?: string;
  role: UserRole;
};

const SESSION_STORAGE_KEY = "mf_session_user_v1";

const DEFAULT_SESSION_USER: SessionUser = {
  id: "u1",
  name: "Dr. João Carvalho",
  email: "joao.carvalho@medicflow.app",
  role: "MEDICO",
};

export function getSessionUser(): SessionUser {
  try {
    const raw = localStorage.getItem(SESSION_STORAGE_KEY);
    if (!raw) return DEFAULT_SESSION_USER;

    const parsed = JSON.parse(raw) as SessionUser;
    if (!parsed?.id || !parsed?.name || !parsed?.role) {
      return DEFAULT_SESSION_USER;
    }

    return parsed;
  } catch {
    return DEFAULT_SESSION_USER;
  }
}

export function setSessionUser(user: SessionUser) {
  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(user));
}

export function clearSessionUser() {
  localStorage.removeItem(SESSION_STORAGE_KEY);
}
