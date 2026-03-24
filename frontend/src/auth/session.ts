export type UserRole = "ADMIN" | "MEDICO" | "SECRETARIA" | "ATENDENTE" | "ENFERMEIRO" | "PACIENTE";

export type SessionUser = {
  id: string;
  login: string;
  name: string;
  role: UserRole;
  roles: UserRole[];
  permissions: string[];
};

export type SessionData = {
  token: string;
  user: SessionUser;
};

export const SESSION_STORAGE_KEY = "mf_auth_session_v2";
export const POST_LOGIN_REDIRECT_KEY = "mf_post_login_redirect_v1";
const SESSION_CLEARED_EVENT = "mf:session-cleared";

type SessionClearedDetail = {
  reason?: "manual" | "unauthorized" | "forbidden" | "invalid";
  status?: number;
  path?: string;
};

function isUserRole(value: unknown): value is UserRole {
  return value === "ADMIN"
    || value === "MEDICO"
    || value === "SECRETARIA"
    || value === "ATENDENTE"
    || value === "ENFERMEIRO"
    || value === "PACIENTE";
}

function isSessionData(value: unknown): value is SessionData {
  if (!value || typeof value !== "object") return false;

  const session = value as Partial<SessionData>;
  const user = session.user as Partial<SessionUser> | undefined;

  return Boolean(
    typeof session.token === "string" &&
      session.token &&
      user &&
      typeof user.id === "string" &&
      user.id &&
      typeof user.login === "string" &&
      user.login &&
      typeof user.name === "string" &&
      user.name &&
      isUserRole(user.role) &&
      Array.isArray(user.roles) &&
      user.roles.every(isUserRole) &&
      Array.isArray(user.permissions) &&
      user.permissions.every((permission) => typeof permission === "string"),
  );
}

export function getStoredSession(): SessionData | null {
  try {
    const raw = localStorage.getItem(SESSION_STORAGE_KEY);
    if (!raw) return null;

    const parsed = JSON.parse(raw) as unknown;
    return isSessionData(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

export function setStoredSession(session: SessionData) {
  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
}

export function rememberPostLoginRedirect(path: string) {
  sessionStorage.setItem(POST_LOGIN_REDIRECT_KEY, path);
}

export function consumePostLoginRedirect() {
  const path = sessionStorage.getItem(POST_LOGIN_REDIRECT_KEY);
  sessionStorage.removeItem(POST_LOGIN_REDIRECT_KEY);
  return path;
}

export function clearStoredSession(detail?: SessionClearedDetail) {
  localStorage.removeItem(SESSION_STORAGE_KEY);
  window.dispatchEvent(new CustomEvent<SessionClearedDetail>(SESSION_CLEARED_EVENT, { detail }));
}

export function getAccessToken() {
  return getStoredSession()?.token ?? null;
}

export function getSessionUser() {
  return getStoredSession()?.user ?? null;
}

export function hasStoredSession() {
  return Boolean(getAccessToken());
}

export function onSessionCleared(listener: (detail?: SessionClearedDetail) => void) {
  const handler = (event: Event) => {
    listener((event as CustomEvent<SessionClearedDetail>).detail);
  };

  window.addEventListener(SESSION_CLEARED_EVENT, handler);
  return () => window.removeEventListener(SESSION_CLEARED_EVENT, handler);
}
