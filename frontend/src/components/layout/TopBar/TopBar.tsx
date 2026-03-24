import { useMemo } from "react";

import { useAuth } from "../../../auth/useAuth";
import type { UserRole } from "../../../auth/session";
import "./topbar.css";

function initials(name: string) {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? "")
    .join("");
}

function formatRole(role: UserRole) {
  if (role === "MEDICO") return "Médico";
  if (role === "SECRETARIA") return "Secretaria";
  if (role === "ATENDENTE") return "Atendente";
  if (role === "ENFERMEIRO") return "Enfermeiro";
  if (role === "PACIENTE") return "Paciente";
  return "Administrador";
}

type Props = {
  showSearch?: boolean;
  searchPlaceholder?: string;
};

export default function Topbar({ showSearch = true, searchPlaceholder = "Buscar..." }: Props) {
  const { user, signOut } = useAuth();

  const avatar = useMemo(() => initials(user?.name ?? "Medic Flow"), [user?.name]);

  return (
    <header className="mf-topbar">
      <div className="mf-topbar__left">
        {showSearch && (
          <div className="mf-topbar__search">
            <input placeholder={searchPlaceholder} />
          </div>
        )}
      </div>

      <div className="mf-topbar__right">
        <button className="mf-user" type="button" onClick={() => signOut()}>
          <div className="mf-user__avatar">{avatar}</div>

          <div className="mf-user__info">
            <span className="mf-user__name">{user?.name ?? "Usuário"}</span>
            <span className="mf-user__role">{user ? formatRole(user.role) : "Sessão"}</span>
          </div>

          <span className="mf-user__chevron">Sair</span>
        </button>
      </div>
    </header>
  );
}
