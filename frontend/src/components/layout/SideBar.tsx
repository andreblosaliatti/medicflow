import { NavLink } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import type { UserRole } from "../../auth/session";
import Brand from "./Brand";

type NavItem = {
  to: string;
  label: string;
  roles: UserRole[];
  end?: boolean;
};

const items: NavItem[] = [
  { to: "/dashboard", label: "Dashboard", roles: ["MEDICO", "SECRETARIA", "ADMIN"], end: true },
  { to: "/agenda", label: "Agenda", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },
  { to: "/pacientes", label: "Pacientes", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },
  { to: "/consultas", label: "Consultas", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },
  { to: "/pendencias", label: "Pendências", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },
];

function formatRole(role: UserRole) {
  if (role === "MEDICO") return "Médico";
  if (role === "SECRETARIA") return "Secretaria";
  if (role === "ATENDENTE") return "Atendente";
  if (role === "ENFERMEIRO") return "Enfermeiro";
  if (role === "PACIENTE") return "Paciente";
  return "Administrador";
}

export default function Sidebar() {
  const { user } = useAuth();

  if (!user) return null;

  const visible = items.filter((item) => item.roles.includes(user.role));

  return (
    <aside className="mf-sidebar">
      <Brand variant="sidebar" />

      <nav className="mf-nav">
        {visible.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `mf-nav__item ${isActive ? "is-active" : ""}`}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="mf-sidebar__footer">
        <div className="mf-sidebar__footer-name">{user.name}</div>
        <div className="mf-sidebar__footer-meta">{formatRole(user.role)} • v1.0.0</div>
      </div>
    </aside>
  );
}
