import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuth } from "./useAuth";
import type { UserRole } from "./session";

export default function ProtectedRoute({ allowedRoles }: { allowedRoles?: UserRole[] }) {
  const location = useLocation();
  const { status, user } = useAuth();

  if (status === "loading") {
    return <div className="mf-page-loading">Carregando sessão...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (allowedRoles?.length && !allowedRoles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}
