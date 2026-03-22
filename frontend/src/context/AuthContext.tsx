import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { useNavigate } from "react-router-dom";

import { consumePostLoginRedirect, onSessionCleared, type SessionData } from "../auth/session";
import * as authService from "../services/authService";
import { AuthContext } from "./auth-context";

export type AuthStatus = "loading" | "authenticated" | "unauthenticated";

export type AuthContextValue = {
  status: AuthStatus;
  isLoading: boolean;
  isAuthenticated: boolean;
  session: SessionData | null;
  user: SessionData["user"] | null;
  login: typeof authService.login;
  logout: () => void;
  signIn: typeof authService.login;
  signOut: () => void;
};


export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const [session, setSession] = useState<SessionData | null>(() => authService.getSession());
  const [status, setStatus] = useState<AuthStatus>(() => (authService.isAuthenticated() ? "authenticated" : "unauthenticated"));

  useEffect(() => {
    return onSessionCleared(() => {
      setSession(null);
      setStatus("unauthenticated");
    });
  }, []);

  const login = useCallback<typeof authService.login>(async (credentials) => {
    setStatus("loading");
    const nextSession = await authService.login(credentials);
    setSession(nextSession);
    setStatus("authenticated");
    return nextSession;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setSession(null);
    setStatus("unauthenticated");
    navigate("/login", { replace: true });
  }, [navigate]);

  const value = useMemo<AuthContextValue>(() => ({
    status,
    isLoading: status === "loading",
    isAuthenticated: status === "authenticated",
    session,
    user: session?.user ?? null,
    login,
    logout,
    signIn: login,
    signOut: logout,
  }), [login, logout, session, status]);

  useEffect(() => {
    if (status !== "authenticated") return;

    const redirectPath = consumePostLoginRedirect();
    if (!redirectPath) return;

    navigate(redirectPath, { replace: true });
  }, [navigate, status]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
