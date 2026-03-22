import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import { login, me } from "../api/auth/service";
import type { LoginRequest } from "../api/auth/types";
import { AuthContext, type AuthContextValue } from "./AuthContext";
import {
  clearStoredSession,
  getStoredSession,
  onSessionCleared,
  setStoredSession,
  type SessionData,
} from "./session";

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [status, setStatus] = useState<AuthContextValue["status"]>("loading");
  const [session, setSession] = useState<SessionData | null>(() => getStoredSession());

  const redirectToLogin = useCallback(() => {
    const isAuthPage = location.pathname === "/login" || location.pathname === "/esqueci-senha";
    if (isAuthPage) return;

    navigate("/login", {
      replace: true,
      state: { from: `${location.pathname}${location.search}${location.hash}` },
    });
  }, [location.hash, location.pathname, location.search, navigate]);

  const applySession = useCallback((nextSession: SessionData | null) => {
    setSession(nextSession);
    setStatus(nextSession ? "authenticated" : "unauthenticated");
  }, []);

  const refreshSession = useCallback(async () => {
    const stored = getStoredSession();
    if (!stored?.token) {
      applySession(null);
      return null;
    }

    setStatus("loading");

    try {
      const nextSession = await me();
      setStoredSession(nextSession);
      applySession(nextSession);
      return nextSession;
    } catch {
      clearStoredSession({ reason: "invalid" });
      applySession(null);
      return null;
    }
  }, [applySession]);

  useEffect(() => {
    const timer = window.setTimeout(() => {
      void refreshSession();
    }, 0);

    return () => window.clearTimeout(timer);
  }, [refreshSession]);

  useEffect(() => {
    return onSessionCleared((detail) => {
      applySession(null);
      if (detail?.reason === "manual") {
        navigate("/login", { replace: true });
        return;
      }

      redirectToLogin();
    });
  }, [applySession, navigate, redirectToLogin]);

  const signIn = useCallback(async (credentials: LoginRequest) => {
    setStatus("loading");

    try {
      const loginSession = await login(credentials);
      setStoredSession(loginSession);

      const hydratedSession = await me();
      setStoredSession(hydratedSession);
      applySession(hydratedSession);
      return hydratedSession;
    } catch (error) {
      clearStoredSession({ reason: "invalid" });
      applySession(null);
      throw error;
    }
  }, [applySession]);

  const signOut = useCallback((reason: "manual" | "unauthorized" | "forbidden" | "invalid" = "manual") => {
    clearStoredSession({ reason });
    applySession(null);
  }, [applySession]);

  const value = useMemo<AuthContextValue>(() => ({
    status,
    session,
    user: session?.user ?? null,
    isAuthenticated: status === "authenticated",
    signIn,
    signOut,
    refreshSession,
  }), [refreshSession, session, signIn, signOut, status]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
