import { createContext } from "react";

import type { LoginRequest } from "../api/auth/types";
import type { SessionData, SessionUser } from "./session";

export type AuthStatus = "loading" | "authenticated" | "unauthenticated";

export type AuthContextValue = {
  status: AuthStatus;
  session: SessionData | null;
  user: SessionUser | null;
  isAuthenticated: boolean;
  signIn: (credentials: LoginRequest) => Promise<SessionData>;
  signOut: (reason?: "manual" | "unauthorized" | "forbidden" | "invalid") => void;
  refreshSession: () => Promise<SessionData | null>;
};

export const AuthContext = createContext<AuthContextValue | null>(null);
