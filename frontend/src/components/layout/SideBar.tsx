import { NavLink } from "react-router-dom";
import { getSessionUser } from "../../auth/session";
import Brand from "./Brand";

type Role = "MEDICO" | "SECRETARIA" | "ADMIN";

type NavItem = {
  to: string;
  label: string;
  roles: Role[];
  end?: boolean;
};

const items: NavItem[] = [
  { to: "/dashboard", label: "Dashboard", roles: ["MEDICO", "SECRETARIA", "ADMIN"], end: true },
  { to: "/agenda", label: "Agenda", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },
  { to: "/pacientes", label: "Pacientes", roles: ["MEDICO", "SECRETARIA", "ADMIN"] },

  { to: "/consultas", label: "Consultas", roles: ["MEDICO"] },
  { to: "/prescricoes", label: "Prescrições", roles: ["MEDICO"] },
    { to: "/pendencias", label: "Pendências", roles: ["MEDICO"] },

  { to: "/confirmacoes", label: "Confirmações", roles: ["SECRETARIA"] },
  { to: "/atendimento", label: "Atendimento", roles: ["SECRETARIA"] },

  { to: "/usuarios", label: "Usuários", roles: ["ADMIN"] },
  { to: "/configuracoes", label: "Configurações", roles: ["ADMIN"] },
];

function formatRole(role: Role) {
  if (role === "MEDICO") return "Médico";
  if (role === "SECRETARIA") return "Secretaria";
  return "Admin";
}

export default function Sidebar() {
  const user = getSessionUser();
  const visible = items.filter((it) => it.roles.includes(user.role));

  return (
    <aside className="mf-sidebar">
      <Brand variant="sidebar" />

      <nav className="mf-nav">
        {visible.map((it) => (
          <NavLink
            key={it.to}
            to={it.to}
            end={it.end}
            className={({ isActive }) => `mf-nav__item ${isActive ? "is-active" : ""}`}
          >
            {it.label}
          </NavLink>
        ))}
      </nav>

      <div className="mf-sidebar__footer">
        <div className="mf-sidebar__footer-name">{user.name}</div>
        <div className="mf-sidebar__footer-meta">
          {formatRole(user.role)} • v1.0.0
        </div>
      </div>
    </aside>
  );
}
